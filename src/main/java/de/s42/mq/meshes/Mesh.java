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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DLContainer;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.cameras.Camera;
import de.s42.mq.core.Copyable;
import de.s42.mq.materials.Material;
import de.s42.mq.util.Transform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 * @param <ChildType>
 */
public abstract class Mesh<ChildType extends Object> extends AbstractAsset implements DLContainer<ChildType>, Copyable
{

	//private final static Logger log = LogManager.getLogger(Mesh.class.getName());
	@AttributeDL(required = false, defaultValue = "false")
	protected boolean flipNormals = false;

	@AttributeDL(required = false)
	protected Material material;

	@AttributeDL(required = false, defaultValue = "-1")
	protected int identifier = -1;

	@AttributeDL(required = false)
	protected String[] layers;

	@AttributeDL(ignore = true)
	protected final Transform transform = new Transform();

	@AttributeDL(ignore = true)
	protected final List<MeshAnimation> animations = new ArrayList<>();

	@AttributeDL(ignore = true)
	protected final List<String> layersAsList = new ArrayList();

	@AttributeDL(ignore = true)
	protected Camera camera;

	@AttributeDL(ignore = true)
	protected MeshGroup parent;

	public abstract void render();

	@Override
	public Mesh copy()
	{
		try {
			Mesh copy = getClass().getConstructor().newInstance();

			// dont copy parent - leads to strange trees which are not consistent when iterating
			copy.name = name;
			copy.loaded = loaded;
			copy.transform.set(transform);
			copy.flipNormals = flipNormals;
			copy.material = material;
			copy.identifier = identifier;
			copy.layers = layers;
			copy.layersAsList.addAll(layersAsList);

			for (MeshAnimation animation : animations) {
				copy.animations.add(animation.copy());
			}

			return copy;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new RuntimeException("Error copying mesh " + ex.getMessage(), ex);
		}
	}

	@Override
	public void update(float elapsedTime)
	{
		for (MeshAnimation animation : animations) {
			animation.update(this, elapsedTime);
		}
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		if (material != null) {
			material.load();
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		super.unload();
	}

	public Matrix4f getModelMatrix()
	{
		return transform.getMatrix();
	}

	public void updateModelMatrix()
	{
		updateModelMatrix(false);
	}

	public void updateModelMatrix(boolean force)
	{
		transform.update(force);
	}

	public void setFromMatrix(Matrix4f matrix)
	{
		transform.setFromMatrix(matrix);
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;

		if (material != null) {
			material.setCamera(camera);
		}
	}

	public Camera getCamera()
	{
		return camera;
	}

	public Vector3f getPosition()
	{
		return transform.getPosition();
	}

	public void setPosition(Vector3f position)
	{
		transform.setPosition(position);
	}

	public Vector3f getScale()
	{
		return transform.getScale();
	}

	public void setScale(Vector3f scale)
	{
		transform.setScale(scale);
	}

	public Quaternionf getRotation()
	{
		return transform.getRotation();
	}

	public void setRotation(Quaternionf rotation)
	{
		transform.setRotation(rotation);
	}

	public boolean isFlipNormals()
	{
		return flipNormals;
	}

	public void setFlipNormals(boolean flipNormals)
	{
		this.flipNormals = flipNormals;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public int getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(int identifier)
	{
		this.identifier = identifier;
	}

	public void addAnimation(MeshAnimation animation)
	{
		animations.add(animation);
	}

	public void removeAnimation(MeshAnimation animation)
	{
		animations.remove(animation);
	}

	public List<MeshAnimation> getAnimations()
	{
		return animations;
	}

	@Override
	public void addChild(String name, ChildType child)
	{
		if (child instanceof MeshAnimation meshAnimation) {
			addAnimation(meshAnimation);
		}
	}

	@Override
	public List<ChildType> getChildren()
	{
		return (List<ChildType>) Collections.unmodifiableList(animations);
	}

	public Matrix4f getParentMatrix()
	{
		return transform.getParentMatrix();
	}

	public void setParentMatrix(Matrix4f parentMatrix)
	{
		transform.setParentMatrix(parentMatrix);
	}

	public boolean isModelMatrixDirty()
	{
		return transform.isDirty();
	}

	public void setModelMatrixDirty(boolean modelMatrixDirty)
	{
		transform.setDirty(modelMatrixDirty);
	}

	public String[] getLayers()
	{
		return layers;
	}

	public void setLayers(String... layers)
	{
		this.layers = layers;
		layersAsList.clear();

		if (layers != null) {
			layersAsList.addAll(Arrays.asList(layers));
		}
	}

	public List<String> getLayersAsList()
	{
		return layersAsList;
	}

	public Vector3f getEuler()
	{
		return transform.getEulerXYZ();
	}

	public void setEuler(Vector3f euler)
	{
		transform.setEulerXYZ(euler);
	}

	public boolean containsLayer(String layer)
	{
		if (layersAsList.isEmpty()) {
			return false;
		}

		return layersAsList.contains(layer);
	}

	public boolean containsLayers(List<String> layers)
	{
		// both empty -> true
		if ((layers == null || layers.isEmpty())
			&& layersAsList.isEmpty()) {
			return true;
		}

		if (layersAsList.isEmpty()) {
			return false;
		}

		// @todo optimize comparing the layer lists possible?
		return !Collections.disjoint(layers, layersAsList);
	}

	@Override
	public String toString()
	{
		return getName() + ":" + getClass().getName() + " scale: " + getScale() + " position: " + getPosition() + " euler: " + getEuler();
	}

	public Transform getTransform()
	{
		return transform;
	}

	public void setTransform(Transform transform)
	{
		assert transform != null;

		this.transform.set(transform);
	}

	public MeshGroup getParent()
	{
		return parent;
	}

	public void setParent(MeshGroup parent)
	{
		this.parent = parent;

		if (parent != null) {
			setParentMatrix(parent.getModelMatrix());
			setCamera(parent.getCamera());
		} else {
			setParentMatrix(null);
			setCamera(null);
		}
	}
}
