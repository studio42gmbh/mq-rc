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

import de.s42.base.conversion.ConversionHelper;

/**
 *
 * @author Benjamin Schiller
 */
public class IntegerData extends AbstractNumberData<Integer>
{

	protected int value;
	protected int min;
	protected int max;
	protected int step;

	static {

		ConversionHelper.addConverter(
			String.class, IntegerData.class,
			(source) -> {
				return new IntegerData(Integer.parseInt(source));
			});
	}

	public IntegerData()
	{
	}

	public IntegerData(int value)
	{
		this.value = value;
	}

	public int getIntegerValue()
	{
		return value;
	}

	public void setIntegerValue(int value)
	{
		if (this.value != value) {
			dirty = true;
		}
		this.value = value;
	}

	@Override
	public Integer getValue()
	{
		return getIntegerValue();
	}

	@Override
	public void setValue(Integer value)
	{
		assert value != null;

		setIntegerValue(value);
	}

	@Override
	public Class<Integer> getDataType()
	{
		return Integer.class;
	}

	@Override
	public Integer add(Integer add)
	{
		assert add != null;

		value += add;

		return value;
	}

	@Override
	public Integer subtract(Integer sub)
	{
		assert sub != null;

		value -= sub;

		return value;
	}

	@Override
	public Integer multiply(Integer mul)
	{
		assert mul != null;

		value *= mul;

		return value;
	}

	@Override
	public Integer divide(Integer divide)
	{
		assert divide != null;

		value /= divide;

		return value;
	}

	@Override
	public Integer inc()
	{
		value++;

		return value;
	}

	@Override
	public Integer dec()
	{
		value--;

		return value;
	}

	@Override
	public Integer getMin()
	{
		return min;
	}

	@Override
	public void setMin(Integer min)
	{
		this.min = min;
	}

	@Override
	public Integer getMax()
	{
		return max;
	}

	@Override
	public void setMax(Integer max)
	{
		this.max = max;
	}

	@Override
	public Integer getStep()
	{
		return step;
	}

	@Override
	public void setStep(Integer step)
	{
		this.step = step;
	}

}
