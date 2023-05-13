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

import de.s42.mq.core.Entity;

/**
 *
 * @author Benjamin Schiller
 */
public interface InputAxis extends Entity
{
	public enum InputAxisOverflowMode
	{
		WRAP, WRAP_ALIGNED, CLAMP
	}

	public boolean isSnapToMiddle();

	public void setSnapToMiddle(boolean snapToMiddle);

	public float getValue();

	public int getIntValue();

	public void setValue(float value);

	public float getMin();

	public void setMin(float min);

	public float getMax();

	public void setMax(float max);

	public float getMiddle();

	public void setMiddle(float middle);

	public float getSpeed();

	public void setSpeed(float speed);

	public boolean isSingleStep();

	public void setSingleStep(boolean singleStep);

	public InputAxisOverflowMode getOverflow();

	public void setOverflow(InputAxisOverflowMode overflow);

	public boolean isTimeScaled();

	public void setTimeScaled(boolean timeScaled);

	public boolean isValueMin();

	public boolean isValueMax();

	public boolean isValueMiddle();
}
