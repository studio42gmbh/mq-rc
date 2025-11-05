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
package de.s42.mq.input;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.core.AbstractEntity;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractInputAxis extends AbstractEntity implements InputAxis
{

	@AttributeDL(required = false, defaultValue = "-1.0")
	protected float min;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float max;

	@AttributeDL(required = false, defaultValue = "0.0")
	protected float middle;

	@AttributeDL(required = false, defaultValue = "0.0")
	protected float value;

	@AttributeDL(required = false, defaultValue = "0.001")
	protected float epsilon;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float speed;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean snapToMiddle;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean singleStep;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean timeScaled;

	@AttributeDL(required = false, defaultValue = "0.1")
	protected float response;

	@AttributeDL(required = false, defaultValue = "CLAMP")
	protected InputAxisOverflowMode overflow;

	protected float velocity;
	protected boolean singleStepActive;

	@Override
	public float getMin()
	{
		return min;
	}

	@Override
	public void setMin(float min)
	{
		this.min = min;
	}

	@Override
	public float getMax()
	{
		return max;
	}

	@Override
	public void setMax(float max)
	{
		this.max = max;
	}

	@Override
	public float getMiddle()
	{
		return middle;
	}

	@Override
	public void setMiddle(float middle)
	{
		this.middle = middle;
	}

	@Override
	public float getValue()
	{
		return value;
	}

	@Override
	public void setValue(float value)
	{
		this.value = value;
	}

	public float getEpsilon()
	{
		return epsilon;
	}

	public void setEpsilon(float epsilon)
	{
		this.epsilon = epsilon;
	}

	@Override
	public boolean isValueMin()
	{
		return Math.abs(value - min) < epsilon;
	}

	@Override
	public boolean isValueMax()
	{
		return Math.abs(value - max) < epsilon;
	}

	@Override
	public boolean isValueMiddle()
	{
		return Math.abs(value - middle) < epsilon;
	}

	@Override
	public boolean isSnapToMiddle()
	{
		return snapToMiddle;
	}

	@Override
	public void setSnapToMiddle(boolean snapToMiddle)
	{
		this.snapToMiddle = snapToMiddle;
	}

	@Override
	public boolean isSingleStep()
	{
		return singleStep;
	}

	@Override
	public void setSingleStep(boolean singleStep)
	{
		this.singleStep = singleStep;
	}

	@Override
	public int getIntValue()
	{
		return Math.round(value);
	}

	@Override
	public void setIntValue(int value)
	{
		this.value = value;
	}

	@Override
	public InputAxisOverflowMode getOverflow()
	{
		return overflow;
	}

	@Override
	public void setOverflow(InputAxisOverflowMode overflow)
	{
		this.overflow = overflow;
	}

	@Override
	public boolean isTimeScaled()
	{
		return timeScaled;
	}

	@Override
	public void setTimeScaled(boolean timeScaled)
	{
		this.timeScaled = timeScaled;
	}

	public float getResponse()
	{
		return response;
	}

	public void setResponse(float response)
	{
		this.response = response;
	}

	@Override
	public float getSpeed()
	{
		return speed;
	}

	@Override
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
