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
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.BooleanData;
import java.nio.*;
import java.nio.file.Path;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.*;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * https://github.com/lwjglgamedev/lwjglbook/tree/master/chapter22/src/main/java/org/lwjglb/engine/sound
 *
 * @author Benjamin Schiller
 */
public class AudioClip extends AbstractAsset
{

	private final static Logger log = LogManager.getLogger(AudioClip.class.getName());

	@AttributeDL(required = true)
	@isFile
	protected Path source;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData loop = new BooleanData();

	/**
	 * If true the position of the source will be taken in relative space dimensions to listener
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData relative = new BooleanData();

	/**
	 * If true the position of the source will be taken in absolute space dimensions to listener
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData absolute = new BooleanData();

	@AttributeDL(ignore = true)
	protected ShortBuffer pcm;

	protected int bufferId = -1;
	protected int channels = -1;
	protected float duration = -1.0f;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		bufferId = alGenBuffers();

		try ( STBVorbisInfo info = STBVorbisInfo.malloc()) {
			pcm = readVorbis(source, info);

			alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());

			if (info.channels() > 1 && (getAbsolute().getBooleanValue() || getRelative().getBooleanValue())) {
				log.warn("The clip " + getSource().toAbsolutePath().toString() + " can not be attenuated with absolute or relative models as it is not MONO!");
			}
		} catch (Exception ex) {
			throw new InvalidInstance("Could not load sound " + ex.getMessage(), ex);
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		alDeleteBuffers(bufferId);
		bufferId = -1;

		MemoryUtil.memFree(pcm);
		pcm = null;

		super.unload();
	}

	protected ShortBuffer readVorbis(Path source, STBVorbisInfo info) throws Exception
	{
		try ( MemoryStack stack = MemoryStack.stackPush()) {

			ByteBuffer vorbis = getAssetManager().getSourceAsByteBuffer(source);

			IntBuffer error = stack.mallocInt(1);

			long decoder = stb_vorbis_open_memory(vorbis, error, null);

			if (decoder == NULL) {
				throw new InvalidInstance("Failed to open Ogg Vorbis file - " + error.get(0));
			}

			stb_vorbis_get_info(decoder, info);

			channels = info.channels();

			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
			duration = stb_vorbis_stream_length_in_seconds(decoder);

			ShortBuffer pcmBuffer = MemoryUtil.memAllocShort(lengthSamples * channels);

			pcmBuffer.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcmBuffer) * channels);
			stb_vorbis_close(decoder);

			log.debug("Loaded audio clip " + source.toAbsolutePath().toString() + " " + duration + " sec " + channels + " channel(s)");

			return pcmBuffer;
		}
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public BooleanData getLoop()
	{
		return loop;
	}

	public void setLoop(BooleanData loop)
	{
		this.loop = loop;
	}

	public BooleanData getRelative()
	{
		return relative;
	}

	public void setRelative(BooleanData relative)
	{
		this.relative = relative;
	}

	public int getBufferId()
	{
		return bufferId;
	}

	public ShortBuffer getPcm()
	{
		return pcm;
	}

	public int getChannels()
	{
		return channels;
	}

	public float getDuration()
	{
		return duration;
	}

	public BooleanData getAbsolute()
	{
		return absolute;
	}

	public void setAbsolute(BooleanData absolute)
	{
		this.absolute = absolute;
	}
}
