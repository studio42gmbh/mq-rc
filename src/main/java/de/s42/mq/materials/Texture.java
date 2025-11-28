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
package de.s42.mq.materials;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.assets.AbstractAsset;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import org.joml.Vector2f;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GLCapabilities;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Benjamin Schiller
 */
public class Texture extends AbstractAsset
{

	private final static Logger log = LogManager.getLogger(Texture.class.getName());

	@isFile
	protected Path source;

	@AttributeDL(defaultValue = "REPEAT")
	protected TextureWrap wrapR = TextureWrap.REPEAT;

	@AttributeDL(defaultValue = "REPEAT")
	protected TextureWrap wrapS = TextureWrap.REPEAT;

	@AttributeDL(defaultValue = "REPEAT")
	protected TextureWrap wrapT = TextureWrap.REPEAT;

	@AttributeDL(defaultValue = "RGBA")
	protected TextureFormat internalFormat = TextureFormat.RGBA;

	@AttributeDL(defaultValue = "RGBA")
	protected TextureFormat format = TextureFormat.RGBA;

	@AttributeDL(defaultValue = "UNSIGNED_BYTE")
	protected TextureType dataType = TextureType.UNSIGNED_BYTE;

	@AttributeDL(defaultValue = "LINEAR_MIPMAP_LINEAR")
	protected TextureFiltering minFilter = TextureFiltering.LINEAR_MIPMAP_LINEAR;

	@AttributeDL(defaultValue = "LINEAR")
	protected TextureFiltering magFilter = TextureFiltering.LINEAR;

	@AttributeDL(defaultValue = "true")
	protected boolean enableAnisotropic = true;

	@AttributeDL(defaultValue = "true")
	protected boolean generateMipMap = true;

	@AttributeDL(defaultValue = "6")
	protected int maxMipLevel = 6;

	@AttributeDL(defaultValue = "0")
	protected int minLod = 0;

	@AttributeDL(defaultValue = "6")
	protected int maxLod = 6;

	@AttributeDL(defaultValue = "-0.5")
	protected float lodBias = -0.5f; // little sharper

	protected MQColor borderColor = new MQColor();

	protected int width;
	protected int height;

	@dontPersist
	protected int textureId = -1;

	public enum TextureFormat
	{
		RED(GL_RED, 1),
		RGBA(GL_RGBA, 4),
		R8(GL_R8, 1),
		R16(GL_R16, 1),
		R16F(GL_R16F, 1),
		RGB16F(GL_RGB16F, 3),
		R32F(GL_R32F, 1),
		RGB32F(GL_RGB32F, 3),
		RGB(GL_RGB, 3),
		RGBA16F(GL_RGBA16F, 4),
		RGBA32F(GL_RGBA32F, 4),
		DEPTH_COMPONENT(GL_DEPTH_COMPONENT, 1),
		DEPTH_COMPONENT16(GL_DEPTH_COMPONENT16, 1),
		DEPTH_COMPONENT24(GL_DEPTH_COMPONENT24, 1),
		DEPTH_COMPONENT32(GL_DEPTH_COMPONENT32, 1),
		DEPTH_COMPONENT32F(GL_DEPTH_COMPONENT32F, 1);

		public final int glFormat;
		public final int channels;

		private TextureFormat(int glFormat, int channels)
		{
			this.glFormat = glFormat;
			this.channels = channels;
		}
	}

	public enum TextureType
	{
		FLOAT(GL_FLOAT),
		UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
		SHORT(GL_SHORT),
		UNSIGNED_INT(GL_UNSIGNED_INT),
		INT(GL_INT),
		HALF_FLOAT(GL_HALF_FLOAT),
		BYTE(GL_BYTE),
		UNSIGNED_BYTE(GL_UNSIGNED_BYTE);

		public final int glFormat;

		private TextureType(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	public enum TextureWrap
	{
		REPEAT(GL_REPEAT),
		MIRRORED_REPEAT(GL_MIRRORED_REPEAT),
		CLAMP_TO_BORDER(GL_CLAMP_TO_BORDER),
		CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE);

		public final int glFormat;

		private TextureWrap(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	public enum TextureFiltering
	{
		NEAREST(GL_NEAREST),
		LINEAR(GL_LINEAR),
		NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
		LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
		LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);

		public final int glFormat;

		private TextureFiltering(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	public Texture()
	{
	}

	public Texture(int textureId, int width, int height)
	{
		this.textureId = textureId;
		this.width = width;
		this.height = height;
	}

	public Texture(Path source)
	{
		this.source = source;
	}

	@Override
	public void load() throws DLException
	{
		assert getFormat() != null;
		assert getInternalFormat() != null;
		assert getDataType() != null;
		assert getWrapS() != null;
		assert getWrapT() != null;
		assert getWrapR() != null;
		assert getMagFilter() != null;
		assert getMinFilter() != null;

		if (isLoaded()) {
			return;
		}

		super.load();

		GLCapabilities caps = GL.createCapabilities();

		log.trace("Loading", getSource());

		try (MemoryStack frame = MemoryStack.stackPush()) {
			IntBuffer widthB = frame.mallocInt(1);
			IntBuffer heightB = frame.mallocInt(1);
			IntBuffer components = frame.mallocInt(1);
			Buffer data;

			// in case of loading hdr into non hdr buffers ...
			//stbi_hdr_to_ldr_gamma(10.0f);
			//stbi_hdr_to_ldr_scale(10.0f);
			textureId = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureId);

			if (getSource() != null) {

				if (dataType == TextureType.FLOAT) {
					try {
						data = stbi_loadf_from_memory(
							getAssetManager().getSourceAsByteBuffer(source), widthB, heightB, components,
							internalFormat.channels);
					} catch (IOException ex) {
						throw new RuntimeException("Error loading texture " + getSource() + " - " + ex.getMessage(), ex);
					}
				} else {
					try {
						data = stbi_load_from_memory(
							getAssetManager().getSourceAsByteBuffer(source), widthB, heightB, components,
							internalFormat.channels);
					} catch (IOException ex) {
						throw new RuntimeException("Error loading texture " + getSource() + " - " + ex.getMessage(), ex);
					}
				}
				width = widthB.get();
				height = heightB.get();

				if (dataType == TextureType.FLOAT) {
					glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.glFormat, width, height, 0, format.glFormat, dataType.glFormat, (FloatBuffer) data);
					stbi_image_free((FloatBuffer) data);
				} else {
					glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.glFormat, width, height, 0, format.glFormat, dataType.glFormat, (ByteBuffer) data);
					stbi_image_free((ByteBuffer) data);
				}
			} // create empty texture
			else {
				glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.glFormat, width, height, 0, format.glFormat, dataType.glFormat, 0);
			}

			if (enableAnisotropic && caps.GL_EXT_texture_filter_anisotropic) {
				float val = Math.min(4f, glGetFloat(GL_TEXTURE_MAX_ANISOTROPY_EXT));
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, val);
			}

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, wrapR.glFormat);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS.glFormat);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT.glFormat);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, minLod);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, maxLod);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, maxMipLevel);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, lodBias);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, getMinFilter().glFormat);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, getMagFilter().glFormat);

			glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor.getAsArray());

			if (generateMipMap) {
				glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);
				glGenerateMipmap(GL_TEXTURE_2D);
			}

			glBindTexture(GL_TEXTURE_2D, 0);
		}

		log.trace("Loaded success", getSource(), getWidth(), getHeight(), getTextureId());
	}

	public void generateMipMap()
	{
		assert isLoaded();

		if (isGenerateMipMap()) {

			glBindTexture(GL_TEXTURE_2D, textureId);
			glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);
			glGenerateMipmap(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		glDeleteTextures(textureId);
		textureId = -1;

		super.unload();
	}

	public void setHDRDefaults()
	{
		setInternalFormat(TextureFormat.RGBA16F);
		setFormat(TextureFormat.RGBA);
		setDataType(TextureType.FLOAT);
		setWrapR(TextureWrap.REPEAT);
		setWrapS(TextureWrap.REPEAT);
		setWrapT(TextureWrap.MIRRORED_REPEAT);
		setMagFilter(TextureFiltering.LINEAR);
		setMinFilter(TextureFiltering.LINEAR_MIPMAP_LINEAR);
		setEnableAnisotropic(true);
		setGenerateMipMap(true);
		setMaxMipLevel(7);
		setMaxLod(7);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Vector2f getDimension()
	{
		return new Vector2f(width, height);
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getTextureId()
	{
		return textureId;
	}

	public void setTextureId(int textureId)
	{
		this.textureId = textureId;
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

	public TextureFormat getInternalFormat()
	{
		return internalFormat;
	}

	public void setInternalFormat(TextureFormat internalFormat)
	{
		this.internalFormat = internalFormat;
	}

	public TextureFormat getFormat()
	{
		return format;
	}

	public void setFormat(TextureFormat format)
	{
		this.format = format;
	}

	public TextureType getDataType()
	{
		return dataType;
	}

	public void setDataType(TextureType dataType)
	{
		this.dataType = dataType;
	}

	public TextureWrap getWrapS()
	{
		return wrapS;
	}

	public void setWrapS(TextureWrap wrapS)
	{
		this.wrapS = wrapS;
	}

	public TextureWrap getWrapT()
	{
		return wrapT;
	}

	public void setWrapT(TextureWrap wrapT)
	{
		this.wrapT = wrapT;
	}

	public TextureWrap getWrapR()
	{
		return wrapR;
	}

	public void setWrapR(TextureWrap wrapR)
	{
		this.wrapR = wrapR;
	}

	public TextureFiltering getMagFilter()
	{
		return magFilter;
	}

	public void setMagFilter(TextureFiltering magFilter)
	{
		this.magFilter = magFilter;
	}

	public TextureFiltering getMinFilter()
	{
		return minFilter;
	}

	public void setMinFilter(TextureFiltering minFilter)
	{
		this.minFilter = minFilter;
	}

	public int getMaxMipLevel()
	{
		return maxMipLevel;
	}

	public void setMaxMipLevel(int maxMipLevel)
	{
		assert maxMipLevel >= 0;

		this.maxMipLevel = maxMipLevel;

		if (isLoaded()) {
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, maxMipLevel);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, maxMipLevel);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	public int getMinLod()
	{
		return minLod;
	}

	public void setMinLod(int minLod)
	{
		assert minLod >= 0;

		this.minLod = minLod;

		if (isLoaded()) {
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, minLod);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	public int getMaxLod()
	{
		return maxLod;
	}

	public void setMaxLod(int maxLod)
	{
		assert maxLod >= 0;

		this.maxLod = maxLod;

		if (isLoaded()) {
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, maxLod);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	public float getLodBias()
	{
		return lodBias;
	}

	public void setLodBias(float lodBias)
	{
		this.lodBias = lodBias;

		if (isLoaded()) {

			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, lodBias);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	public MQColor getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(MQColor borderColor)
	{
		this.borderColor = borderColor;
	}
	// </editor-fold>
}
