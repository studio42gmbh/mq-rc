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
import de.s42.mq.input.AbstractInputKey;
import de.s42.mq.input.KeyInputHandler;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 *
 * @author Benjamin Schiller
 */
public class KeyInputKey extends AbstractInputKey implements KeyInputHandler
{

	@required
	protected int key;

	@Override
	public void handleChar(String chars)
	{
		// @todo
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		if (key == this.key) {
			if (action == GLFW_PRESS) {
				pressNow();
			} else if (action == GLFW_RELEASE) {
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
