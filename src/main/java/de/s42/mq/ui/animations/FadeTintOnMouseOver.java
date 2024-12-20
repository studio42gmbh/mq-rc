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
package de.s42.mq.ui.animations;

import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshAnimation;
import de.s42.mq.tasks.AbstractTask;
import de.s42.mq.ui.Button;
import de.s42.mq.util.MQMath;

/**
 *
 * @author Benjamin Schiller
 */
public class FadeTintOnMouseOver extends AbstractTask implements MeshAnimation
{

	private final static Logger log = LogManager.getLogger(FadeTintOnMouseOver.class.getName());

	@required
	protected IntegerData currentIdentifier = new IntegerData();

	@required
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData baseColor = new ColorData();

	@required
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData overColor = new ColorData();

	/*
	 * if not set it transitions instant
	 */
	@required
	//@AnnotationDL(value = InSecondsDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected FloatData transitionDuration = new FloatData();

	protected Button button;

	@dontPersist
	protected long lastTime;

	@dontPersist
	protected float transition;

	@dontPersist
	protected final MQColor transitionColor = new MQColor();

	@Override
	protected void runTaskFirstTime()
	{
		lastTime = System.nanoTime();
	}

	@Override
	public void update(Mesh mesh, float deltaTime)
	{
		button = (Button) mesh;
		runTask();
	}

	@Override
	public FadeTintOnMouseOver copy()
	{
		FadeTintOnMouseOver copy = new FadeTintOnMouseOver();

		copy.currentIdentifier = currentIdentifier;
		copy.baseColor = baseColor;
		copy.overColor = overColor;
		copy.transitionDuration = transitionDuration;
		copy.button = button;
		copy.lastTime = lastTime;
		copy.transition = transition;
		copy.transitionColor.set(transitionColor);

		return copy;
	}

	@Override
	protected void runTask()
	{
		assert button != null;
		assert currentIdentifier != null;

		long currentTime = System.nanoTime();
		float transitionDelta = 1.0f;

		if (transitionDuration.getFloatValue() > 0) {
			transitionDelta = (currentTime - lastTime) / 1.0E9f / transitionDuration.getFloatValue();
		}

		if (currentIdentifier.getValue() == button.getIdentifier()) {
			//log.warn(button.getAnimations().size(), hashCode(), currentIdentifier.getValue(), button.getIdentifier(), transition, button.getPanelComponent().getBackgroundColor().hashCode());
			transition += transitionDelta;
		} else {
			transition -= transitionDelta;
		}

		lastTime = currentTime;

		transition = MQMath.saturate(transition);

		transitionColor.set(baseColor.getValue()).blend(overColor.getValue(), transition);
		button.getPanelComponent().getBackgroundColor().getValue().set(transitionColor);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public IntegerData getCurrentIdentifier()
	{
		return currentIdentifier;
	}

	public void setCurrentIdentifier(IntegerData currentIdentifier)
	{
		this.currentIdentifier = currentIdentifier;
	}

	public ColorData getBaseColor()
	{
		return baseColor;
	}

	public void setBaseColor(ColorData baseColor)
	{
		this.baseColor = baseColor;
	}

	public ColorData getOverColor()
	{
		return overColor;
	}

	public void setOverColor(ColorData overColor)
	{
		this.overColor = overColor;
	}

	public Button getButton()
	{
		return button;
	}

	public void setButton(Button button)
	{
		this.button = button;
	}

	public FloatData getTransitionDuration()
	{
		return transitionDuration;
	}

	public void setTransitionDuration(FloatData transitionDuration)
	{
		this.transitionDuration = transitionDuration;
	}
	// "Getters/Setters" </editor-fold>
}
