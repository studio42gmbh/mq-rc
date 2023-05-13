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
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author Benjamin Schiller
 */
public class MouseButtonKey extends AbstractInputKey implements MouseButtonInputHandler
{
	private final static Logger log = LogManager.getLogger(MouseButtonKey.class.getName());

	@AttributeDL(required = true)
	protected int key;

	@Override
	public void handleMouseButton(int button, int action, int mods)
	{
		//log.debug("" + button + " " + action + "," + mods);

		if (button == this.key) {
			if (action == GLFW_PRESS) {
				pressNow();
			}
			else if (action == GLFW_RELEASE) {
				releasedNow();
			}
		}
	}

	public int getKey()
	{
		return key;
	}

	public void setKey(int key)
	{
		this.key = key;
	}
}
