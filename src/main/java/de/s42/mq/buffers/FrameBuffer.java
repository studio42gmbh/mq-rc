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
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.ColorData;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Benjamin Schiller
 */
public class FrameBuffer extends AbstractAsset
{

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean clearBuffer = false;

	@AttributeDL(required = false)
	protected ColorData clearColor = new ColorData(new MQColor(0.0f, 0.0f, 0.0f, 0.0f));

	@AttributeDL(required = false, defaultValue = "1")
	protected int width;

	@AttributeDL(required = false, defaultValue = "1")
	protected int height;

	protected int fbo = -1;
	protected int[] attachments;

	public FrameBuffer()
	{
	}

	protected void loadRenderBuffers() throws DLException
	{
		// do load your render buffers

		/*i.e. colorRenderBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorRenderBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorRenderBuffer, 0);

		int attachments[] = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4};
		glDrawBuffers(attachments);
		 */
		attachments = new int[]{};
	}

	protected void unloadRenderBuffers() throws DLException
	{
		// do unload your render buffers
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glDisable(GL_FRAMEBUFFER_SRGB);

		loadRenderBuffers();

		//int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		//assert fboStatus == GL_FRAMEBUFFER_COMPLETE : "Could not create FBO " + fboStatus;
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		unloadRenderBuffers();
		glDeleteFramebuffers(fbo);
		fbo = -1;

		super.unload();
	}

	public void resize(int width, int height) throws DLException
	{
		assert width > 0;
		assert height > 0;

		setWidth(width);
		setHeight(height);

		unload();

		load();
	}

	public void startRender()
	{
		assert isLoaded();

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		glViewport(0, 0, getWidth(), getHeight());

		glDrawBuffers(attachments);

		if (isClearBuffer()) {
			MQColor cCol = getClearColor().getValue();
			glClearColor(cCol.r, cCol.g, cCol.b, cCol.a);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		}
	}

	public void clearBuffer()
	{
		assert isLoaded();
		assert getClearColor() != null;

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		glViewport(0, 0, getWidth(), getHeight());
		glDrawBuffers(attachments);
		MQColor cCol = getClearColor().getValue();
		glClearColor(cCol.r, cCol.g, cCol.b, cCol.a);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}

	public void endRender()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		glDrawBuffers(new int[]{});
	}

	public int getFbo()
	{
		return fbo;
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

	public boolean isClearBuffer()
	{
		return clearBuffer;
	}

	public void setClearBuffer(boolean clearBuffer)
	{
		this.clearBuffer = clearBuffer;
	}

	public int[] getAttachments()
	{
		return attachments;
	}

	public ColorData getClearColor()
	{
		return clearColor;
	}

	public void setClearColor(ColorData clearColor)
	{
		this.clearColor = clearColor;
	}
}
