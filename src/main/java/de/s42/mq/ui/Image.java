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
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.data.ColorData;
import de.s42.mq.materials.*;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureWrap;
import de.s42.mq.meshes.Quad;
import de.s42.mq.shaders.BasicShader;
import de.s42.mq.shaders.Shader;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import java.nio.file.Path;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 * Represents a simple image in the scene.
 * @author Benjamin Schiller
 */
public class Image extends Quad implements UIComponent
{

	private final static Logger log = LogManager.getLogger(Image.class.getName());

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "texture", "buffer", "material" })
	protected Path source;

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "texture", "source", "material" })
	protected FXBuffer buffer;

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "source", "buffer", "material" })
	protected Texture texture;
	
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData tint = new ColorData();
	
	@AttributeDL(required = false)
	protected Layout layout;
	
	@AttributeDL(required = false)
	protected LayoutOptions layoutOptions;
	
	
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

	@AttributeDL(required = false)
	protected UIManager uiManager;
	
	protected boolean textureLoaded;
	protected float originalWidth;

	@Override
	public Image copy()
	{
		Image copy = (Image) super.copy();

		// @todo finalize proper copying
		copy.uiManager = uiManager;
		copy.source = source;
		copy.buffer = buffer;
		copy.texture = texture;
		copy.layout = layout;
		copy.layoutOptions = layoutOptions;
		copy.smooth = smooth;
		copy.screenUvs = screenUvs;
		copy.tint.setValue(tint.getValue());

		return copy;
	}

	public void setOptions(ImageOptions options, boolean useIdentifierMaterial)
	{
		assert options != null;

		setTint(options.getTint());
		setSmooth(options.isSmooth());
		setScreenUvs(options.isScreenUvs());
		if (!useIdentifierMaterial) {
			setMaterial(options.getMaterial());
		} else {
			setMaterial(options.getIdentifierMaterial());
		}
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		originalWidth = getScale().x;

		log.info("Loading image " + getName());

		// load texture from buffer
		if (buffer != null) {
			texture = buffer.getTexture();
		}

		// load a texture from path
		if (source != null && texture == null) {

			texture = new Texture(source);

			texture.setWrapR(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapS(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapT(TextureWrap.CLAMP_TO_EDGE);

			if (isSmooth()) {
				texture.setGenerateMipMap(true);
				texture.setEnableAnisotropic(true);
				texture.setMaxMipLevel(3);
				texture.setMaxLod(3);
				texture.setMinFilter(TextureFiltering.LINEAR_MIPMAP_LINEAR);
				texture.setMagFilter(TextureFiltering.LINEAR);
			} else {
				texture.setGenerateMipMap(false);
				texture.setEnableAnisotropic(false);
				texture.setMaxMipLevel(0);
				texture.setMaxLod(0);
				texture.setMinFilter(TextureFiltering.NEAREST);
				texture.setMagFilter(TextureFiltering.NEAREST);
			}

			texture.load();
			textureLoaded = true;
		}

		//if (isScreenUvs()) {
		setDynamicVertices(true);
		//}

		super.load();
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		if (textureLoaded) {
			texture.unload();
			texture = null;
			textureLoaded = false;
		}

		super.unload();
	}

	@Override
	public void render()
	{
		// make sure the texture gets set from the buffer
		if (buffer != null) {
			texture = buffer.getTexture();
		}

		//set texture into shader if given BasicShade		
		Texture tex = getTexture();
		if (tex != null) {

			Material mat = getMaterial();

			assert mat != null;

			Shader shader = mat.getShader();

			//@todo how to make sharing of a texture into shading/materials more generic?
			if (shader instanceof BasicShader) {
				((BasicShader) shader).setBaseTexture(tex);
				((BasicShader) shader).setTint(getTint());
			}
		}

		// apply layout if given
		Layout lay = getLayout();
		if (lay != null) {
			lay.layout(this, layoutOptions);
		}

		super.render();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public boolean isTextureLoaded()
	{
		return textureLoaded;
	}

	public Layout getLayout()
	{
		return layout;
	}

	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	public LayoutOptions getLayoutOptions()
	{
		return layoutOptions;
	}

	public void setLayoutOptions(LayoutOptions layoutOptions)
	{
		this.layoutOptions = layoutOptions;
	}

	public boolean isSmooth()
	{
		return smooth;
	}

	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}

	public FXBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FXBuffer buffer)
	{
		this.buffer = buffer;
	}

	public boolean isScreenUvs()
	{
		return screenUvs;
	}

	public void setScreenUvs(boolean screenUvs)
	{
		this.screenUvs = screenUvs;
	}

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
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
	// "Getters/Setters" </editor-fold>	
}
