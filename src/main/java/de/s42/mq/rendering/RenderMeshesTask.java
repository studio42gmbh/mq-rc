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
package de.s42.mq.rendering;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.collision.Collider;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.loaders.fbx.FbxSubMesh;
import de.s42.mq.loaders.fbx.FbxSubMesh.InstanceData;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.shaders.Shader.CullType;
import de.s42.mq.ui.AbstractWindowTask;
import de.s42.mq.ui.UIComponent;
import de.s42.mq.ui.layout.Layout;
import java.util.*;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class RenderMeshesTask extends AbstractWindowTask
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(RenderMeshesTask.class.getName());

	@AttributeDL(required = true)
	protected MeshGroup meshes;

	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = "window")
	protected FrameBuffer buffer;

	@AttributeDL(required = false)
	protected Camera camera;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean clipMeshes = false;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean lodMeshes = true;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float lodMeshDistanceScale = 1.0f;

	@AttributeDL(required = false)
	protected String[] layers;

	@AttributeDL(required = false)
	protected Material overrideMaterial;

	@AttributeDL(required = false)
	protected CullType overrideCullType;

	protected FloatData totalTime = new FloatData();
	protected FloatData deltaTime = new FloatData();
	protected IntegerData tick = new IntegerData();
	protected Camera shadowCamera;
	protected FXBuffer shadowBuffer;

	protected final static List<InstanceData> INSTANCE_DATA = new ArrayList<>();

	@Override
	protected void runTaskFirstTime()
	{
		try {
			assert meshes != null;

			meshes.load();

			if (buffer != null) {
				buffer.load();
			}

			if (camera != null) {
				camera.load();
			}

			if (overrideMaterial != null) {
				overrideMaterial.load();
			}

		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected final static Map<String, List<Mesh>> meshesCache = new HashMap<>();

	public final static void clearMeshesCache()
	{
		meshesCache.clear();
	}

	@Override
	@SuppressWarnings("null")
	protected void runTask()
	{
		//log.debug("runTask");

		// render main scene into gbuffer
		if (buffer != null) {
			buffer.startRender();
		} // reset to screen size
		else {
			window.setRenderViewportToWindow();
		}

		if (camera != null) {

			camera.update();

			if (overrideMaterial != null) {
				overrideMaterial.setCamera(camera);
			}
		}

		DefaultRenderContext context = new DefaultRenderContext();
		context.setOverrideCullType(overrideCullType);
		context.setOverrideMaterial(overrideMaterial);
		context.setTick(tick.getIntegerValue());
		context.setDeltaTime(deltaTime.getFloatValue());
		context.setTotalTime(totalTime.getFloatValue());
		context.setShadowCamera(shadowCamera);
		if (shadowBuffer != null) {
			context.setShadowTexture(shadowBuffer.getTexture());
		}

		String cacheKey = "" + meshes.hashCode() + "" + layers[0];

		List<Mesh> ms = meshesCache.get(cacheKey);

		if (ms == null) {

			List l = Arrays.asList(layers);

			ms = meshes.findMeshesByFilter((mesh) -> {
				return mesh != null
					&& mesh.isEnabled()
					&& !(mesh instanceof MeshGroup)
					&& mesh.containsLayers(l);
			});

			meshesCache.put(cacheKey, ms);
		}

		Matrix4f viewProjection = camera.getViewProjectionMatrix();

		// Update UI
		// @todo make layouting more stable to not require it when culling (currently once it clips it remains disappeared)
		for (Mesh m : ms) {
			if (m instanceof UIComponent uiComponent) {
				Layout lay = uiComponent.getLayout();
				if (lay != null) {
					lay.layout(m, uiComponent.getLayoutOptions());
				}
			}
		}

		Map<Integer, List<FbxSubMesh>> meshesByVao = new HashMap<>();

		FrustumIntersection intersection = new FrustumIntersection(viewProjection, true);

		Vector3f cameraPosition = camera.getWorldPosition();

		for (Mesh m : ms) {

			// Cull mesh lod wich is not in range
			if (lodMeshes && m.getLod() > -1) {

				float distance = (new Vector3f(cameraPosition)).sub(m.getWorldPosition()).length();

				m = m.getLodMesh(distance);

				if (m == null) {
					continue;
				}
			}

			// Frustum cull meshes
			if (clipMeshes) {
				Collider collider = m.getBoundsCollider();

				if (!collider.intersectsFrustum(intersection)) {
					continue;
				}
			}

			// Dont render but gather the fbx sub meshes for later instanced rendering
			if (m instanceof FbxSubMesh fbxSubMesh
				&& (fbxSubMesh.getInstanceCount() == 1
				|| fbxSubMesh.containsLayer("instanceSpawn"))) {

				int vao = fbxSubMesh.getVao();

				if (!meshesByVao.containsKey(vao)) {
					meshesByVao.put(vao, new ArrayList<>());
				}

				meshesByVao.get(vao).add(fbxSubMesh);

				continue;
			}

			//log.debug("Mesh", m.getName());
			m.setCamera(camera);
			m.render(context);
		}

		// Render fbx sub meshes instanced
		// @todo Optimize handling of instaning spawner meshes - currently can cause a lot of mem usage if spawner changes often (culling ...)
		for (Map.Entry<Integer, List<FbxSubMesh>> entry : meshesByVao.entrySet()) {

			List<FbxSubMesh> fbxMeshes = entry.getValue();

			FbxSubMesh fbxSubMesh = fbxMeshes.getFirst();

			INSTANCE_DATA.clear();
			for (int i = 0; i < fbxMeshes.size(); ++i) {

				FbxSubMesh fbxSM = fbxMeshes.get(i);
				InstanceData iData = new FbxSubMesh.InstanceData();

				iData.identifier = fbxSM.getIdentifier();
				iData.tint = MQColor.White;
				iData.matrix = fbxSM.getTransform().getMatrix().get4x3(new Matrix4x3f());

				INSTANCE_DATA.add(iData);
			}

			fbxSubMesh.addLayer("instanceSpawn");
			fbxSubMesh.updateInstanceData(INSTANCE_DATA);

			// Small workaround to create all instances relative to world origin
			fbxSubMesh.getTransform().getMatrix().identity();

			fbxSubMesh.setCamera(camera);
			fbxSubMesh.render(context);

			fbxSubMesh.getTransform().update(true);
		}

		if (buffer != null) {
			buffer.endRender();
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public MeshGroup getMeshes()
	{
		return meshes;
	}

	public void setMeshes(MeshGroup meshes)
	{
		this.meshes = meshes;
	}

	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	public String[] getLayers()
	{
		return layers;
	}

	public void setLayers(String[] layers)
	{
		this.layers = layers;
	}

	public Material getOverrideMaterial()
	{
		return overrideMaterial;
	}

	public void setOverrideMaterial(Material overrideMaterial)
	{
		this.overrideMaterial = overrideMaterial;
	}

	public FloatData getTotalTime()
	{
		return totalTime;
	}

	public void setTotalTime(FloatData totalTime)
	{
		this.totalTime = totalTime;
	}

	public FloatData getDeltaTime()
	{
		return deltaTime;
	}

	public void setDeltaTime(FloatData deltaTime)
	{
		this.deltaTime = deltaTime;
	}

	public IntegerData getTick()
	{
		return tick;
	}

	public void setTick(IntegerData tick)
	{
		this.tick = tick;
	}

	public Camera getShadowCamera()
	{
		return shadowCamera;
	}

	public void setShadowCamera(Camera shadowCamera)
	{
		this.shadowCamera = shadowCamera;
	}

	public FXBuffer getShadowBuffer()
	{
		return shadowBuffer;
	}

	public void setShadowBuffer(FXBuffer shadowBuffer)
	{
		this.shadowBuffer = shadowBuffer;
	}

	public CullType getOverrideCullType()
	{
		return overrideCullType;
	}

	public void setOverrideCullType(CullType overrideCullType)
	{
		this.overrideCullType = overrideCullType;
	}

	public boolean isClipMeshes()
	{
		return clipMeshes;
	}

	public void setClipMeshes(boolean clipMeshes)
	{
		this.clipMeshes = clipMeshes;
	}

	public boolean isLodMeshes()
	{
		return lodMeshes;
	}

	public void setLodMeshes(boolean lodMeshes)
	{
		this.lodMeshes = lodMeshes;
	}

	public float getLodMeshDistanceScale()
	{
		return lodMeshDistanceScale;
	}

	public void setLodMeshDistanceScale(float lodMeshDistanceScale)
	{
		this.lodMeshDistanceScale = lodMeshDistanceScale;
	}
	// "Getters/Setters" </editor-fold>
}
