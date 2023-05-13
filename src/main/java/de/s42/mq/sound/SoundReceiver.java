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
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector3Data;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.*;

/**
 *
 * @author Benjamin Schiller
 */
public class SoundReceiver extends AbstractAsset
{

	@AttributeDL(required = false)
	protected Vector3Data position = new Vector3Data();

	@AttributeDL(required = false)
	protected Vector3Data velocity = new Vector3Data();

	@AttributeDL(required = false)
	protected Vector3Data direction = new Vector3Data();

	@AttributeDL(required = false)
	protected Vector3Data up = new Vector3Data();

	/**
	 * Is the relative volume of this sound. will be applied to attenuation model
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData gain = new FloatData(1.0f);

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		update(0.0f);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		super.unload();
	}

	@Override
	public void update(float elapsedTime)
	{
		assert position != null;
		assert velocity != null;
		assert direction != null;
		assert up != null;
		assert gain != null;

		setGainDirect(getGain().getFloatValue());
		setVelocityDirect(getVelocity().getValue());
		setPositionDirect(getPosition().getValue());
		setOrientationDirect(getDirection().getValue(), getUp().getValue());
	}

	protected void setGainDirect(float gain)
	{
		alListenerf(AL_GAIN, gain);
	}

	protected void setPositionDirect(Vector3f pos)
	{
		alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
	}

	protected void setVelocityDirect(Vector3f vel)
	{
		alListener3f(AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	protected void setOrientationDirect(Vector3f direction, Vector3f up)
	{
		float[] data = new float[6];
		data[0] = direction.x;
		data[1] = direction.y;
		data[2] = direction.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		alListenerfv(AL_ORIENTATION, data);
	}

	public Vector3Data getPosition()
	{
		return position;
	}

	public void setPosition(Vector3Data position)
	{
		this.position = position;

		if (position != null && isLoaded()) {
			setPositionDirect(position.getValue());
		}
	}

	public Vector3Data getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector3Data velocity)
	{
		this.velocity = velocity;

		if (velocity != null && isLoaded()) {
			setPositionDirect(velocity.getValue());
		}
	}

	public Vector3Data getDirection()
	{
		return direction;
	}

	public void setDirection(Vector3Data direction)
	{
		this.direction = direction;

		if (direction != null && isLoaded()) {
			setOrientationDirect(direction.getValue(), getUp().getValue());
		}
	}

	public Vector3Data getUp()
	{
		return up;
	}

	public void setUp(Vector3Data up)
	{
		this.up = up;

		if (direction != null && isLoaded()) {
			setOrientationDirect(getDirection().getValue(), up.getValue());
		}
	}

	public FloatData getGain()
	{
		return gain;
	}

	public void setGain(FloatData gain)
	{
		this.gain = gain;

		if (gain != null && isLoaded()) {
			setGainDirect(gain.getFloatValue());
		}
	}
}
