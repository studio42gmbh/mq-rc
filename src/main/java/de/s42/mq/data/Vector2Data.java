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
package de.s42.mq.data;

import org.joml.Vector2f;

/**
 *
 * @author Benjamin Schiller
 */
public class Vector2Data extends AbstractData<Vector2f>
{

	protected final Vector2f value = new Vector2f();
	protected float min;
	protected float max;
	protected float step;

	public Vector2Data()
	{
	}

	public Vector2Data(Vector2f value)
	{
		assert value != null : "value != null";

		this.value.set(value);
	}

	@Override
	public Vector2f getValue()
	{
		return value;
	}

	@Override
	public void setValue(Vector2f value)
	{
		assert value != null : "value != null";

		if (!this.value.equals(value)) {
			dirty = true;
			this.value.set(value);
		}
	}

	public void setValue(float x, float y)
	{
		if (!this.value.equals(x, y)) {
			dirty = true;
			this.value.set(x, y);
		}
	}

	@Override
	public Class<Vector2f> getDataType()
	{
		return Vector2f.class;
	}

	public float getMin()
	{
		return min;
	}

	public void setMin(float min)
	{
		this.min = min;
	}

	public float getMax()
	{
		return max;
	}

	public void setMax(float max)
	{
		this.max = max;
	}

	public float getStep()
	{
		return step;
	}

	public void setStep(float step)
	{
		this.step = step;
	}
}
