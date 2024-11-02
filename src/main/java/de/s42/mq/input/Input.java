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

import de.s42.dl.types.DLContainer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.Updateable;
import de.s42.mq.core.AbstractEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class Input extends AbstractEntity implements Updateable, DLContainer, InputHandler, KeyInputHandler, MouseButtonInputHandler, ScrollInputHandler, MouseCursorInputHandler
{

	private final static Logger log = LogManager.getLogger(Input.class.getName());

	protected boolean debug;

	protected final List<MouseCursorInputHandler> mouseCursors = Collections.synchronizedList(new ArrayList<>());
	protected final List<MouseButtonInputHandler> mouseButtons = Collections.synchronizedList(new ArrayList<>());
	protected final List<KeyInputHandler> keys = Collections.synchronizedList(new ArrayList<>());
	protected final List<ScrollInputHandler> scrolls = Collections.synchronizedList(new ArrayList<>());
	protected final List<InputHandler> handlers = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		if (debug) {
			log.debug("Key", key, scancode, action, mods);
		}

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

		if (child instanceof InputHandler inputHandler) {
			handlers.add(inputHandler);
		}

		if (child instanceof KeyInputHandler keyInputHandler) {
			keys.add(keyInputHandler);
		}

		if (child instanceof ScrollInputHandler scrollInputHandler) {
			scrolls.add(scrollInputHandler);
		}

		if (child instanceof MouseButtonInputHandler mouseButtonInputHandler) {
			mouseButtons.add(mouseButtonInputHandler);
		}

		if (child instanceof MouseCursorInputHandler mouseCursorInputHandler) {
			mouseCursors.add(mouseCursorInputHandler);
		}
	}

	@Override
	public List getChildren()
	{
		return Collections.unmodifiableList(handlers);
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
}
