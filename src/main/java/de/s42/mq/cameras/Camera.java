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
package de.s42.mq.cameras;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.FloatData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class Camera extends AbstractAsset
{

	@AttributeDL(required = false, defaultValue = "0.1")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float near = 0.1f;

	@AttributeDL(required = false, defaultValue = "10.0")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float far = 10.0f;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float aspectRatio = 1.0f;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData exposure = new FloatData();

	protected final Matrix4f projectionMatrix = new Matrix4f();
	protected final Matrix4f viewMatrix = new Matrix4f();
	protected final Matrix4f viewProjectionMatrix = new Matrix4f();
	protected final Vector3f cameraPosition = new Vector3f();
	protected final Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
	protected final Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
	protected boolean updateProjectionMatrix = true;
	protected boolean updateViewMatrix = true;

	public Camera()
	{
	}

	public Camera(float near, float far, float aspectRatio)
	{
		this.near = near;
		this.far = far;
		this.aspectRatio = aspectRatio;
	}

	public void update()
	{
		if (isUpdateViewMatrix()) {
			viewMatrix.setLookAt(cameraPosition, cameraLookAt, cameraUp);
			viewProjectionMatrix.set(viewMatrix).mul(projectionMatrix);
			setUpdateViewMatrix(false);
		}
	}

	public Vector3f toScreenPosition(Vector3f worldPosition)
	{
		Vector3f screenPosition = new Vector3f(worldPosition);

		return screenPosition.mulPosition(viewProjectionMatrix);
	}

	public void rotateAroundLookAt(float roationY, float distance, float y)
	{
		assert distance > 0.0;

		setPosition(new Vector3f(
			(float) Math.sin(roationY) * distance + cameraLookAt.x,
			(float) y + cameraLookAt.y,
			(float) Math.cos(roationY) * distance + cameraLookAt.z
		));
	}

	public Vector3f getPosition()
	{
		return new Vector3f(getCameraPosition());
	}

	public Vector3f getWorldPosition()
	{
		return getPosition();
	}

	public Vector3f getLookAt()
	{
		return new Vector3f(getCameraLookAt());
	}

	public Vector3f getForwardDirection()
	{
		Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f);
		return forward.mulDirection(new Matrix4f(viewMatrix).invert());
	}

	public Vector3f getRightDirection()
	{
		Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
		return right.mulDirection(new Matrix4f(viewMatrix).invert());
	}

	public void setLookAt(Vector3f lookAt)
	{
		getCameraLookAt().set(lookAt);
		setUpdateViewMatrix(true);
	}

	public void setPosition(Vector3f position)
	{
		getCameraPosition().set(position);
		setUpdateViewMatrix(true);
	}

	public boolean isUpdateProjectionMatrix()
	{
		return updateProjectionMatrix;
	}

	public void setUpdateProjectionMatrix(boolean updateProjectionMatrix)
	{
		this.updateProjectionMatrix = updateProjectionMatrix;
	}

	public float getNear()
	{
		return near;
	}

	public void setNear(float near)
	{
		assert near > 0.0;

		this.near = near;
		setUpdateProjectionMatrix(true);
	}

	public float getFar()
	{
		return far;
	}

	public void setFar(float far)
	{
		assert far > 0.0;

		this.far = far;
		setUpdateProjectionMatrix(true);
	}

	public float getAspectRatio()
	{
		return aspectRatio;
	}

	public void setAspectRatio(float aspectRatio)
	{
		assert aspectRatio > 0.0;

		this.aspectRatio = aspectRatio;
		setUpdateProjectionMatrix(true);
	}

	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix()
	{
		return viewMatrix;
	}

	public Matrix4f getViewProjectionMatrix()
	{
		return viewProjectionMatrix;
	}

	public Vector3f getCameraPosition()
	{
		return cameraPosition;
	}

	public Vector3f getCameraLookAt()
	{
		return cameraLookAt;
	}

	public Vector3f getCameraUp()
	{
		return cameraUp;
	}

	public boolean isUpdateViewMatrix()
	{
		return updateViewMatrix;
	}

	public void setUpdateViewMatrix(boolean updateViewMatrix)
	{
		this.updateViewMatrix = updateViewMatrix;
	}

	public FloatData getExposure()
	{
		return exposure;
	}

	public void setExposure(FloatData exposure)
	{
		this.exposure = exposure;
	}
}
