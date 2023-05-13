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
	protected Vector2f leftTop;

	@AttributeDL(required = false)
	protected Vector2f leftTopAnchor = UILayoutAnchors.CENTER_CENTER;

	@AttributeDL(required = true)
	protected Vector2f rightBottom;

	@AttributeDL(required = false)
	protected Vector2f rightBottomAnchor = UILayoutAnchors.CENTER_CENTER;

	@AttributeDL(required = false, defaultValue = "NONE")
	protected UILayoutFit fit = UILayoutFit.NONE;

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Vector2f getLeftTop()
	{
		return leftTop;
	}

	public void setLeftTop(Vector2f leftTop)
	{
		this.leftTop = leftTop;
	}

	public Vector2f getLeftTopAnchor()
	{
		return leftTopAnchor;
	}

	public void setLeftTopAnchor(Vector2f leftTopAnchor)
	{
		this.leftTopAnchor = leftTopAnchor;
	}

	public Vector2f getRightBottom()
	{
		return rightBottom;
	}

	public void setRightBottom(Vector2f rightBottom)
	{
		this.rightBottom = rightBottom;
	}

	public Vector2f getRightBottomAnchor()
	{
		return rightBottomAnchor;
	}

	public void setRightBottomAnchor(Vector2f rightBottomAnchor)
	{
		this.rightBottomAnchor = rightBottomAnchor;
	}

	public UILayoutFit getFit()
	{
		return fit;
	}

	public void setFit(UILayoutFit fit)
	{
		this.fit = fit;
	}
	// "Getters/Setters" </editor-fold>	

	@Override
	public String toString()
	{
		return "UILayoutOptions { "
			+ "leftTop: " + leftTop + "; leftTopAnchor : " + leftTopAnchor
			+ "; rightBottom : " + rightBottom + "; rightBottomAnchor : " + rightBottomAnchor
			+ "; }";
	}
}
