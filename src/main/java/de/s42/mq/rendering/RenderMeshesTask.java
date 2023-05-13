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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.buffers.*;
import de.s42.mq.cameras.Camera;
import de.s42.mq.meshes.*;
import de.s42.mq.ui.AbstractWindowTask;

/**
 *
 * @author Benjamin Schiller
 */
public class RenderMeshesTask extends AbstractWindowTask
{

	@AttributeDL(required = true)
	protected MeshGroup meshes;

	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = "window")
	protected FrameBuffer buffer;

	@AttributeDL(required = false)
	protected Camera camera;

	@AttributeDL(required = false)
	protected String[] layers;

	@Override
	protected void runTaskFirstTime()
	{
		try {
			assert meshes != null;

			meshes.load();

			if (buffer != null) {
				buffer.load();
			}

			if (getCamera() != null) {
				camera.load();
			}
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void runTask()
	{
		assert meshes != null;

		// render main scene into gbuffer
		if (buffer != null) {
			buffer.startRender();
		} // reset to screen size
		else {
			window.setRenderViewportToWindow();
		}

		// set camera into materials of meshes
		if (getCamera() != null) {
			getCamera().update();
			meshes.setCamera(getCamera());
		}

		meshes.setLayers(layers);
		meshes.render();

		if (buffer != null) {
			buffer.endRender();
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public MeshGroup getMeshes()
	{
		return meshes;
	}

	public void setMeshes(MeshGroup meshes)
	{
		this.meshes = meshes;
	}

	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	public String[] getLayers()
	{
		return layers;
	}

	public void setLayers(String[] layers)
	{
		this.layers = layers;
	}
	// "Getters/Setters" </editor-fold>	
}
