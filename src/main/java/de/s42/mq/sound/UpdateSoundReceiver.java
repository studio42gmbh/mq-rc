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
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.FloatData;
import de.s42.mq.tasks.AbstractTask;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class UpdateSoundReceiver extends AbstractTask
{

	private final static Logger log = LogManager.getLogger(UpdateSoundReceiver.class.getName());

	@AttributeDL(required = true)
	protected SoundReceiver receiver;

	@AttributeDL(required = true)
	protected Camera camera;

	@AttributeDL(required = true)
	protected FloatData deltaTime = new FloatData();

	@Override
	protected void runTask()
	{
		assert receiver != null;
		assert receiver.isLoaded();
		assert camera != null;
		assert camera.isLoaded();
		assert deltaTime != null;

		receiver.getPosition().setValue(camera.getWorldPosition());
		receiver.getDirection().setValue(camera.getForwardDirection());
		receiver.getVelocity().setValue(0.0f, 0.0f, 0.0f);
		receiver.getUp().setValue(camera.getCameraUp());
		receiver.update(deltaTime.getFloatValue());
	}

	public SoundReceiver getReceiver()
	{
		return receiver;
	}

	public void setReceiver(SoundReceiver receiver)
	{
		assert receiver != null;

		this.receiver = receiver;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		assert camera != null;

		this.camera = camera;
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
}
