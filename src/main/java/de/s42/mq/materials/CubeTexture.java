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
import de.s42.dl.exceptions.DLException;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.cameras.PerspectiveCamera;
import de.s42.mq.meshes.Cube;
import de.s42.mq.rendering.DefaultRenderContext;
import de.s42.mq.shaders.EquirectangularToCubemapShader;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP_HINT;
import static org.lwjgl.opengl.GL30.*;

/**
 * https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/2.2.1.ibl_specular/ibl_specular.cpp
 *
 * @author Benjamin Schiller
 */
public class CubeTexture extends Texture
{

	//private final static Logger log = LogManager.getLogger(CubeTexture.class.getName());
	@AttributeDL(required = true)
	protected Texture equirectangularTexture;

	@AttributeDL(ignore = true)
	protected EquirectangularToCubemapShader equirectangularToCubemapShader;

	public CubeTexture()
	{
		super();

	}

	public CubeTexture(int width, int height, int maxMipLevel)
	{
		super();

		assert width > 0;
		assert height > 0;
		assert maxMipLevel >= 0;
		assert width >> maxMipLevel > 0;
		assert height >> maxMipLevel > 0;

		this.maxMipLevel = maxMipLevel;
		this.width = width;
		this.height = height;
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		assert equirectangularTexture != null;
		assert equirectangularTexture.isLoaded();

		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
		for (int i = 0; i < 6; ++i) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, getWidth(), getHeight(), 0, GL_RGB, GL_FLOAT, 0);
		}
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, wrapS.glFormat);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, wrapT.glFormat);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, wrapR.glFormat);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, minFilter.glFormat);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, magFilter.glFormat);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_LOD, minLod);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LOD, maxMipLevel);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, maxMipLevel);

		Vector3f lookAts[] = {
			new Vector3f(1.0f, 0.0f, 0.0f),
			new Vector3f(-1.0f, 0.0f, 0.0f),
			new Vector3f(0.0f, 1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, 0.0f, 1.0f),
			new Vector3f(0.0f, 0.0f, -1.0f)
		};

		Vector3f ups[] = {
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, 0.0f, 1.0f),
			new Vector3f(0.0f, 0.0f, -1.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f)
		};

		Camera camera = new PerspectiveCamera(90.0f, 0.1f, 3.0f, 1.0f);

		equirectangularToCubemapShader.setEquirectangularTexture(equirectangularTexture);
		equirectangularToCubemapShader.setCamera(camera);
		equirectangularToCubemapShader.load();

		FrameBuffer frameBuffer = new FrameBuffer();
		frameBuffer.setClearBuffer(false);
		frameBuffer.setWidth(getWidth());
		frameBuffer.setHeight(getHeight());
		frameBuffer.load();

		Cube cube = new Cube();
		ShaderMaterial material = new ShaderMaterial(equirectangularToCubemapShader);
		material.setCamera(camera);
		material.load();
		cube.setMaterial(material);
		cube.load();
		cube.setCamera(camera);

		generateMipMap();

		float mm = (float) getMaxMipLevel();
		if (getMaxMipLevel() == 0) {
			mm = 1.0f;
		}

		for (int m = 0; m <= getMaxMipLevel(); ++m) {

			equirectangularToCubemapShader.setFilterStrength((float) m / mm);

			frameBuffer.setWidth(getWidth() >> m);
			frameBuffer.setHeight(getHeight() >> m);

			frameBuffer.startRender();

			for (int i = 0; i < 6; ++i) {
				camera.getCameraLookAt().set(lookAts[i]);
				camera.getCameraUp().set(ups[i]);
				camera.setUpdateViewMatrix(true);
				camera.update();

				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, textureId, m);

				DefaultRenderContext context = new DefaultRenderContext();
				cube.render(context);
			}
		}

		frameBuffer.endRender();
		frameBuffer.unload();
		equirectangularToCubemapShader.unload();
	}

	@Override
	public void generateMipMap()
	{
		assert isLoaded();

		if (isGenerateMipMap()) {

			glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
			glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);
			glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		}
	}

	public Texture getEquirectangularTexture()
	{
		return equirectangularTexture;
	}

	public void setEquirectangularTexture(Texture equirectangularTexture)
	{
		this.equirectangularTexture = equirectangularTexture;
	}

	public EquirectangularToCubemapShader getEquirectangularToCubemapShader()
	{
		return equirectangularToCubemapShader;
	}

	public void setEquirectangularToCubemapShader(EquirectangularToCubemapShader equirectangularToCubemapShader)
	{
		this.equirectangularToCubemapShader = equirectangularToCubemapShader;
	}
}
