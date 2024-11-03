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
package de.s42.mq.ui.layout.uilayout;

import de.s42.base.strings.StringHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.ui.layout.LayoutOptions;
import org.joml.Vector2f;

/**
 *
 * @author Benjamin Schiller
 */
public class UILayoutOptions implements LayoutOptions
{

	@AttributeDL(required = true)
	protected final Vector2f leftTop = new Vector2f();

	@AttributeDL(required = false)
	protected final Vector2f leftTopAnchor = new Vector2f(UILayoutAnchors.CENTER_CENTER);

	@AttributeDL(required = true)
	protected final Vector2f rightBottom = new Vector2f();

	@AttributeDL(required = false)
	protected final Vector2f rightBottomAnchor = new Vector2f(UILayoutAnchors.CENTER_CENTER);

	@AttributeDL(required = false, defaultValue = "NONE")
	protected UILayoutFit fit = UILayoutFit.NONE;

	@Override
	public UILayoutOptions copy()
	{
		UILayoutOptions copy = new UILayoutOptions();

		copy.fit = fit;
		copy.leftTop.set(leftTop);
		copy.leftTopAnchor.set(leftTopAnchor);
		copy.rightBottom.set(rightBottom);
		copy.rightBottomAnchor.set(rightBottomAnchor);

		return copy;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Vector2f getLeftTop()
	{
		return new Vector2f(leftTop);
	}

	public void setLeftTop(Vector2f leftTop)
	{
		assert leftTop != null : "leftTop != null";

		this.leftTop.set(leftTop);
	}

	public Vector2f getLeftTopAnchor()
	{
		return new Vector2f(leftTopAnchor);
	}

	public void setLeftTopAnchor(Vector2f leftTopAnchor)
	{
		assert leftTopAnchor != null : "leftTopAnchor != null";

		this.leftTopAnchor.set(leftTopAnchor);
	}

	public Vector2f getRightBottom()
	{
		return new Vector2f(rightBottom);
	}

	public void setRightBottom(Vector2f rightBottom)
	{
		assert rightBottom != null : "rightBottom != null";

		this.rightBottom.set(rightBottom);
	}

	public Vector2f getRightBottomAnchor()
	{
		return new Vector2f(rightBottomAnchor);
	}

	public void setRightBottomAnchor(Vector2f rightBottomAnchor)
	{
		assert rightBottomAnchor != null : "rightBottomAnchor != null";

		this.rightBottomAnchor.set(rightBottomAnchor);
	}

	public UILayoutFit getFit()
	{
		return fit;
	}

	public void setFit(UILayoutFit fit)
	{
		assert fit != null : "fit != null";

		this.fit = fit;
	}
	// "Getters/Setters" </editor-fold>

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
}
