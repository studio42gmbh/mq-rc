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

import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.mq.input.AbstractInputAxis;
import de.s42.mq.input.InputHandler;
import de.s42.mq.input.KeyInputHandler;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 *
 * @author Benjamin Schiller
 */
public class KeyInputAxis extends AbstractInputAxis implements InputHandler, KeyInputHandler
{

	@required
	protected int minKey;

	@required
	protected int maxKey;

	@dontPersist
	protected boolean isMinKeyPressed;

	@dontPersist
	protected boolean isMaxKeyPressed;

	@Override
	public void handleChar(String chars)
	{
		// @todo
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		if (key == minKey) {
			if (action == GLFW_PRESS) {
				isMinKeyPressed = true;
				singleStepActive = true;
			} else if (action == GLFW_RELEASE) {
				isMinKeyPressed = false;
			}
		} else if (key == maxKey) {
			if (action == GLFW_PRESS) {
				isMaxKeyPressed = true;
				singleStepActive = true;
			} else if (action == GLFW_RELEASE) {
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
		} // JUST maxkey is pressed
		else if (isMaxKeyPressed && !isMinKeyPressed) {

			keysActive = true;
			direction = 1.0f;
		} // No key or both keys are pressed -> snap to middle if active
		else {

			if (snapToMiddle) {

				snapActive = true;

				if (value > middle) {

					direction = -1.0f;
				} else if (value < middle) {

					direction = 1.0f;
				}
			}
		}

		if (timeScaled) {
			velocity = org.joml.Math.lerp(velocity, direction, response * elapsedTime);
			value += velocity * speed * elapsedTime;
		} else {
			velocity = org.joml.Math.lerp(velocity, direction, response);
			value += velocity * speed;
		}

		// Stop velocity
		if (!keysActive && !snapActive && Math.abs(velocity) < epsilon) {
			velocity = 0.0f;
		}

		// Snap to middle
		if (snapActive && Math.abs(value - middle) < epsilon) {
			value = middle;
		}

		if (null != overflow) // Clamp value
		{
			switch (overflow) {
				case CLAMP ->
					value = Math.min(Math.max(min, value), max);
				case WRAP -> {
					if (value > max) {
						value = value - (max - min);
					} else if (value < min) {
						value = value + (max - min);
					}
				}
				case WRAP_ALIGNED -> {
					if (value > max) {
						value = min;
					} else if (value < min) {
						value = max;
					}
				}
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
