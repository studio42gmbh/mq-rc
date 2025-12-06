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

import de.s42.base.strings.StringHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.Vector2Data;
import static de.s42.mq.util.GLFWHelper.getImageBufferFromImagePath;
import java.io.IOException;
import java.nio.file.Path;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import static org.lwjgl.opengl.NVMeshShader.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Window extends AbstractAsset
{

	private final static Logger log = LogManager.getLogger(Window.class.getName());

	@AttributeDL(required = true)
	protected int width;

	@AttributeDL(required = true)
	protected int height;

	@AttributeDL(required = false, defaultValue = "-1")
	protected int left = -1;

	@AttributeDL(required = true, defaultValue = "-1")
	protected int top = -1;

	@AttributeDL(required = false, defaultValue = "-")
	protected String title = "-";

	@AttributeDL(required = false)
	@isFile
	protected Path icon;

	@AttributeDL(required = false, defaultValue = "false")
	protected boolean fullScreen = false;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean decorated = true;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean vSync = true;

	@AttributeDL(required = false, defaultValue = "60")
	protected int vSyncRate = 60;

	@AttributeDL(required = false)
	protected Cursor cursor;

	@AttributeDL(required = false)
	protected Cursor defaultCursor;

	@AttributeDL(required = false)
	protected Vector2Data dimension = new Vector2Data();

	protected GLCapabilities capabilities;
	protected boolean resized;
	private long glfwWindowHandle = -1;
	private boolean shouldClose;

	public void swapBuffer()
	{
		assert isLoaded();

		// display in window
		glfwSwapBuffers(glfwWindowHandle);
	}

	public boolean shouldClose()
	{
		assert isLoaded();

		// The state variable is necessary as glfwWindowShouldClose does return false if the window is already closed
		return shouldClose | glfwWindowShouldClose(glfwWindowHandle);
	}

	public void requestCloseWindow()
	{
		assert isLoaded();

		shouldClose = true;
		glfwSetWindowShouldClose(glfwWindowHandle, shouldClose);
	}

	public void activateForRendering()
	{
		assert isLoaded();

		glfwMakeContextCurrent(glfwWindowHandle);

		if (capabilities == null) {
			capabilities = GL.createCapabilities();

			log.info("OpenGL version", glGetString(GL_VERSION));

			log.trace("OpenGL capabilities", StringHelper.toString(capabilities));

			log.info("GL_NV_mesh_shader", capabilities.GL_NV_mesh_shader);
			log.info("GL_MAX_MESH_WORK_GROUP_SIZE_NV X", GL45.glGetIntegeri(GL_MAX_MESH_WORK_GROUP_SIZE_NV, 0));
			log.info("GL_MAX_MESH_WORK_GROUP_SIZE_NV Y", GL45.glGetIntegeri(GL_MAX_MESH_WORK_GROUP_SIZE_NV, 1));
			log.info("GL_MAX_MESH_WORK_GROUP_SIZE_NV Z", GL45.glGetIntegeri(GL_MAX_MESH_WORK_GROUP_SIZE_NV, 2));
			log.info("GL_MAX_DRAW_MESH_TASKS_COUNT_NV", GL45.glGetInteger(GL_MAX_DRAW_MESH_TASKS_COUNT_NV));
			log.info("GL_MAX_MESH_OUTPUT_VERTICES_NV", GL45.glGetInteger(GL_MAX_MESH_OUTPUT_VERTICES_NV));
			log.info("GL_MAX_MESH_OUTPUT_PRIMITIVES_NV", GL45.glGetInteger(GL_MAX_MESH_OUTPUT_PRIMITIVES_NV));
		}
	}

	public void setRenderViewportToWindow()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, getWidth(), getHeight());

		// This iss from the glfw help
		// https://www.glfw.org/docs/3.3/window_guide.html#window_attributes
		glDrawBuffers(new int[]{
			GL_BACK_LEFT
		});
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		if (getDefaultCursor() != null) {
			getDefaultCursor().load();
		}

		super.load();
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		capabilities = null;
		GL.setCapabilities(null);
		glfwMakeContextCurrent(0);

		if (getDefaultCursor() != null) {
			getDefaultCursor().unload();
		}

		super.unload();
	}

	public float getAspectRatio()
	{
		return (float) getWidth() / getHeight();
	}

	@AttributeDL(ignore = true)
	public GLFWImage.Buffer getImageBufferFromIcon() throws IOException
	{
		return getImageBufferFromImagePath(getIcon());
	}

	public void setCursor(Cursor cursor)
	{
		if (this.cursor == cursor) {
			return;
		}

		this.cursor = cursor;

		if (cursor != null) {
			glfwSetCursor(glfwWindowHandle, cursor.getCursor());
		} else {
			glfwSetCursor(glfwWindowHandle, 0L);
		}
	}

	public synchronized int getWidth()
	{
		return width;
	}

	public synchronized void setWidth(int width)
	{
		this.width = width;

		dimension.setValue(width, height);
		setResized(true);
	}

	public synchronized int getHeight()
	{
		return height;
	}

	public synchronized void setHeight(int height)
	{
		this.height = height;

		dimension.setValue(width, height);
		setResized(true);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public long getGlfwWindowHandle()
	{
		return glfwWindowHandle;
	}

	public void setGlfwWindowHandle(long glfwWindowHandle)
	{
		this.glfwWindowHandle = glfwWindowHandle;
	}

	public boolean isResized()
	{
		return resized;
	}

	public void setResized(boolean resized)
	{
		this.resized = resized;
	}

	public boolean isFullScreen()
	{
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen)
	{
		this.fullScreen = fullScreen;
	}

	public boolean isDecorated()
	{
		return decorated;
	}

	public void setDecorated(boolean decorated)
	{
		this.decorated = decorated;
	}

	public boolean isvSync()
	{
		return vSync;
	}

	public void setvSync(boolean vSync)
	{
		this.vSync = vSync;
	}

	public int getvSyncRate()
	{
		return vSyncRate;
	}

	public void setvSyncRate(int vSyncRate)
	{
		this.vSyncRate = vSyncRate;
	}

	public Path getIcon()
	{
		return icon;
	}

	public void setIcon(Path icon)
	{
		this.icon = icon;
	}

	public Cursor getDefaultCursor()
	{
		return defaultCursor;
	}

	public void setDefaultCursor(Cursor defaultCursor)
	{
		this.defaultCursor = defaultCursor;
	}

	public Cursor getCursor()
	{
		return cursor;
	}

	public Vector2Data getDimension()
	{
		return dimension;
	}

	public void setDimension(Vector2Data dimension)
	{
		this.dimension = dimension;
	}

	public synchronized Vector2f getSize()
	{
		return new Vector2f(width, height);
	}

	public synchronized void setSize(int width, int height)
	{
		assert width >= 0;
		assert height >= 0;

		this.width = width;
		this.height = height;
		this.dimension.setValue(width, height);
		setResized(true);
	}

	public int getLeft()
	{
		return left;
	}

	public void setLeft(int left)
	{
		this.left = left;
	}

	public int getTop()
	{
		return top;
	}

	public void setTop(int top)
	{
		this.top = top;
	}
}
