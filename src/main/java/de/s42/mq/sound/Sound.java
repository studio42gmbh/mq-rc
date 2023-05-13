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

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.*;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.AL_MAX_DISTANCE;
import static org.lwjgl.openal.AL10.AL_ROLLOFF_FACTOR;
import static org.lwjgl.openal.AL11.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Sound extends AbstractAsset
{

	@AttributeDL(required = true)
	protected AudioClip clip;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData loop = new BooleanData(false);

	/**
	 * If true the position of the source will be taken in relative space dimensions to listener. If not set the value
	 * of the clip is taken
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData relative = new BooleanData(false);

	/**
	 * If true the position of the source will be taken in absolute space dimensions to listener. If not set the value
	 * of the clip is taken
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData absolute = new BooleanData(false);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected BooleanData autoPlay = new BooleanData(false);

	/**
	 * Is the relative volume of this sound. will be applied to attenuation model
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData gain = new FloatData(1.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0001")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData pitch = new FloatData(1.0f);

	/**
	 * On attenutation see i.e. https://hackage.haskell.org/package/OpenAL-1.7.0.5/docs/Sound-OpenAL-AL-Attenuation.html
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.01")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData rolloff = new FloatData(1.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0001")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData referenceDistance = new FloatData(1.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0001")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData maxDistance = new FloatData(100.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected Vector3Data position = new Vector3Data();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected Vector3Data velocity = new Vector3Data();

	protected int sourceId = -1;

	@Override
	public void load() throws DLException
	{
		assert clip != null;

		if (isLoaded()) {
			return;
		}

		super.load();

		clip.load();

		sourceId = alGenSources();

		setBufferDirect(clip.getBufferId());

		//@todo allow to set these sound values later
		alSourcef(sourceId, AL_MAX_GAIN, 1.0f);
		alSourcef(sourceId, AL_MIN_GAIN, 0.0f);

		if (BooleanData.isTrue(getLoop()) || BooleanData.isTrue(getClip().getLoop())) {
			alSourcei(sourceId, AL_LOOPING, AL_TRUE);
		}

		if (BooleanData.isTrue(getRelative()) || BooleanData.isTrue(getClip().getRelative())) {
			alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
		}

		if (BooleanData.isTrue(getAbsolute()) || BooleanData.isTrue(getClip().getAbsolute())) {
			alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_TRUE);
		}

		update(0.0f);

		if (BooleanData.isTrue(getAutoPlay())) {
			play();
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		stop();

		alDeleteSources(sourceId);
		sourceId = -1;

		super.unload();
	}

	@Override
	public void update(float deltaTime)
	{
		assert isLoaded();

		// @todo might not want to do that on every update if not necessary
		setReferenceDistanceDirect(getReferenceDistance().getFloatValue());
		setMaxDistanceDirect(getMaxDistance().getFloatValue());
		setRolloffDirect(getRolloff().getFloatValue());
		setGainDirect(getGain().getFloatValue());
		setPitchDirect(getPitch().getFloatValue());
		setPositionDirect(getPosition().getValue());
		setVelocityDirect(getVelocity().getValue());
	}

	protected void setReferenceDistanceDirect(float distance)
	{
		assert isLoaded();

		alSourcef(sourceId, AL_REFERENCE_DISTANCE, distance);
	}

	protected void setMaxDistanceDirect(float distance)
	{
		assert isLoaded();

		alSourcef(sourceId, AL_MAX_DISTANCE, distance);
	}

	protected void setRolloffDirect(float rolloff)
	{
		assert isLoaded();

		alSourcef(sourceId, AL_ROLLOFF_FACTOR, rolloff);
	}

	protected void setBufferDirect(int bufferId)
	{
		assert isLoaded();

		stop();

		alSourcei(sourceId, AL_BUFFER, bufferId);
	}

	protected void setPositionDirect(Vector3f position)
	{
		assert isLoaded();

		alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
	}

	protected void setVelocityDirect(Vector3f velocity)
	{
		assert isLoaded();

		alSource3f(sourceId, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	protected void setGainDirect(float gain)
	{
		assert isLoaded();

		alSourcef(sourceId, AL_GAIN, gain);
	}

	protected void setPitchDirect(float pitch)
	{
		assert isLoaded();

		alSourcef(sourceId, AL_PITCH, pitch);
	}

	protected void setPropertyDirect(int param, float value)
	{
		assert isLoaded();

		alSourcef(sourceId, param, value);
	}

	public void playIfNotPlaying()
	{
		assert isLoaded();

		if (!isPlaying()) {
			play();
		}
	}

	public void play()
	{
		assert isLoaded();

		alSourcePlay(sourceId);
	}

	public boolean isPlaying()
	{
		assert isLoaded();

		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
	}

	public void pause()
	{
		assert isLoaded();

		alSourcePause(sourceId);
	}

	public void stop()
	{
		assert isLoaded();

		alSourceStop(sourceId);
	}

	public BooleanData getLoop()
	{
		return loop;
	}

	public void setLoop(BooleanData loop)
	{
		assert loop != null;

		this.loop = loop;
	}

	public BooleanData getRelative()
	{
		return relative;
	}

	public void setRelative(BooleanData relative)
	{
		assert relative != null;

		this.relative = relative;
	}

	public AudioClip getClip()
	{
		return clip;
	}

	public void setClip(AudioClip clip)
	{
		assert !isLoaded();
		assert clip != null;

		this.clip = clip;
	}

	public BooleanData getAutoPlay()
	{
		return autoPlay;
	}

	public void setAutoPlay(BooleanData autoPlay)
	{
		assert !isLoaded();
		assert autoPlay != null;

		this.autoPlay = autoPlay;
	}

	public FloatData getGain()
	{
		return gain;
	}

	public void setGain(FloatData gain)
	{
		assert gain != null;

		this.gain = gain;
	}

	public int getSourceId()
	{
		return sourceId;
	}

	public Vector3Data getPosition()
	{
		return position;
	}

	public void setPosition(Vector3Data position)
	{
		assert position != null;

		this.position = position;
	}

	public Vector3Data getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector3Data velocity)
	{
		assert velocity != null;

		this.velocity = velocity;
	}

	public FloatData getPitch()
	{
		return pitch;
	}

	public void setPitch(FloatData pitch)
	{
		this.pitch = pitch;
	}

	public BooleanData getAbsolute()
	{
		return absolute;
	}

	public void setAbsolute(BooleanData absolute)
	{
		this.absolute = absolute;
	}

	public FloatData getRolloff()
	{
		return rolloff;
	}

	public void setRolloff(FloatData rolloff)
	{
		this.rolloff = rolloff;
	}

	public FloatData getReferenceDistance()
	{
		return referenceDistance;
	}

	public void setReferenceDistance(FloatData referenceDistance)
	{
		this.referenceDistance = referenceDistance;
	}

	public FloatData getMaxDistance()
	{
		return maxDistance;
	}

	public void setMaxDistance(FloatData maxDistance)
	{
		this.maxDistance = maxDistance;
	}
}
