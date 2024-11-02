/*
 * Copyright Studio 42 GmbH 2024. All rights reserved.
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
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.buffers.GBuffer;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.materials.Texture;

/**
 *
 * @author Benjamin Schiller
 */
public class FogShader extends Shader
{

	private final static Logger log = LogManager.getLogger(FogShader.class.getName());

	protected int inBufferResolutionUniform = -1;
	protected int timeUniform = -1;
	protected int iterationsUniform = -1;
	protected int cameraPositionUniform = -1;
	protected int cameraNearUniform = -1;
	protected int cameraFarUniform = -1;
	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;

	protected FloatData time = new FloatData();

	@AttributeDL(required = true)
	protected Texture noiseTexture;

	@AttributeDL(required = true)
	protected GBuffer inBuffer;

	@AttributeDL(required = true)
	protected IntegerData iterations = new IntegerData();

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("inPosition", 0);
		setUniform("inNormals", 1);
		setUniform("inColor", 2);
		setUniform("inDepth", 3);
		setUniform("inNoise", 4);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		inBufferResolutionUniform = getUniformLocationOpt("inBufferResolution");
		iterationsUniform = getUniformLocationOpt("iterations");
		timeUniform = getUniformLocationOpt("time");
		cameraPositionUniform = getUniformLocationOpt("cameraPosition");
		cameraNearUniform = getUniformLocationOpt("cameraNear");
		cameraFarUniform = getUniformLocationOpt("cameraFar");
	}

	@Override
	public void beforeRendering()
	{
		assert inBuffer != null;
		assert noiseTexture != null;
		assert camera != null;

		super.beforeRendering();

		setUniform(inBufferResolutionUniform, 1.0f / (float) inBuffer.getWidth(), 1.0f / (float) inBuffer.getWidth());
		setTexture(inBuffer.getPositionRenderBuffer(), 0);
		setTexture(inBuffer.getNormalRenderBuffer(), 1);
		setTexture(inBuffer.getColorRenderBuffer(), 2);
		setTexture(inBuffer.getDepthRenderBuffer(), 3);
		setTexture(noiseTexture.getTextureId(), 4);

		setUniform(iterationsUniform, iterations);
		setUniform(timeUniform, time);
		setUniform(cameraNearUniform, getCamera().getNear());
		setUniform(cameraFarUniform, getCamera().getFar());
		setUniform(cameraPositionUniform, getCamera().getPosition());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());

		setDraw1ColorAttachment();
	}

	@Override
	public void afterRendering()
	{
		setTexture(0, 0);
		setTexture(0, 1);
		setTexture(0, 2);
		setTexture(0, 3);
		setTexture(0, 4);
		super.afterRendering();

		setDraw0ColorAttachment();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public GBuffer getInBuffer()
	{
		return inBuffer;
	}

	public void setInBuffer(GBuffer inBuffer)
	{
		this.inBuffer = inBuffer;
	}

	public Texture getNoiseTexture()
	{
		return noiseTexture;
	}

	public void setNoiseTexture(Texture noiseTexture)
	{
		this.noiseTexture = noiseTexture;
	}

	public int getInBufferResolutionUniform()
	{
		return inBufferResolutionUniform;
	}

	public int getIterationsUniform()
	{
		return iterationsUniform;
	}

	public IntegerData getIterations()
	{
		return iterations;
	}

	public void setIterations(IntegerData iterations)
	{
		this.iterations = iterations;
	}

	public int getTimeUniform()
	{
		return timeUniform;
	}

	public FloatData getTime()
	{
		return time;
	}

	public void setTime(FloatData time)
	{
		this.time = time;
	}

	public int getCameraPositionUniform()
	{
		return cameraPositionUniform;
	}

	public int getCameraNearUniform()
	{
		return cameraNearUniform;
	}

	public int getCameraFarUniform()
	{
		return cameraFarUniform;
	}

	public void setCameraFarUniform(int cameraFarUniform)
	{
		this.cameraFarUniform = cameraFarUniform;
	}

	public int getViewMatrixUniform()
	{
		return viewMatrixUniform;
	}

	public int getProjectionMatrixUniform()
	{
		return projectionMatrixUniform;
	}
	// </editor-fold>
}
