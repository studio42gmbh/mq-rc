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
package de.s42.mq.meshes;

import de.s42.base.collections.ListHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.BooleanData;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.editor;
import java.util.*;
import java.util.function.Predicate;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class MeshGroup extends Mesh
{

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private final static Logger log = LogManager.getLogger(MeshGroup.class.getName());

	@AttributeDL(required = false)
	@editor
	protected BooleanData enabled = new BooleanData(true);

	protected final List<Mesh> meshes = Collections.synchronizedList(new ArrayList());
	protected final Map<String, Object> customProperties = new HashMap<>();

	@Override
	public MeshGroup copy()
	{
		MeshGroup copy = (MeshGroup) super.copy();

		copy.enabled = new BooleanData(enabled.getBooleanValue());
		copy.layers = layers;
		copy.customProperties.putAll(customProperties);

		for (Mesh mesh : meshes) {
			copy.addMesh(mesh.copy());
		}

		return copy;
	}

	@Override
	public void update(float elapsedTime)
	{
		assert enabled != null;

		if (!enabled.getBooleanValue()) {
			return;
		}

		super.update(elapsedTime);

		for (Mesh mesh : meshes) {
			mesh.update(elapsedTime);
		}
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		loadMeshes();
	}

	public void loadMeshes() throws DLException
	{
		for (Mesh mesh : meshes) {
			mesh.load();
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		unloadMeshes();

		super.unload();
	}

	public void unloadMeshes() throws DLException
	{
		for (Mesh mesh : meshes) {
			mesh.unload();
		}
	}

	@Override
	public void render(RenderContext context)
	{
		assert context != null : "context != null";
		assert enabled != null;
		assert isLoaded();
		assert getCamera() != null;

		if (!enabled.getBooleanValue()) {
			return;
		}

		updateModelMatrix();

		for (Mesh mesh : meshes) {
			renderSubMesh(mesh, context);
		}
	}

	public boolean shallBeRendered(Mesh mesh)
	{
		return mesh.containsLayers(getLayersAsList());
	}

	protected void renderSubMesh(Mesh mesh, RenderContext context)
	{
		assert mesh != null;

		mesh.setCamera(getCamera());

		if (mesh.getMaterial() != null) {
			mesh.getMaterial().beforeRendering(context);
		}

		if (mesh instanceof MeshGroup meshGroup) {
			// @todo: should i preserve the layers of contained meshgroups?
			meshGroup.setLayers(getLayers());
			meshGroup.render(context);
		} else if (shallBeRendered(mesh)) {
			mesh.render(context);
		}

		if (mesh.getMaterial() != null) {
			mesh.getMaterial().afterRendering(context);
		}
	}

	public void addMesh(Mesh mesh)
	{
		assert mesh != null;

		mesh.setParent(this);
		meshes.add(mesh);
	}

	public void removeMesh(Mesh mesh)
	{
		assert mesh != null;

		mesh.setParent(null);
		meshes.remove(mesh);
	}

	public void clearMeshes()
	{
		meshes.clear();
	}

	public void unloadAndClearMeshes() throws DLException
	{
		unloadMeshes();
		clearMeshes();
	}

	@Override
	public void addChild(String name, Object child)
	{
		switch (child) {
			case Mesh mesh ->
				addMesh(mesh);
			case MeshReference meshReference ->
				addMesh(meshReference.getMesh());
			default -> {
			}
		}

		super.addChild(name, child);
	}

	@Override
	public List getChildren()
	{
		return Collections.unmodifiableList(ListHelper.concatenate(meshes, animations));
	}

	public List<Mesh> getMeshes()
	{
		return meshes;
	}

	@SuppressWarnings("null")
	public <MeshType extends Mesh> MeshType findMesh(String name)
	{
		assert name != null;

		for (Mesh mesh : meshes) {

			if (name.equals(mesh.getName())) {
				return (MeshType) mesh;
			}

			if (mesh instanceof MeshGroup meshGroup) {
				Mesh sub = meshGroup.findMesh(name);
				if (sub != null) {
					return (MeshType) sub;
				}
			}
		}

		return null;
	}

	public <MeshType extends Mesh> List<MeshType> findMeshes(String... names)
	{
		List<MeshType> result = new ArrayList<>();

		if (names != null) {
			for (String _name : names) {
				Mesh child = findMesh(_name);
				if (child != null) {
					result.add((MeshType) child);
				}
			}
		}

		return result;
	}

	protected <MeshType extends Mesh> void findMeshesOfType(Class<MeshType> type, List<MeshType> result)
	{
		for (Mesh mesh : meshes) {

			if (type.isAssignableFrom(mesh.getClass())) {
				result.add((MeshType) mesh);
			}

			if (mesh instanceof MeshGroup meshGroup) {
				meshGroup.findMeshesOfType(type, result);
			}
		}
	}

	public <MeshType extends Mesh> List<MeshType> findMeshesOfType(Class<MeshType> type)
	{
		List<MeshType> result = new ArrayList<>();

		findMeshesOfType(type, result);

		return result;
	}

	protected List<Mesh> findMeshesByFilter(Predicate<Mesh> filter, List<Mesh> result)
	{
		for (Mesh mesh : meshes) {

			if (filter.test(mesh)) {
				result.add(mesh);
			}

			if (mesh instanceof MeshGroup meshGroup) {
				meshGroup.findMeshesByFilter(filter, result);
			}
		}

		return result;
	}

	public List<Mesh> findMeshesByFilter(Predicate<Mesh> filter)
	{
		List<Mesh> result = new ArrayList<>();

		findMeshesByFilter(filter, result);

		return result;
	}

	@Override
	public void setCamera(Camera camera)
	{
		super.setCamera(camera);

		for (Mesh mesh : meshes) {
			mesh.setCamera(camera);
		}
	}

	@Override
	public void updateModelMatrix()
	{
		super.updateModelMatrix();

		for (Mesh mesh : meshes) {
			mesh.updateModelMatrix();
		}
	}

	@Override
	public void setModelMatrixDirty(boolean modelMatrixDirty)
	{
		super.setModelMatrixDirty(modelMatrixDirty);

		for (Mesh mesh : meshes) {
			mesh.setModelMatrixDirty(modelMatrixDirty);
		}
	}

	@Override
	public void setPosition(Vector3f position)
	{
		super.setPosition(position);
		setModelMatrixDirty(true);
	}

	@Override
	public void setRotation(Quaternionf rotation)
	{
		super.setRotation(rotation);
		setModelMatrixDirty(true);
	}

	@Override
	public void setScale(Vector3f scale)
	{
		super.setScale(scale);
		setModelMatrixDirty(true);
	}

	@Override
	public void setIdentifier(int identifier)
	{
		super.setIdentifier(identifier);

		for (Mesh mesh : meshes) {
			mesh.setIdentifier(identifier);
		}
	}

	public BooleanData getEnabled()
	{
		return enabled;
	}

	public void setEnabled(BooleanData enabled)
	{
		this.enabled = enabled;
	}

	protected void logHierarchy(Mesh mesh, int indent)
	{
		String indentStr = "";

		for (int i = 0; i < indent; ++i) {
			indentStr += "\t";
		}

		log.debug(indentStr + " " + mesh);

		if (mesh instanceof MeshGroup meshGroup) {
			for (Mesh child : (List<Mesh>) meshGroup.getMeshes()) {
				logHierarchy(child, indent + 1);
			}
		}
	}

	public void logHierarchy()
	{
		logHierarchy(this, 0);
	}

	public Map<String, Object> getCustomProperties()
	{
		return customProperties;
	}

	public <PropertyType extends Object> PropertyType getCustomProperty(String name)
	{
		return (PropertyType) customProperties.get(name);
	}

	public void setCustomProperty(String name, Object value)
	{
		customProperties.put(name, value);
	}

	public boolean containsCustomProperty(String name)
	{
		return customProperties.containsKey(name);
	}

	/**
	 * @todo This is not nice atm - will have to change it being handled in the render call ...
	 * @param layers
	 */
	public void setLayersDeep(String... layers)
	{
		super.setLayers(layers);

		for (Mesh mesh : meshes) {
			if (mesh instanceof MeshGroup meshGroup) {
				meshGroup.setLayersDeep(layers);
			} else if (mesh != null) {
				mesh.setLayers(layers);
			}
		}
	}
}
