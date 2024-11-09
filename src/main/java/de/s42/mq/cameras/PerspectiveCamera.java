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
import de.s42.mq.data.FloatData;

/**
 *
 * @author Benjamin Schiller
 */
public class PerspectiveCamera extends Camera
{

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = InDegreesDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "179.9")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData fov = new FloatData();

	public PerspectiveCamera()
	{
	}

	public PerspectiveCamera(float fov, float near, float far, float aspectRatio)
	{
		super(near, far, aspectRatio);

		this.fov.setFloatValue(fov);
	}

	@Override
	public void update()
	{
		//if (isUpdateProjectionMatrix()) {
		projectionMatrix.setPerspective((float) Math.toRadians(getFov().getFloatValue()), getAspectRatio(), getNear(), getFar());
		viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix);
		setUpdateProjectionMatrix(false);
		//}

		super.update();
	}

	public FloatData getFov()
	{
		return fov;
	}

	public void setFov(FloatData fov)
	{
		assert fov != null;
		assert fov.getFloatValue() > 0.0;

		this.fov = fov;
		setUpdateProjectionMatrix(true);
	}
}
