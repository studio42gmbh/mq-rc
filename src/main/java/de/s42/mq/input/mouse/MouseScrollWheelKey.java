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
package de.s42.mq.input.mouse;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.input.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class MouseScrollWheelKey extends AbstractInputKey implements ScrollInputHandler
{
	private final static Logger log = LogManager.getLogger(MouseScrollWheelKey.class.getName());

	@AttributeDL(required = true)
	protected ScrollKey key;

	public enum ScrollKey
	{
		UP,
		DOWN,
		LEFT,
		RIGHT,
	}

	public ScrollKey getKey()
	{
		return key;
	}

	public void setKey(ScrollKey key)
	{
		this.key = key;
	}

	@Override
	public void handleScroll(double xOffset, double yOffset)
	{
		//log.debug("Wheel" + xOffset + " " + yOffset);

		ScrollKey button = null;

		if (yOffset > 0) {
			button = ScrollKey.UP;
		}
		else if (yOffset < 0) {
			button = ScrollKey.DOWN;
		}
		else if (xOffset > 0) {
			button = ScrollKey.RIGHT;
		}
		else if (xOffset < 0) {
			button = ScrollKey.LEFT;
		}

		if (button == key) {
			pressNow();
		}
	}
}
