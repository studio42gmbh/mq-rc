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
import java.util.concurrent.atomic.AtomicInteger;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class AbstractInputKey extends AbstractEntity implements InputKey, InputHandler
{
	private final static Logger log = LogManager.getLogger(AbstractInputKey.class.getName());

	protected AtomicInteger pressedAtomic = new AtomicInteger(0);
	protected AtomicInteger releasedAtomic = new AtomicInteger(0);

	protected boolean pressed;
	protected boolean pressedNow;
	protected boolean releasedNow;

	@Override
	public void update(float elapsedTime)
	{
		int pA = pressedAtomic.get();

		pressedNow = (pA > 1);

		if (pA > 1) {
			pressedAtomic.decrementAndGet();
		}
		
		pressed = (pA > 0);

		int rA = releasedAtomic.get();

		releasedNow = (rA > 1);

		if (rA > 1) {
			releasedAtomic.decrementAndGet();
		}
	}

	@Override
	public boolean isPressed()
	{
		return pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}

	@Override
	public boolean isPressedNow()
	{
		return pressedNow;
	}

	public void setPressedNow(boolean pressedNow)
	{
		this.pressedNow = pressedNow;
	}

	@Override
	public boolean isReleased()
	{
		return !isPressed();
	}

	public void setReleased(boolean released)
	{
		setPressed(!released);
	}

	@Override
	public boolean isReleasedNow()
	{
		return releasedNow;
	}

	public void setReleasedNow(boolean releasedNow)
	{
		this.releasedNow = releasedNow;
	}

	public void pressNow()
	{
		//log.debug("pressNow");

		pressedAtomic.set(2);
		releasedAtomic.set(0);
	}

	public void releasedNow()
	{
		//log.debug("releasedAtomic");

		pressedAtomic.set(0);
		releasedAtomic.set(2);
	}
}
