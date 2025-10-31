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
package de.s42.mq.rendering;

import de.s42.dl.exceptions.DLException;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.materials.ShaderMaterial;
import de.s42.mq.meshes.ScreenQuad;
import de.s42.mq.shaders.BasicFXShader;
import de.s42.mq.ui.AbstractWindowTask;

/**
 *
 * @author Benjamin Schiller
 */
public class RenderFXShaderTask extends AbstractWindowTask
{

	protected FrameBuffer buffer;
	protected ScreenQuad screenQuad;
	protected BasicFXShader shader;
	protected ShaderMaterial shaderMaterial;
	protected FXBuffer inBuffer;

	@Override
	protected void runTaskFirstTime() throws DLException
	{
		if (shaderMaterial == null) {
			shaderMaterial = new ShaderMaterial();
		}

		shaderMaterial.load();
	}

	@Override
	protected void runTask()
	{
		assert screenQuad != null;
		assert shader != null;

		// render main scene into gbuffer
		if (buffer != null) {
			buffer.startRender();
		}

		DefaultRenderContext context = new DefaultRenderContext();

		shaderMaterial.setShader(shader);
		shaderMaterial.beforeRendering(context);
		shader.setInBuffer(inBuffer);
		shader.setMesh(screenQuad);
		shader.beforeRendering(context);

		// reset to screen size
		if (buffer == null) {
			window.setRenderViewportToWindow();
		}

		screenQuad.render(context);

		shader.afterRendering(context);
		shader.setInBuffer(null);
		shaderMaterial.afterRendering(context);

		if (buffer != null) {
			buffer.endRender();
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}

	public ScreenQuad getScreenQuad()
	{
		return screenQuad;
	}

	public void setScreenQuad(ScreenQuad screenQuad)
	{
		this.screenQuad = screenQuad;
	}

	public BasicFXShader getShader()
	{
		return shader;
	}

	public void setShader(BasicFXShader shader)
	{
		this.shader = shader;
	}

	public FXBuffer getInBuffer()
	{
		return inBuffer;
	}

	public void setInBuffer(FXBuffer inBuffer)
	{
		this.inBuffer = inBuffer;
	}

	public ShaderMaterial getShaderMaterial()
	{
		return shaderMaterial;
	}

	public void setShaderMaterial(ShaderMaterial shaderMaterial)
	{
		this.shaderMaterial = shaderMaterial;
	}
	// "Getters/Setters" </editor-fold>
}
