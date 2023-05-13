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

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.mq.assets.AbstractAsset;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.openal.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Benjamin Schiller
 */
public class Sounds extends AbstractAsset
{

	protected long deviceId = -1;
	protected long contextId = -1;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		deviceId = alcOpenDevice((ByteBuffer) null);
		if (deviceId == NULL) {
			throw new InvalidInstance("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(deviceId);
		contextId = alcCreateContext(deviceId, (IntBuffer) null);
		if (contextId == NULL) {
			throw new InvalidInstance("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(contextId);
		AL.createCapabilities(deviceCaps);

		setAttenuationModelDirect(AL_EXPONENT_DISTANCE_CLAMPED);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		alcDestroyContext(contextId);
		contextId = -1;
		alcCloseDevice(deviceId);
		deviceId = -1;

		super.unload();
	}

	protected void setAttenuationModelDirect(int model)
	{
		alDistanceModel(model);
	}

	public long getDeviceId()
	{
		return deviceId;
	}

	public long getContextId()
	{
		return contextId;
	}
}
