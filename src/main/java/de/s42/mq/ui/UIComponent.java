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
import de.s42.mq.core.Copyable;
import de.s42.mq.core.Entity;
import de.s42.mq.input.KeyInputHandler;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;

/**
 *
 * @author Benjamin Schiller
 */
public interface UIComponent extends Copyable, Entity, KeyInputHandler
{

	public int getIdentifier();

	public void setIdentifier(int identifier);

	@AttributeDL(ignore = true)
	public UIManager getUiManager();

	@AttributeDL(ignore = true)
	public void setUiManager(UIManager uiManager);

	public void handleClick(int x, int y) throws Exception;

	public Layout getLayout();

	public void setLayout(Layout layout);

	public LayoutOptions getLayoutOptions();

	public void setLayoutOptions(LayoutOptions options);

	public boolean isFocusable();

	public void setFocusable(boolean focusable);
}
