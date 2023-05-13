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

import de.s42.mq.core.AbstractEntity;
import java.util.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.Updateable;
import de.s42.dl.types.DLContainer;

/**
 *
 * @author Benjamin Schiller
 */
public class Input extends AbstractEntity implements Updateable, DLContainer, InputHandler, KeyInputHandler, MouseButtonInputHandler, ScrollInputHandler, MouseCursorInputHandler
{
	private final static Logger log = LogManager.getLogger(Input.class.getName());

	protected final List<MouseCursorInputHandler> mouseCursors = Collections.synchronizedList(new ArrayList<>());
	protected final List<MouseButtonInputHandler> mouseButtons = Collections.synchronizedList(new ArrayList<>());
	protected final List<KeyInputHandler> keys = Collections.synchronizedList(new ArrayList<>());
	protected final List<ScrollInputHandler> scrolls = Collections.synchronizedList(new ArrayList<>());
	protected final List<InputHandler> handlers = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		//log.debug("Key {} {}", key, scancode);
		for (KeyInputHandler iKey : keys) {
			iKey.handleKey(key, scancode, action, mods);
		}
	}

	@Override
	public void handleScroll(double xOffset, double yOffset)
	{
		for (ScrollInputHandler scroll : scrolls) {
			scroll.handleScroll(xOffset, yOffset);
		}
	}

	@Override
	public void handleMouseCursor(double xPos, double yPos)
	{
		for (MouseCursorInputHandler mouseCursor : mouseCursors) {
			mouseCursor.handleMouseCursor(xPos, yPos);
		}
	}

	@Override
	public void update(float elapsedTime)
	{
		for (InputHandler handler : handlers) {
			handler.update(elapsedTime);
		}
	}

	@Override
	public void handleMouseButton(int button, int action, int mods)
	{
		for (MouseButtonInputHandler mouseButton : mouseButtons) {
			mouseButton.handleMouseButton(button, action, mods);
		}
	}

	@Override
	public void addChild(String name, Object child)
	{
		assert name != null;
		assert child != null;

		if (child instanceof InputHandler) {
			handlers.add((InputHandler) child);
		}

		if (child instanceof KeyInputHandler) {
			keys.add((KeyInputHandler) child);
		}

		if (child instanceof ScrollInputHandler) {
			scrolls.add((ScrollInputHandler) child);
		}

		if (child instanceof MouseButtonInputHandler) {
			mouseButtons.add((MouseButtonInputHandler) child);
		}

		if (child instanceof MouseCursorInputHandler) {
			mouseCursors.add((MouseCursorInputHandler) child);
		}
	}
}
