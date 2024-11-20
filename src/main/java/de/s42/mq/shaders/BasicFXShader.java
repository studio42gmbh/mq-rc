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
package de.s42.mq.shaders;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.buffers.FXBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

/**
 *
 * @author Benjamin Schiller
 */
public class BasicFXShader extends Shader
{

	@AttributeDL(required = false)
	protected FXBuffer inBuffer;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean samplerActive = false;

	protected int inBufferResolutionUniform = -1;
	protected int samplerId = -1;

	@Override
	protected void loadShader()
	{
		setUniform("inBuffer", 0);
		inBufferResolutionUniform = getUniformLocationOpt("inBufferResolution");

		samplerId = glGenSamplers();
		glSamplerParameteri(samplerId, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glSamplerParameteri(samplerId, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		if (inBuffer != null) {

			setUniform(inBufferResolutionUniform, 1.0f / (float) inBuffer.getScaledWidth(), 1.0f / (float) inBuffer.getScaledHeight());
			setTexture(inBuffer, 0);

			if (samplerActive) {
				glBindSampler(0, samplerId);
			}
		}

		setDraw1ColorAttachment();

	}

	@Override
	public void afterRendering()
	{
		if (inBuffer != null) {
			unsetTexture(0);

			if (samplerActive) {
				glBindSampler(0, 0);
			}
		}

		super.afterRendering();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public FXBuffer getInBuffer()
	{
		return inBuffer;
	}

	public void setInBuffer(FXBuffer inBuffer)
	{
		this.inBuffer = inBuffer;
	}

	public int getInBufferResolutionUniform()
	{
		return inBufferResolutionUniform;
	}

	public boolean isSamplerActive()
	{
		return samplerActive;
	}

	public void setSamplerActive(boolean samplerActive)
	{
		this.samplerActive = samplerActive;
	}

	public int getSamplerId()
	{
		return samplerId;
	}
	// </editor-fold>
}
