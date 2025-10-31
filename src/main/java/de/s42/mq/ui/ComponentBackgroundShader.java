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
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector4Data;
import de.s42.mq.rendering.RenderContext;

/**
 *
 * @author Benjamin Schiller
 */
public class ComponentBackgroundShader extends UIShader
{

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
	protected ColorData color = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData borderColor = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected FloatData borderWidth = new FloatData();

	protected int borderRadiusUniform = -1;
	protected int borderWidthUniform = -1;
	protected int borderColorUniform = -1;
	protected int colorUniform = -1;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("baseSampler", 0);

		borderRadiusUniform = getUniformLocationOpt("borderRadius");
		borderColorUniform = getUniformLocationOpt("borderColor");
		borderWidthUniform = getUniformLocationOpt("borderWidth");
		colorUniform = getUniformLocationOpt("color");
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		assert camera != null;
		assert mesh != null;

		super.beforeRendering(context);

		if (mesh instanceof Panel panel) {
			setUniform(colorUniform, panel.getBackgroundColor());
			setUniform(borderRadiusUniform, panel.getBorderRadius());
			setUniform(borderColorUniform, panel.getBorderColor());
			setUniform(borderWidthUniform, panel.getBorderWidth());
		} else if (mesh instanceof Image image) {

			setTextureOpt(image.getTexture(), 0);

			setUniform(colorUniform, color);
			setUniform(borderRadiusUniform, image.getBorderRadius());
			setUniform(borderColorUniform, borderColor);
			setUniform(borderWidthUniform, borderWidth);
		} else {
			setUniform(colorUniform, color);
			setUniform(borderRadiusUniform, borderRadius);
			setUniform(borderColorUniform, borderColor);
			setUniform(borderWidthUniform, borderWidth);
		}
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		setTexture(0, 0);

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Vector4Data getBorderRadius()
	{
		return borderRadius;
	}

	public void setBorderRadius(Vector4Data borderRadius)
	{
		this.borderRadius = borderRadius;
	}

	public int getBorderRadiusUniform()
	{
		return borderRadiusUniform;
	}

	public ColorData getColor()
	{
		return color;
	}

	public void setColor(ColorData color)
	{
		this.color = color;
	}

	public int getBorderColorUniform()
	{
		return borderColorUniform;
	}

	public void setBorderColorUniform(int borderColorUniform)
	{
		this.borderColorUniform = borderColorUniform;
	}

	public int getColorUniform()
	{
		return colorUniform;
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
	// </editor-fold>
}
