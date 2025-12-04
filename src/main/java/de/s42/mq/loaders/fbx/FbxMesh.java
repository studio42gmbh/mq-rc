/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.loaders.fbx;

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.Assets;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.ui.editor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

/**
 * See for ref: https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
 * and https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
 *
 * @author Benjamin Schiller
 */
public class FbxMesh extends MeshGroup
{

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private final static Logger log = LogManager.getLogger(FbxMesh.class.getName());

	// Used to filter out default properties from blender export
	protected final static List<String> defaultProperties = Arrays.asList(
		"UserProperties",
		"IsNull",
		"DefaultAttributeIndex",
		"InheritType"
	);

	@AttributeDL(required = true)
	@isFile
	@editor(editable = false)
	protected Path source;

	@AttributeDL(required = false)
	protected Assets materials;

	public FbxMesh()
	{
	}

	public FbxMesh(Path source)
	{
		assert source != null;

		this.source = source;
	}

	@Override
	public FbxMesh copy()
	{
		FbxMesh copy = (FbxMesh) super.copy();

		copy.source = source;

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		assert source != null;

		if (isLoaded()) {
			return;
		}

		long startTime = System.nanoTime();

		/* @todo might wan to check which of these loading flags should be configurable */
		//| aiProcess_FindDegenerates
		//| aiProcess_FindInvalidData
		//| aiProcess_OptimizeGraph
		//| aiProcess_RemoveRedundantMaterials
		//| aiProcess_SortByPType
		//| aiProcess_ForceGenNormals
		//| aiProcess_GenNormals
		//| aiProcess_GenSmoothNormals
		try (AIScene scene = aiImportFile(source.toAbsolutePath().toString(),
			aiProcess_JoinIdenticalVertices
			| aiProcess_Triangulate
			| aiProcess_FlipUVs
			| aiProcess_ImproveCacheLocality
			| aiProcess_GenNormals
			| aiProcess_CalcTangentSpace
			| aiProcess_GenBoundingBoxes
		)) {
			if (scene == null) {
				throw new IllegalStateException(aiGetErrorString());
			}
			if (scene.mMeshes() == null) {
				throw new InvalidInstance("No meshes found in file " + FilesHelper.createMavenNetbeansFileConsoleLink(source));
			}

			AINode node = scene.mRootNode();
			loadMetaData(scene.mMetaData(), this);
			loadNodes(scene, node);
			super.load();

			long duration = (System.nanoTime() - startTime) / 1000000L;

			//logHierarchy();
			log.debug("Loaded " + source.toAbsolutePath() + " with " + scene.mNumMeshes() + " meshes in " + duration + " ms.");
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		super.unload();

		clearMeshes();
	}

	protected Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4)
	{
		Matrix4f result = new Matrix4f();
		result.m00(aiMatrix4x4.a1());
		result.m10(aiMatrix4x4.a2());
		result.m20(aiMatrix4x4.a3());
		result.m30(aiMatrix4x4.a4());
		result.m01(aiMatrix4x4.b1());
		result.m11(aiMatrix4x4.b2());
		result.m21(aiMatrix4x4.b3());
		result.m31(aiMatrix4x4.b4());
		result.m02(aiMatrix4x4.c1());
		result.m12(aiMatrix4x4.c2());
		result.m22(aiMatrix4x4.c3());
		result.m32(aiMatrix4x4.c4());
		result.m03(aiMatrix4x4.d1());
		result.m13(aiMatrix4x4.d2());
		result.m23(aiMatrix4x4.d3());
		result.m33(aiMatrix4x4.d4());

		return result;
	}

	protected Material findMaterial(String materialName)
	{
		Material subMaterial = null;

		if (getMaterials() != null) {
			subMaterial = getMaterials().findMaterial(materialName);
		}

		if (subMaterial == null) {
			subMaterial = getMaterial();
		}

		return subMaterial;
	}

	protected void loadMetaData(AIMetaData metadata, MeshGroup nodeContainer)
	{
		// read metadata for custom properties
		if (metadata != null) {

			log.trace("loadMetaData", nodeContainer.getName());

			for (int c = 0; c < metadata.mNumProperties(); ++c) {

				String key = metadata.mKeys().get(c).dataString();
				AIMetaDataEntry aiMetaDataEntry = metadata.mValues().get(c);

				if (!defaultProperties.contains(key)) {

					switch (aiMetaDataEntry.mType()) {
						case AI_FLOAT -> {
							ByteBuffer buff = aiMetaDataEntry.mData(4);
							float prop = buff.getFloat();
							log.trace("Custom Float Data in " + nodeContainer.getName() + " " + key + " : " + prop);
							nodeContainer.setCustomProperty(key, prop);
						}
						case AI_INT32 -> {
							ByteBuffer buff = aiMetaDataEntry.mData(4);
							int prop = buff.getInt();
							log.trace("Custom Int Data in " + nodeContainer.getName() + " " + key + " : " + prop);
							nodeContainer.setCustomProperty(key, prop);
						}
						case AI_DOUBLE -> {
							ByteBuffer buff = aiMetaDataEntry.mData(8);
							Double prop = buff.getDouble();
							log.trace("Custom Double Data in " + nodeContainer.getName() + " " + key + " : " + prop);
							nodeContainer.setCustomProperty(key, prop);
						}
						case AI_UINT64 -> {
							ByteBuffer buff = aiMetaDataEntry.mData(8);
							long prop = buff.getLong();
							log.trace("Custom Long Data in " + nodeContainer.getName() + " " + key + " : " + prop);
							nodeContainer.setCustomProperty(key, prop);
						}
						case AI_AIVECTOR3D -> {
							ByteBuffer buff = aiMetaDataEntry.mData(24);
							double x = buff.getDouble();
							double y = buff.getDouble();
							double z = buff.getDouble();
							Vector3f prop = new Vector3f((float) x, (float) y, (float) z);
							log.trace("Custom Vector3 Data in " + nodeContainer.getName() + " " + key + " : " + prop);
							nodeContainer.setCustomProperty(key, prop);
						}
						case AI_AISTRING -> {
							ByteBuffer buff = aiMetaDataEntry.mData(8192);
							int len = buff.getInt();
							// @todo why is there an offset of 4? it should directly contain the string ... well works for now
							buff = buff.slice(buff.position() + 4, (int) len);
							String prop = StandardCharsets.UTF_8.decode(buff).toString();
							log.trace("Custom String Data in " + nodeContainer.getName() + " " + key + " : '" + prop + "'");
							nodeContainer.setCustomProperty(key, prop);
						}
						default -> {
							log.warn("Unknown Custom Data in " + nodeContainer.getName() + " " + key + " - aiMetaDataEntry.mType : '" + aiMetaDataEntry.mType() + "'");
						}
					}
				}
			}
		}
	}

	protected final Map<String, FbxSubMesh> lodMeshes = new HashMap<>();

	protected boolean loadSubMeshes(AIScene scene, AINode node, MeshGroup nodeContainer, Matrix4f meshMatrix)
	{
		//log.debug("loadSubMeshes", nodeContainer.getName());

		boolean shallAddContainer = true;

		for (int i = 0; i < node.mNumMeshes(); ++i) {

			shallAddContainer = false;
			@SuppressWarnings("null")
			AIMesh aiMesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));

			String meshName = aiMesh.mName().dataString();

			// Really can it get more annoying ...
			@SuppressWarnings("null")
			AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(aiMesh.mMaterialIndex()));
			AIString materialNameStr = AIString.calloc();
			aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, aiTextureType_NONE, 0, materialNameStr);
			String materialName = materialNameStr.dataString();

			Material subMaterial = findMaterial(materialName.trim());
			FbxSubMesh subMesh = new FbxSubMesh();
			subMesh.setName(meshName);
			subMesh.setAiMesh(aiMesh);
			subMesh.setIdentifier(getIdentifier());
			subMesh.setLayers("opaque");
			subMesh.setMaterial(subMaterial);
			subMesh.setFromMatrix(meshMatrix);

			subMesh.setCustomProperties(nodeContainer.getCustomProperties());

			// layers - Handle custom layers for nodes
			if (nodeContainer.containsCustomProperty("layers")) {
				subMesh.setLayers(((String) nodeContainer.getCustomProperty("layers")).split(","));
			}

			// lod
			if (nodeContainer.containsCustomProperty("lod")) {
				subMesh.setLod((Integer) nodeContainer.getCustomProperty("lod"));
			}

			// lodMinDistance
			if (nodeContainer.containsCustomProperty("lodDistanceMin")) {
				subMesh.setLodDistanceMin((Float) nodeContainer.getCustomProperty("lodDistanceMin"));
			}

			// lodMaxDistance
			if (nodeContainer.containsCustomProperty("lodDistanceMax")) {
				subMesh.setLodDistanceMax((Float) nodeContainer.getCustomProperty("lodDistanceMax"));
			}

			//log.debug("subMesh", subMesh.getName());
			// Handle lod meshing
			if (subMesh.getLod() > -1) {

				String lodMeshId = subMesh.getName().substring(0, subMesh.getName().indexOf('.'));

				FbxSubMesh currentLodMesh = lodMeshes.get(lodMeshId);

				// Found existing lod mesh -> Add this
				if (currentLodMesh != null) {
					currentLodMesh.addLodMesh(subMesh);
					//log.debug("Added lod mesh", currentLodMesh.getName(), subMesh.getName());
					continue;
				}

				// First lod mesh -> Make this the leading lod mesh for this id
				lodMeshes.put(lodMeshId, subMesh);
			}

			nodeContainer.addMesh(subMesh);
			shallAddContainer = true;
		}

		return shallAddContainer;
	}

	protected void loadNodes(AIScene scene, AINode rootNode)
	{
		// Compensate for FBX Scale with 0.01 scale (Default in FBX is Centimeters -> Here Meters)
		loadContainerNode(scene, rootNode, this, new Matrix4f().scale(0.01f));
	}

	protected Matrix4f getOriginTransform(Matrix4f nodeTransform, Matrix4f globalTransform)
	{
		Matrix4f result = new Matrix4f();

		Vector3f pos = nodeTransform.getTranslation(new Vector3f());
		Vector3f scl = globalTransform.getScale(new Vector3f());
		pos = pos.mul(scl);
		result.translate(pos);
		Quaternionf r = nodeTransform.getUnnormalizedRotation(new Quaternionf());
		result.rotate(r);

		return result;
	}

	protected Matrix4f getMeshTransform(Matrix4f nodeTransform, Matrix4f globalTransform, Matrix4f nodeGlobalTransform)
	{
		Matrix4f result = new Matrix4f();

		Matrix4f scaleM = new Matrix4f(nodeGlobalTransform);
		Vector3f scl = scaleM.getScale(new Vector3f());
		result.scale(scl);

		return result;
	}

	protected void loadContainerNode(AIScene scene, AINode node, MeshGroup nodeMeshContainer, Matrix4f globalTransform)
	{
		assert node != null;
		assert nodeMeshContainer != null;

		String nodeName = node.mName().dataString();

		Matrix4f nodeTransform = toMatrix(node.mTransformation());

		Matrix4f nodeGlobalTransform = (new Matrix4f(nodeTransform)).mulLocal(globalTransform);

		// Manages rotations and offsets
		MeshGroup originContainer = new MeshGroup();
		originContainer.setName(nodeName + "Origin");
		Matrix4f originTransform = getOriginTransform(nodeTransform, globalTransform);
		originContainer.setFromMatrix(originTransform);

		// Is an identity node that allowss to manipulate the mesh easily later
		MeshGroup childContainer = new MeshGroup();
		childContainer.setName(nodeName);

		/* Simpler version without Origin container ...
		MeshGroup childContainer = new MeshGroup();
		Matrix4f originTransform = getOriginTransform(nodeTransform, globalTransform);
		childContainer.setFromMatrix(originTransform);
		childContainer.setName(nodeName);
		nodeMeshContainer.addMesh(childContainer);
		 */
		loadMetaData(node.mMetadata(), childContainer);

		// load the meshes into the container
		Matrix4f meshTransform = getMeshTransform(nodeTransform, globalTransform, nodeGlobalTransform);
		boolean shallAddContainer = loadSubMeshes(scene, node, childContainer, meshTransform);

		// Just add control nodes if there is submeshes or further subnodes
		// @todo Improve addedSubMeshes state is very unclear -1 is for Mounts, 0 no meshes (lod), >0 normal submeshes
		if (shallAddContainer || node.mNumChildren() > 0) {
			nodeMeshContainer.addMesh(originContainer);
			originContainer.addMesh(childContainer);
		}

		// scan for futher sub nodes
		for (int i = 0; i < node.mNumChildren(); ++i) {
			@SuppressWarnings("null")
			AINode child = AINode.create(node.mChildren().get(i));

			loadContainerNode(scene, child, childContainer, nodeGlobalTransform);
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public Assets getMaterials()
	{
		return materials;
	}

	public void setMaterials(Assets materials)
	{
		this.materials = materials;
	}
	// </editor-fold>
}
