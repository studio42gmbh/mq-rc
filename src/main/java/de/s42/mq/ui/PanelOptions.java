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

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.*;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;
import de.s42.mq.materials.Material;

/**
 *
 * @author Benjamin Schiller
 */
public class PanelOptions
{

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData backgroundColor = new ColorData();

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
	protected ColorData borderColor = new ColorData();

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected FloatData borderWidth = new FloatData();

	@AttributeDL(required = false)
	protected Material material;

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public ColorData getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(ColorData backgroundColor)
	{
		this.backgroundColor = backgroundColor;
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
