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
package de.s42.mq.ui;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.tasks.AbstractTask;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractWindowTask extends AbstractTask implements WindowTask
{

	@AttributeDL(required = false)
	protected Window window;

	@Override
	public Window getWindow()
	{
		return window;
	}

	@Override
	public void setWindow(Window window)
	{
		this.window = window;
	}
}
