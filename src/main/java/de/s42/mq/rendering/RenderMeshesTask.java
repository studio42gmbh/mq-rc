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
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.MeshGroup;
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

	@AttributeDL(required = false)
	protected Material overrideMaterial;

	protected FloatData totalTime = new FloatData();
	protected FloatData deltaTime = new FloatData();
	protected IntegerData tick = new IntegerData();
	protected Camera shadowCamera;
	protected FXBuffer shadowBuffer;

	@Override
	protected void runTaskFirstTime()
	{
		try {
			assert meshes != null;

			meshes.load();

			if (buffer != null) {
				buffer.load();
			}

			if (camera != null) {
				camera.load();
			}

			if (overrideMaterial != null) {
				overrideMaterial.load();
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
		if (camera != null) {

			camera.update();

			if (overrideMaterial != null) {
				overrideMaterial.setCamera(camera);
			}

			meshes.setCamera(camera);
		}

		meshes.setLayers(layers);

		DefaultRenderContext context = new DefaultRenderContext();
		context.setOverrideMaterial(overrideMaterial);
		context.setTick(tick.getIntegerValue());
		context.setDeltaTime(deltaTime.getFloatValue());
		context.setTotalTime(totalTime.getFloatValue());
		context.setShadowCamera(shadowCamera);
		if (shadowBuffer != null) {
			context.setShadowTexture(shadowBuffer.getTexture());
		}

		meshes.render(context);

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

	public Material getOverrideMaterial()
	{
		return overrideMaterial;
	}

	public void setOverrideMaterial(Material overrideMaterial)
	{
		this.overrideMaterial = overrideMaterial;
	}

	public FloatData getTotalTime()
	{
		return totalTime;
	}

	public void setTotalTime(FloatData totalTime)
	{
		this.totalTime = totalTime;
	}

	public FloatData getDeltaTime()
	{
		return deltaTime;
	}

	public void setDeltaTime(FloatData deltaTime)
	{
		this.deltaTime = deltaTime;
	}

	public IntegerData getTick()
	{
		return tick;
	}

	public void setTick(IntegerData tick)
	{
		this.tick = tick;
	}

	public Camera getShadowCamera()
	{
		return shadowCamera;
	}

	public void setShadowCamera(Camera shadowCamera)
	{
		this.shadowCamera = shadowCamera;
	}

	public FXBuffer getShadowBuffer()
	{
		return shadowBuffer;
	}

	public void setShadowBuffer(FXBuffer shadowBuffer)
	{
		this.shadowBuffer = shadowBuffer;
	}
	// "Getters/Setters" </editor-fold>
}
