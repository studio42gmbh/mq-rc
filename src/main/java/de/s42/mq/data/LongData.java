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
public class LongData extends AbstractNumberData<Long>
{
	protected long value;
	protected long min;
	protected long max;
	protected long step;

	public LongData()
	{
	}

	public LongData(long value)
	{
		this.value = value;
	}

	public long getLongValue()
	{
		return value;
	}

	public void setLongValue(long value)
	{
		if (this.value != value) {
			dirty = true;
		}
		this.value = value;
	}

	@Override
	public Long getValue()
	{
		return getLongValue();
	}

	@Override
	public void setValue(Long value)
	{
		assert value != null;

		setLongValue(value);
	}

	@Override
	public Class<Long> getDataType()
	{
		return Long.class;
	}

	@Override
	public Long add(Long add)
	{
		assert add != null;

		value += add;

		return value;
	}

	@Override
	public Long subtract(Long sub)
	{
		assert sub != null;

		value -= sub;

		return value;
	}

	@Override
	public Long multiply(Long mul)
	{
		assert mul != null;

		value *= mul;

		return value;
	}

	@Override
	public Long divide(Long divide)
	{
		assert divide != null;

		value /= divide;

		return value;
	}

	@Override
	public Long inc()
	{
		value++;

		return value;
	}

	@Override
	public Long dec()
	{
		value--;

		return value;
	}

	@Override
	public Long getMin()
	{
		return min;
	}

	@Override
	public void setMin(Long min)
	{
		this.min = min;
	}

	@Override
	public Long getMax()
	{
		return max;
	}

	@Override
	public void setMax(Long max)
	{
		this.max = max;
	}

	@Override
	public Long getStep()
	{
		return step;
	}

	@Override
	public void setStep(Long step)
	{
		this.step = step;
	}

}
