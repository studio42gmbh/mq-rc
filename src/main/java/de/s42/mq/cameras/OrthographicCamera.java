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

/**
 *
 * @author Benjamin Schiller
 */
public class OrthographicCamera extends Camera
{

	@AttributeDL(required = false, defaultValue = "-1.0")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float left = -1.0f;

	@AttributeDL(required = false, defaultValue = "1.0")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float right = 1.0f;

	@AttributeDL(required = false, defaultValue = "-1.0")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float bottom = -1.0f;

	@AttributeDL(required = false, defaultValue = "1.0")
	//@AnnotationDL(value = InWorldUnitsDLAnnotation.DEFAULT_SYMBOL)
	protected float top = 1.0f;

	public OrthographicCamera()
	{

	}

	public OrthographicCamera(float left, float right, float bottom, float top, float near, float far, float aspectRatio)
	{
		super(near, far, aspectRatio);

		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
	}

	@Override
	public void update()
	{
		if (isUpdateProjectionMatrix()) {
			projectionMatrix.setOrtho(left, right, bottom, top, near, far);
			setUpdateProjectionMatrix(false);
		}

		super.update();
	}

	public float getLeft()
	{
		return left;
	}

	public void setLeft(float left)
	{
		this.left = left;
		setUpdateProjectionMatrix(true);
	}

	public float getRight()
	{
		return right;
	}

	public void setRight(float right)
	{
		this.right = right;
		setUpdateProjectionMatrix(true);
	}

	public float getBottom()
	{
		return bottom;
	}

	public void setBottom(float bottom)
	{
		this.bottom = bottom;
		setUpdateProjectionMatrix(true);
	}

	public float getTop()
	{
		return top;
	}

	public void setTop(float top)
	{
		this.top = top;
		setUpdateProjectionMatrix(true);
	}
}
