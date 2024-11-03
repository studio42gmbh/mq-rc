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

/**
 *
 * @author Benjamin Schiller
 */
public class FloatData extends AbstractNumberData<Float>
{

	protected float value;
	protected float min;
	protected float max;
	protected float step;

	public FloatData()
	{
	}

	public FloatData(String name)
	{
		super(name);
	}

	public FloatData(float value)
	{
		this.value = value;
	}

	public float getFloatValue()
	{
		return value;
	}

	public void setFloatValue(float value)
	{
		if (this.value != value) {
			dirty = true;
		}
		this.value = value;
	}

	@Override
	public Float getValue()
	{
		return getFloatValue();
	}

	@Override
	public void setValue(Float value)
	{
		assert value != null;

		setFloatValue(value);
	}

	@Override
	public Class<Float> getDataType()
	{
		return Float.class;
	}

	public float add(float add)
	{
		value += add;

		return value;
	}

	@Override
	public Float add(Float add)
	{
		assert add != null;

		value += add;

		return value;
	}

	public float subtract(float sub)
	{
		value -= sub;

		return value;
	}

	@Override
	public Float subtract(Float sub)
	{
		assert sub != null;

		value -= sub;

		return value;
	}

	@Override
	public Float multiply(Float mul)
	{
		assert mul != null;

		value *= mul;

		return value;
	}

	@Override
	public Float divide(Float divide)
	{
		assert divide != null;

		value /= divide;

		return value;
	}

	@Override
	public Float inc()
	{
		value++;

		return value;
	}

	@Override
	public Float dec()
	{
		value--;

		return value;
	}

	@Override
	public Float getMin()
	{
		return min;
	}

	@Override
	public void setMin(Float min)
	{
		this.min = min;
	}

	@Override
	public Float getMax()
	{
		return max;
	}

	@Override
	public void setMax(Float max)
	{
		this.max = max;
	}

	@Override
	public Float getStep()
	{
		return step;
	}

	@Override
	public void setStep(Float step)
	{
		this.step = step;
	}
}
