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

import de.s42.dl.*;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.tasks.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class RunScenes extends AbstractTask implements LongRunningTask
{

	private final static Logger log = LogManager.getLogger(RunScenes.class.getName());

	@AttributeDL(required = true)
	protected Scenes scenes;

	/*
	 * will be set into scenes that are loaded and run
	 */
	@AttributeDL(required = true)
	protected DLCore core;

	protected Scene runNextTransition(SceneTransition transition)
	{
		try {
			Scene nextScene = transition.getNext();

			nextScene.setCore(getCore());

			nextScene.load();

			nextScene.run();

			nextScene.unload();

			return nextScene;
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected SceneTransition nextTransition(Scene currentScene)
	{
		return getScenes().nextTransition(currentScene);
	}

	@Override
	protected void runTask()
	{
		assert getScenes() != null;

		SceneTransition currentTransition = getScenes().getFirstTransition();

		while (true) {

			//log.debug("Transition " + currentTransition);
			Scene currentScene = runNextTransition(currentTransition);

			// exit loop if end is reached
			if (currentTransition == getScenes().getExitTransition()) {
				break;
			}

			getScenes().getShallTransition().setBooleanValue(false);
			currentTransition = nextTransition(currentScene);
		}
	}

	public Scenes getScenes()
	{
		return scenes;
	}

	public void setScenes(Scenes scenes)
	{
		this.scenes = scenes;
	}

	public DLCore getCore()
	{
		return core;
	}

	public void setCore(DLCore core)
	{
		this.core = core;
	}
}
