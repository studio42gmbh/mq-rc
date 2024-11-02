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
package de.s42.mq.buffers;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.MQColor;
import de.s42.mq.materials.Texture;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureFormat;
import de.s42.mq.materials.Texture.TextureType;
import de.s42.mq.materials.Texture.TextureWrap;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Benjamin Schiller
 */
public class FXBuffer extends FrameBuffer
{

	//private final static Logger log = LogManager.getLogger(FXBuffer.class.getName());
	@AttributeDL(required = false, defaultValue = "1.0")
	protected float widthScale = 1.0f;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float heightScale = 1.0f;

	@AttributeDL(required = false, defaultValue = "RGBA")
	protected TextureFormat format = TextureFormat.RGBA;

	@AttributeDL(required = false, defaultValue = "RGBA16F")
	protected TextureFormat internalFormat = TextureFormat.RGBA16F;

	@AttributeDL(required = false, defaultValue = "FLOAT")
	protected TextureType dataType = TextureType.FLOAT;

	@AttributeDL(required = false, defaultValue = "LINEAR")
	protected TextureFiltering minFilter = TextureFiltering.LINEAR;

	@AttributeDL(required = false, defaultValue = "LINEAR")
	protected TextureFiltering magFilter = TextureFiltering.LINEAR;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean enableAnisotropic = false;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean generateMipMap = false;

	@AttributeDL(required = false, defaultValue = "0")
	protected int maxMipLevel = 0;

	@AttributeDL(required = false, defaultValue = "0")
	protected int minLod = 0;

	@AttributeDL(required = false, defaultValue = "0")
	protected int maxLod = 0;

	@AttributeDL(required = false, defaultValue = "-0.5")
	protected float lodBias = -0.5f; // little sharper

	@AttributeDL(required = false)
	protected Texture texture;

	protected boolean textureCreated = false;

	public FXBuffer()
	{
	}

	public FXBuffer(Texture texture)
	{
		this.texture = texture;
	}

	@Override
	protected void loadRenderBuffers() throws DLException
	{
		if (texture == null) {
			texture = new Texture();
			texture.setFormat(format);
			texture.setInternalFormat(internalFormat);
			texture.setDataType(dataType);
			texture.setMinFilter(minFilter);
			texture.setMagFilter(magFilter);
			texture.setEnableAnisotropic(enableAnisotropic);
			texture.setGenerateMipMap(generateMipMap);
			texture.setWidth(getScaledWidth());
			texture.setHeight(getScaledHeight());
			texture.setWrapR(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapS(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapT(TextureWrap.CLAMP_TO_EDGE);
			texture.setMaxLod(maxLod);
			texture.setMinLod(minLod);
			texture.setMaxMipLevel(maxMipLevel);
			texture.setLodBias(lodBias);
			textureCreated = true;
		}

		if (!texture.isLoaded()) {
			texture.load();
		}

		if (!textureCreated) {
			setWidth(texture.getWidth());
			setHeight(texture.getHeight());
		}

		glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureId(), 0);

		attachments = new int[]{
			GL_COLOR_ATTACHMENT0
		};
	}

	@Override
	protected void unloadRenderBuffers() throws DLException
	{
		if (textureCreated && texture.isLoaded()) {
			texture.unload();
			texture = null;
		}
	}

	@Override
	public void startRender()
	{
		assert isLoaded();

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		glViewport(0, 0, getScaledWidth(), getScaledHeight());

		glDrawBuffers(attachments);

		if (isClearBuffer()) {
			MQColor cCol = getClearColor().getValue();
			glClearColor(cCol.r, cCol.g, cCol.b, cCol.a);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		}
	}

	@Override
	public void resize(int width, int height) throws DLException
	{
		// just resize if the texture was created - otherwise this buffer can not be resized
		if (textureCreated) {
			super.resize(width, height);
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getScaledWidth()
	{
		return (int) Math.ceil((float) getWidth() * getWidthScale());
	}

	public int getScaledHeight()
	{
		return (int) Math.ceil((float) getHeight() * getHeightScale());
	}

	public int getBufferTextureId()
	{
		assert texture != null;

		return texture.getTextureId();
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;

		if (texture != null) {
			setWidth(texture.getWidth());
			setHeight(texture.getHeight());
		}
	}

	public float getWidthScale()
	{
		return widthScale;
	}

	public void setWidthScale(float widthScale)
	{
		this.widthScale = widthScale;
	}

	public float getHeightScale()
	{
		return heightScale;
	}

	public void setHeightScale(float heightScale)
	{
		this.heightScale = heightScale;
	}

	public TextureFormat getFormat()
	{
		return format;
	}

	public void setFormat(TextureFormat format)
	{
		this.format = format;
	}

	public TextureFormat getInternalFormat()
	{
		return internalFormat;
	}

	public void setInternalFormat(TextureFormat internalFormat)
	{
		this.internalFormat = internalFormat;
	}

	public TextureType getDataType()
	{
		return dataType;
	}

	public void setDataType(TextureType dataType)
	{
		this.dataType = dataType;
	}

	public TextureFiltering getMinFilter()
	{
		return minFilter;
	}

	public void setMinFilter(TextureFiltering minFilter)
	{
		this.minFilter = minFilter;
	}

	public TextureFiltering getMagFilter()
	{
		return magFilter;
	}

	public void setMagFilter(TextureFiltering magFilter)
	{
		this.magFilter = magFilter;
	}

	public boolean isEnableAnisotropic()
	{
		return enableAnisotropic;
	}

	public void setEnableAnisotropic(boolean enableAnisotropic)
	{
		this.enableAnisotropic = enableAnisotropic;
	}

	public boolean isGenerateMipMap()
	{
		return generateMipMap;
	}

	public void setGenerateMipMap(boolean generateMipMap)
	{
		this.generateMipMap = generateMipMap;
	}

	public int getMaxMipLevel()
	{
		return maxMipLevel;
	}

	public void setMaxMipLevel(int maxMipLevel)
	{
		this.maxMipLevel = maxMipLevel;
	}

	public int getMinLod()
	{
		return minLod;
	}

	public void setMinLod(int minLod)
	{
		this.minLod = minLod;
	}

	public int getMaxLod()
	{
		return maxLod;
	}

	public void setMaxLod(int maxLod)
	{
		this.maxLod = maxLod;
	}

	public float getLodBias()
	{
		return lodBias;
	}

	public void setLodBias(float lodBias)
	{
		this.lodBias = lodBias;
	}
	// "Getters/Setters" </editor-fold>
}
