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
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.AbstractAsset;
import static de.s42.mq.util.GLFWHelper.getImageFromImagePath;
import java.io.IOException;
import java.nio.file.Path;
import static org.lwjgl.glfw.GLFW.glfwCreateCursor;

/**
 *
 * @author Benjamin Schiller
 */
public class Cursor extends AbstractAsset
{

	@AttributeDL(required = true)
	@isFile
	protected Path image;

	@AttributeDL(required = false, defaultValue = "0")
	protected int hotX = 0;

	@AttributeDL(required = false, defaultValue = "0")
	protected int hotY = 0;

	protected long cursor = -1;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		try {
			cursor = glfwCreateCursor(getImageFromImagePath(image), hotX, hotY);
		} catch (IOException ex) {
			throw new RuntimeException("Could not load cursor " + ex.getMessage(), ex);
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		// @todo
		super.unload();
	}

	public Path getImage()
	{
		return image;
	}

	public void setImage(Path image)
	{
		this.image = image;
	}

	public int getHotX()
	{
		return hotX;
	}

	public void setHotX(int hotX)
	{
		this.hotX = hotX;
	}

	public int getHotY()
	{
		return hotY;
	}

	public void setHotY(int hotY)
	{
		this.hotY = hotY;
	}

	public long getCursor()
	{
		return cursor;
	}

	public void setCursor(long cursor)
	{
		this.cursor = cursor;
	}

}
