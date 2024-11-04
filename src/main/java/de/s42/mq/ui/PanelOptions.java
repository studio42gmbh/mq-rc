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
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector4Data;
import de.s42.mq.materials.Material;

/**
 *
 * @author Benjamin Schiller
 */
public class PanelOptions implements Copyable
{

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected final ColorData backgroundColor = new ColorData();

	/**
	 * x = lefttop, y = righttop, z = rightbottom, w = leftbottom
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected final Vector4Data borderRadius = new Vector4Data();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected final ColorData borderColor = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected final FloatData borderWidth = new FloatData();

	@AttributeDL(required = false)
	protected Material material;

	@Override
	public PanelOptions copy()
	{
		PanelOptions copy = new PanelOptions();

		copy.backgroundColor.setValue(backgroundColor.getValue());
		copy.borderRadius.setValue(borderRadius.getValue());
		copy.borderColor.setValue(borderColor.getValue());
		copy.borderWidth.setValue(borderWidth.getValue());
		copy.material = material;

		return copy;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public ColorData getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(ColorData backgroundColor)
	{
		this.backgroundColor.setValue(backgroundColor.getValue().copy());
	}

	public Vector4Data getBorderRadius()
	{
		return borderRadius;
	}

	public void setBorderRadius(Vector4Data borderRadius)
	{
		this.borderRadius.setValue(borderRadius.getValue());
	}

	public ColorData getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(ColorData borderColor)
	{
		this.borderColor.setValue(borderColor.getValue().copy());
	}

	public FloatData getBorderWidth()
	{
		return borderWidth;
	}

	public void setBorderWidth(FloatData borderWidth)
	{
		this.borderWidth.setValue(borderWidth.getValue());
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}
	// "Getters/Setters" </editor-fold>
}
