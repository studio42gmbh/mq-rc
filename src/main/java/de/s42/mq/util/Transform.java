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
package de.s42.mq.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public final class Transform
{

	protected Matrix4f parentMatrix;
	protected final Matrix4f matrix = new Matrix4f();
	protected final Vector3f position = new Vector3f();
	protected final Vector3f scale = new Vector3f(1.0f);
	protected final Quaternionf rotation = new Quaternionf();
	protected boolean dirty = true;

	public Transform()
	{

	}

	public Transform(Transform other)
	{
		set(other);
	}

	public Transform copy()
	{
		return new Transform(this);
	}

	public void set(Transform other)
	{
		assert other != null;

		parentMatrix = other.parentMatrix;
		matrix.set(other.matrix);
		position.set(other.position);
		scale.set(other.scale);
		rotation.set(other.rotation);
		dirty = other.dirty;
	}

	public void update()
	{
		update(false);
	}

	public void update(boolean force)
	{
		if (force || dirty) {

			matrix.identity();
			matrix.setTranslation(position);
			matrix.rotate(rotation);
			matrix.scale(scale);
			if (parentMatrix != null) {
				matrix.mulLocal(parentMatrix);
			}

			dirty = false;
		}
	}

	public Matrix4f getParentMatrix()
	{
		return parentMatrix;
	}

	public void setParentMatrix(Matrix4f parentMatrix)
	{
		this.parentMatrix = parentMatrix;
		dirty = true;
	}

	public void setFromMatrix(Matrix4f matrix)
	{
		assert matrix != null;

		matrix.getTranslation(position);
		matrix.getScale(scale);
		matrix.getUnnormalizedRotation(rotation);
		dirty = true;
	}

	public Matrix4f getMatrix()
	{
		return matrix;
	}

	public Vector3f getWorldPosition()
	{
		Vector4f wPos = (new Vector4f(0.0f, 0.0f, 0.0f, 1.0f)).mul(matrix);

		return new Vector3f(wPos.x / wPos.w, wPos.y / wPos.w, wPos.z / wPos.w);
	}

	public Vector3f getWorldDirection()
	{
		return (new Vector3f(1.0f, 0.0f, 0.0f)).mulDirection(matrix);
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		assert position != null;

		this.position.set(position);
		dirty = true;
	}

	public Vector3f getScale()
	{
		return scale;
	}

	public void setScale(Vector3f scale)
	{
		assert scale != null;

		this.scale.set(scale);
		dirty = true;
	}

	public Quaternionf getRotation()
	{
		return rotation;
	}

	public void setRotation(Quaternionf rotation)
	{
		assert rotation != null;

		this.rotation.identity().mul(rotation);
		dirty = true;
	}

	public Vector3f getEulerXYZ()
	{
		return rotation.getEulerAnglesXYZ(new Vector3f());
	}

	public void setEulerXYZ(Vector3f euler)
	{
		assert euler != null;

		rotation.identity().rotateXYZ(euler.x, euler.y, euler.z);
		dirty = true;
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}

	public void rotateAroundX(float rotationX)
	{
		rotation.rotateLocalX(rotationX).normalize();
		dirty = true;
	}

	public void rotateAroundY(float rotationY)
	{
		rotation.rotateLocalY(rotationY).normalize();
		dirty = true;
	}

	public void rotateAroundZ(float rotationZ)
	{
		rotation.rotateLocalZ(rotationZ).normalize();
		dirty = true;
	}
}
