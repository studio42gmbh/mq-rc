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
import de.s42.mq.data.ColorData;
import de.s42.mq.materials.Material;

/**
 * Represents settings for a simple image in the scene.
 *
 * @author Benjamin Schiller
 */
public class ImageOptions
{

	/**
	 * if set to true its generating mipmaps and using LINEAR_MIP_LINEAR etc; smooth = false is good for precise pixel
	 * graphics
	 */
	@AttributeDL(required = false, defaultValue = "true")
	protected boolean smooth = true;

	/**
	 * if set to true layouters shall change the uvs to match the screencoordinates in uv space (left,top = 0,0,
	 * right,bottom = 1,1)
	 */
	@AttributeDL(required = false, defaultValue = "false")
	protected boolean screenUvs = false;

	@AttributeDL(required = true)
	protected Material material;

	/**
	 * is currently used to generate a identifier layer rending
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = PreliminaryDLAnnotation.DEFAULT_SYMBOL)
	protected Material identifierMaterial;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData tint = new ColorData();

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public boolean isSmooth()
	{
		return smooth;
	}

	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}

	public boolean isScreenUvs()
	{
		return screenUvs;
	}

	public void setScreenUvs(boolean screenUvs)
	{
		this.screenUvs = screenUvs;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public Material getIdentifierMaterial()
	{
		return identifierMaterial;
	}

	public void setIdentifierMaterial(Material identifierMaterial)
	{
		this.identifierMaterial = identifierMaterial;
	}

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
	}
	// "Getters/Setters" </editor-fold>	
}
