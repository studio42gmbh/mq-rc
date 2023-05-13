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
package de.s42.mq.fonts;

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.MQColor;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.data.*;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;
import de.s42.mq.fonts.Text.HorizontalAlignment;
import de.s42.mq.fonts.Text.VerticalAlignment;
import de.s42.mq.materials.Material;

/**
 *
 * @author Benjamin Schiller
 */
public class TextOptions
{

	@AttributeDL(required = true)
	protected Font font;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData color = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.01")
	protected FloatData edge = new FloatData(0.5f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData color2 = new ColorData(new MQColor(1.0f));

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.01")
	protected FloatData edge2 = new FloatData(1.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected FloatData fontSize = new FloatData(1.0f);

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "-5.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "5.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.01")
	protected FloatData letterSpacing = new FloatData(0.0f);

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean scaleWithBufferSize = false;

	@AttributeDL(required = false, defaultValue = "TOP")
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

	@AttributeDL(required = false, defaultValue = "LEFT")
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

	@AttributeDL(required = false, defaultValue = "100")
	protected int maxCharCount = 100;

	@AttributeDL(required = true)
	protected Material material;

	@AttributeDL(required = true)
	protected FrameBuffer buffer;

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public ColorData getColor()
	{
		return color;
	}

	public void setColor(ColorData color)
	{
		this.color = color;
	}

	public FloatData getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(FloatData fontSize)
	{
		this.fontSize = fontSize;
	}

	public boolean isScaleWithBufferSize()
	{
		return scaleWithBufferSize;
	}

	public void setScaleWithBufferSize(boolean scaleWithBufferSize)
	{
		this.scaleWithBufferSize = scaleWithBufferSize;
	}

	public int getMaxCharCount()
	{
		return maxCharCount;
	}

	public void setMaxCharCount(int maxCharCount)
	{
		this.maxCharCount = maxCharCount;
	}

	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}

	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public FloatData getEdge()
	{
		return edge;
	}

	public void setEdge(FloatData edge)
	{
		this.edge = edge;
	}

	public ColorData getColor2()
	{
		return color2;
	}

	public void setColor2(ColorData color2)
	{
		this.color2 = color2;
	}

	public FloatData getEdge2()
	{
		return edge2;
	}

	public void setEdge2(FloatData edge2)
	{
		this.edge2 = edge2;
	}

	public FloatData getLetterSpacing()
	{
		return letterSpacing;
	}

	public void setLetterSpacing(FloatData letterSpacing)
	{
		this.letterSpacing = letterSpacing;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}
	// "Getters/Setters" </editor-fold>	
}
