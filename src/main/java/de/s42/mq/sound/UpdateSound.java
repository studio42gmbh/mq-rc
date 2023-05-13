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
package de.s42.mq.sound;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.FloatData;
import de.s42.mq.tasks.AbstractTask;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class UpdateSound extends AbstractTask
{

	private final static Logger log = LogManager.getLogger(UpdateSound.class.getName());

	@AttributeDL(required = true)
	protected Sound sound;

	@AttributeDL(required = true)
	protected FloatData deltaTime = new FloatData();

	@Override
	protected void runTask()
	{
		Sound snd = getSound();

		assert snd != null;
		assert snd.isLoaded();
		assert getDeltaTime() != null;

		boolean playing = snd.isPlaying();

		if (playing) {
			snd.pause();
		}

		snd.update(getDeltaTime().getFloatValue());

		if (playing) {
			snd.play();
		}
	}

	public FloatData getDeltaTime()
	{
		return deltaTime;
	}

	public void setDeltaTime(FloatData deltaTime)
	{
		assert deltaTime != null;

		this.deltaTime = deltaTime;
	}

	public Sound getSound()
	{
		return sound;
	}

	public void setSound(Sound sound)
	{
		this.sound = sound;
	}
}
