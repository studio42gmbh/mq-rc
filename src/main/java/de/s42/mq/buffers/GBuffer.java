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
import de.s42.mq.MQColor;
import de.s42.mq.materials.Texture;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Benjamin Schiller
 */
public class GBuffer extends FrameBuffer
{

	@AttributeDL(required = false)
	protected FXBuffer colorFXBuffer = new FXBuffer();

	protected int albedoRenderBuffer = -1;
	protected int positionRenderBuffer = -1;
	protected int normalRenderBuffer = -1;
	protected int specialRenderBuffer = -1;
	protected int irradianceRenderBuffer = -1;
	protected int depthRenderBuffer = -1;
	protected int colorRenderBuffer = -1;
	protected int identifierRenderBuffer = -1;

	@Override
	protected void loadRenderBuffers()
	{
		colorRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorRenderBuffer, 0);
		colorFXBuffer.setTexture(new Texture(colorRenderBuffer, getWidth(), getHeight()));

		albedoRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, albedoRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, albedoRenderBuffer, 0);

		normalRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, normalRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, normalRenderBuffer, 0);

		positionRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, positionRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, positionRenderBuffer, 0);

		specialRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, specialRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, specialRenderBuffer, 0);

		irradianceRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, irradianceRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, irradianceRenderBuffer, 0);

		depthRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, getWidth(), getHeight(), 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthRenderBuffer, 0);

		identifierRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, identifierRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R32I, getWidth(), getHeight(), 0, GL_RED_INTEGER, GL_INT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, identifierRenderBuffer, 0);

		// https://www.roxlu.com/2014/048/fast-pixel-transfers-with-pixel-buffer-objects
		/*identifierRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, identifierRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R32I, getWidth(), getHeight(), 0, GL_RED_INTEGER, GL_INT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, identifierRenderBuffer, 0);
		 */
		attachments = new int[]{
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2,
			GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4,
			GL_COLOR_ATTACHMENT5,
			//GL_DEPTH_STENCIL_ATTACHMENT,
			GL_COLOR_ATTACHMENT6
		};
	}

	@Override
	protected void unloadRenderBuffers()
	{
		if (isLoaded()) {
			glDeleteRenderbuffers(colorRenderBuffer);
			colorRenderBuffer = -1;
			glDeleteRenderbuffers(albedoRenderBuffer);
			albedoRenderBuffer = -1;
			glDeleteRenderbuffers(normalRenderBuffer);
			normalRenderBuffer = -1;
			glDeleteRenderbuffers(positionRenderBuffer);
			positionRenderBuffer = -1;
			glDeleteRenderbuffers(specialRenderBuffer);
			specialRenderBuffer = -1;
			glDeleteRenderbuffers(depthRenderBuffer);
			depthRenderBuffer = -1;
			glDeleteRenderbuffers(irradianceRenderBuffer);
			irradianceRenderBuffer = -1;
			glDeleteRenderbuffers(identifierRenderBuffer);
			identifierRenderBuffer = -1;
		}
	}

	@Override
	public void startRender()
	{
		assert isLoaded();

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		glViewport(0, 0, width, height);

		glDrawBuffers(attachments);

		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);

		if (isClearBuffer()) {
			MQColor cCol = getClearColor().getValue();
			glClearColor(cCol.r, cCol.g, cCol.b, cCol.a);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		}
	}

	public int getIdentifier(int x, int y)
	{
		// @todo this access causes to crash in renderdoc - how fix that?
		glBindFramebuffer(GL_FRAMEBUFFER, getFbo());
		glReadBuffer(GL_COLOR_ATTACHMENT6);
		int[] pixel = new int[1];
		pixel[0] = -1;
		glReadPixels(x, getHeight() - y, 1, 1, GL_RED_INTEGER, GL_INT, pixel);
		glBindFramebuffer(GL_FRAMEBUFFER, -1);

		return pixel[0];
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getAlbedoRenderBuffer()
	{
		return albedoRenderBuffer;
	}

	public int getPositionRenderBuffer()
	{
		return positionRenderBuffer;
	}

	public int getNormalRenderBuffer()
	{
		return normalRenderBuffer;
	}

	public int getSpecialRenderBuffer()
	{
		return specialRenderBuffer;
	}

	public int getDepthRenderBuffer()
	{
		return depthRenderBuffer;
	}

	public int getIrradianceRenderBuffer()
	{
		return irradianceRenderBuffer;
	}

	public int getColorRenderBuffer()
	{
		return colorRenderBuffer;
	}

	public FXBuffer getColorFXBuffer()
	{
		return colorFXBuffer;
	}

	public void setColorFXBuffer(FXBuffer colorFXBuffer)
	{
		this.colorFXBuffer = colorFXBuffer;
	}

	public int getIdentifierRenderBuffer()
	{
		return identifierRenderBuffer;
	}

	// </editor-fold>
}
