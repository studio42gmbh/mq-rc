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
package de.s42.mq.scenes;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.BooleanData;
import java.util.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.dl.types.DLContainer;

/**
 *
 * @author Benjamin Schiller
 */
public class Scenes extends AbstractAsset implements DLContainer
{

	private final static Logger log = LogManager.getLogger(Scenes.class.getName());

	@AttributeDL(required = true)
	protected SceneTransition firstTransition;

	@AttributeDL(required = true)
	protected SceneTransition exitTransition;

	@AttributeDL(required = false)
	protected BooleanData shallTransition = new BooleanData();

	protected final List<Scene> scenes = new ArrayList<>();
	protected final List<SceneTransition> sceneTransitions = new ArrayList<>();
	protected SceneTransition nextTransition;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		//log.debug("Loaded " + this + " " + this.hashCode());
		getShallTransition().setBooleanValue(false);
		setNextTransition(null);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		getShallTransition().setBooleanValue(false);
		setNextTransition(null);

		super.unload();
	}

	public void transition(SceneTransition transition)
	{
		assert transition != null;

		if (!getSceneTransitions().contains(transition)) {
			throw new IllegalArgumentException("transition " + transition + " is not contained in this transitions " + this);
		}

		setNextTransition(transition);
		getShallTransition().setValue(true);
	}

	public boolean containsScene(Scene scene)
	{
		assert scene != null;

		return getScenes().contains(scene);
	}

	public SceneTransition nextTransition(Scene currentScene)
	{
		if (!containsScene(currentScene)) {
			throw new IllegalArgumentException("Scene is not contained " + currentScene);
		}

		SceneTransition next = getNextTransition();

		// allow custom path with nextTransition
		if (next != null) {
			if (next.getCurrent() != null && next.getCurrent() != currentScene) {
				throw new IllegalArgumentException("Next transition is not of current scene " + currentScene + " but " + next.getNext());
			}

			if (!getSceneTransitions().contains(next)) {
				throw new IllegalArgumentException("Next transition " + next + " is not contained in this transitions " + this);
			}

			// automatically reset next transition on tranditioning
			SceneTransition result = next;
			setNextTransition(null);
			return result;
		}

		// find the first matching scene
		for (SceneTransition transition : getSceneTransitions()) {
			if (transition.getCurrent() == currentScene) {
				return transition;
			}
		}

		// use exit transition
		return getExitTransition();
	}

	public SceneTransition getFirstTransition()
	{
		return firstTransition;
	}

	public void setFirstTransition(SceneTransition firstTransition)
	{
		this.firstTransition = firstTransition;
	}

	@Override
	public void addChild(String name, Object child)
	{
		if (child instanceof Scene) {
			addScene((Scene) child);
		} else if (child instanceof SceneTransition) {
			addSceneTransition((SceneTransition) child);
		}
	}

	public List<Scene> getScenes()
	{
		return scenes;
	}

	public void addScene(Scene scene)
	{
		assert scene != null;

		scenes.add(scene);
	}

	public Optional<Scene> getSceneByName(String name)
	{
		assert name != null;
		for (Scene scene : scenes) {
			if (name.equals(scene.getName())) {
				return Optional.of(scene);
			}
		}

		return Optional.empty();
	}

	public List<SceneTransition> getSceneTransitions()
	{
		return sceneTransitions;
	}

	public void addSceneTransition(SceneTransition sceneTransition)
	{
		assert sceneTransition != null;

		sceneTransitions.add(sceneTransition);
	}

	public Optional<SceneTransition> getSceneTransitionByName(String name)
	{
		assert name != null;
		for (SceneTransition sceneTransition : sceneTransitions) {
			if (name.equals(sceneTransition.getName())) {
				return Optional.of(sceneTransition);
			}
		}

		return Optional.empty();
	}

	public SceneTransition getExitTransition()
	{
		return exitTransition;
	}

	public void setExitTransition(SceneTransition exitTransition)
	{
		this.exitTransition = exitTransition;
	}

	protected SceneTransition getNextTransition()
	{
		return nextTransition;
	}

	protected void setNextTransition(SceneTransition nextTransition)
	{
		this.nextTransition = nextTransition;
	}

	public BooleanData getShallTransition()
	{
		return shallTransition;
	}

	public void setShallTransition(BooleanData shallTransition)
	{
		this.shallTransition = shallTransition;
	}
}
