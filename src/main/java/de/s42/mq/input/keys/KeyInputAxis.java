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
package de.s42.mq.input.keys;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.input.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author Benjamin Schiller
 */
public class KeyInputAxis extends AbstractInputAxis implements InputHandler, KeyInputHandler
{
	@AttributeDL(required = true)
	protected int minKey;
	
	@AttributeDL(required = true)
	protected int maxKey;
	
	protected boolean isMinKeyPressed;
	protected boolean isMaxKeyPressed;

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		if (key == minKey) {
			if (action == GLFW_PRESS) {
				isMinKeyPressed = true;
				singleStepActive = true;
			}
			else if (action == GLFW_RELEASE) {
				isMinKeyPressed = false;
			}
		}
		else if (key == maxKey) {
			if (action == GLFW_PRESS) {
				isMaxKeyPressed = true;
				singleStepActive = true;
			}
			else if (action == GLFW_RELEASE) {
				isMaxKeyPressed = false;
			}
		}
	}

	@Override
	public void update(float elapsedTime)
	{
		if (singleStep && !singleStepActive) {
			return;
		}

		singleStepActive = false;

		float direction = 0.0f;
		boolean snapActive = false;
		boolean keysActive = false;

		// JUST minkey is pressed
		if (isMinKeyPressed && !isMaxKeyPressed) {

			keysActive = true;
			direction = -1.0f;
		}
		// JUST maxkey is pressed
		else if (isMaxKeyPressed && !isMinKeyPressed) {

			keysActive = true;
			direction = 1.0f;
		}
		// no key or both keys are pressed -> snap to middle if active
		else {

			if (snapToMiddle) {

				snapActive = true;

				if (value > middle) {

					direction = -1.0f;
				}
				else if (value < middle) {

					direction = 1.0f;
				}
			}
		}

		if (timeScaled) {
			velocity = org.joml.Math.lerp(velocity, direction, response * elapsedTime);
			value += velocity * speed * elapsedTime;
		}
		else {
			velocity = org.joml.Math.lerp(velocity, direction, response);
			value += velocity * speed;
		}

		// stop velocity
		if (!keysActive && !snapActive && Math.abs(velocity) < epsilon) {
			velocity = 0.0f;
		}

		// snap to middle
		if (snapActive && Math.abs(value - middle) < epsilon) {
			value = middle;
		}

		// clamp value
		if (overflow == InputAxisOverflowMode.CLAMP) {
			value = Math.min(Math.max(min, value), max);
		}
		else if (overflow == InputAxisOverflowMode.WRAP) {
			if (value > max) {
				value = value - (max - min);
			}
			else if (value < min) {
				value = value + (max - min);
			}
		}
		else if (overflow == InputAxisOverflowMode.WRAP_ALIGNED) {
			if (value > max) {
				value = min;
			}
			else if (value < min) {
				value = max;
			}

		}
	}

	public int getMinKey()
	{
		return minKey;
	}

	public void setMinKey(int minKey)
	{
		this.minKey = minKey;
	}

	public int getMaxKey()
	{
		return maxKey;
	}

	public void setMaxKey(int maxKey)
	{
		this.maxKey = maxKey;
	}

	public boolean isIsMinKeyPressed()
	{
		return isMinKeyPressed;
	}

	public void setIsMinKeyPressed(boolean isMinKeyPressed)
	{
		this.isMinKeyPressed = isMinKeyPressed;
	}

	public boolean isIsMaxKeyPressed()
	{
		return isMaxKeyPressed;
	}

	public void setIsMaxKeyPressed(boolean isMaxKeyPressed)
	{
		this.isMaxKeyPressed = isMaxKeyPressed;
	}
}
