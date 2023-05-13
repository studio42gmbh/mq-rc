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
import de.s42.mq.buffers.*;
import de.s42.mq.materials.ShaderMaterial;
import de.s42.mq.meshes.*;
import de.s42.mq.shaders.Shader;
import de.s42.mq.ui.AbstractWindowTask;

/**
 *
 * @author Benjamin Schiller
 */
public class RenderShaderTask extends AbstractWindowTask
{

	protected FrameBuffer buffer;
	protected ScreenQuad screenQuad;
	protected Shader shader;
	protected ShaderMaterial shaderMaterial;

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

		shaderMaterial.setShader(shader);
		shaderMaterial.beforeRendering();
		shader.setMesh(screenQuad);
		shader.beforeRendering();

		// reset to screen size
		if (buffer == null) {
			window.setRenderViewportToWindow();
		}

		screenQuad.render();

		shader.afterRendering();
		shaderMaterial.afterRendering();

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

	public Shader getShader()
	{
		return shader;
	}

	public void setShader(Shader shader)
	{
		this.shader = shader;
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
