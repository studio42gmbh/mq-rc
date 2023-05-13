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

import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class Vector3Data extends AbstractData<Vector3f>
{
	protected final Vector3f value = new Vector3f();
	protected float min;
	protected float max;
	protected float step;

	public Vector3Data()
	{
	}

	public Vector3Data(Vector3f value)
	{
		this.value.set(value);
	}

	@Override
	public Vector3f getValue()
	{
		return value;
	}

	@Override
	public void setValue(Vector3f value)
	{
		assert value != null;

		if (!this.value.equals(value)) {
			dirty = true;
			this.value.set(value);
		}
	}

	public void setValue(float s)
	{
		setValue(s, s, s);
	}

	public void setValue(float x, float y)
	{
		setValue(x, y, 0.0f);
	}

	public void setValue(float x, float y, float z)
	{
		if (!this.value.equals(x, y, z)) {
			dirty = true;
			this.value.set(x, y, z);
		}
	}

	@Override
	public Class<Vector3f> getDataType()
	{
		return Vector3f.class;
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
