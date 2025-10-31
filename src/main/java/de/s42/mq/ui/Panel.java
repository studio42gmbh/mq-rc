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
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.data.Vector4Data;
import de.s42.mq.meshes.Quad;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;

/**
 * Represents a simple panel in the scene.
 *
 * @author Benjamin Schiller
 */
public class Panel extends Quad implements UIComponent
{

	private final static Logger log = LogManager.getLogger(Panel.class.getName());

	/**
	 * if no layout is given position and scale shall remain untouched
	 */
	@AttributeDL(required = false)
	protected Layout layout;

	@AttributeDL(required = false)
	protected LayoutOptions layoutOptions;

	@AttributeDL(required = false)
	protected Vector2Data dimensionUI = new Vector2Data();

	// border settings
	/**
	 * x = lefttop, y = righttop, z = rightbottom, w = leftbottom
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected Vector4Data borderRadius = new Vector4Data();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData backgroundColor = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData borderColor = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected FloatData borderWidth = new FloatData();

	@AttributeDL(required = false)
	protected UIManager uiManager;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean focusable = false;

	public void setOptions(PanelOptions options)
	{
		assert options != null;

		backgroundColor.setValue(options.getBackgroundColor().getValue());
		//setBackgroundColor(options.getBackgroundColor());
		setBorderRadius(options.getBorderRadius());
		setBorderColor(options.getBorderColor());
		setBorderWidth(options.getBorderWidth());
		setMaterial(options.getMaterial());
	}

	@Override
	public Panel copy()
	{
		Panel copy = (Panel) super.copy();

		// @todo finalize proper copying
		copy.uiManager = uiManager;
		copy.layout = layout;
		copy.layoutOptions = (layoutOptions != null) ? layoutOptions.copy() : null;
		copy.dimensionUI.setValue(dimensionUI.getValue());
		copy.backgroundColor.setValue(backgroundColor.getValue());
		copy.borderRadius.setValue(borderRadius.getValue());
		copy.borderColor.setValue(borderColor.getValue());
		copy.borderWidth.setValue(borderWidth.getValue());

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		log.trace("Loading panel " + getName());

		setDynamicVertices(true);

		super.load();
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
	public void render(RenderContext context)
	{
		assert getLayout() != null : "getLayout() != null for " + this;
		assert getLayoutOptions() != null : "getLayoutOptions() != null for " + this;

		// apply layout if given
		Layout lay = getLayout();
		if (lay != null) {
			lay.layout(this, getLayoutOptions());
		}

		super.render(context);
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		// do nothing
	}

	@Override
	public void handleChar(String chars)
	{
	}

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		// do nothing
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public Layout getLayout()
	{
		return layout;
	}

	@Override
	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	@Override
	public LayoutOptions getLayoutOptions()
	{
		return layoutOptions;
	}

	@Override
	public void setLayoutOptions(LayoutOptions layoutOptions)
	{
		this.layoutOptions = layoutOptions;
	}

	public Vector2Data getDimensionUI()
	{
		return dimensionUI;
	}

	public void setDimensionUI(Vector2Data dimensionUI)
	{
		this.dimensionUI = dimensionUI;
	}

	public ColorData getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(ColorData backgroundColor)
	{
		this.backgroundColor.setValue(backgroundColor.getValue());
	}

	public Vector4Data getBorderRadius()
	{
		return borderRadius;
	}

	public void setBorderRadius(Vector4Data borderRadius)
	{
		this.borderRadius = borderRadius;
	}

	public ColorData getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(ColorData borderColor)
	{
		this.borderColor = borderColor;
	}

	public FloatData getBorderWidth()
	{
		return borderWidth;
	}

	public void setBorderWidth(FloatData borderWidth)
	{
		this.borderWidth = borderWidth;
	}

	@Override
	public UIManager getUiManager()
	{
		return uiManager;
	}

	@Override
	public void setUiManager(UIManager uiManager)
	{
		this.uiManager = uiManager;
	}

	@Override
	public boolean isFocusable()
	{
		return focusable;
	}

	@Override
	public void setFocusable(boolean focusable)
	{
		this.focusable = focusable;
	}
	// "Getters/Setters" </editor-fold>
}
