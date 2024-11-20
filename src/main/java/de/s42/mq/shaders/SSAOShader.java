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
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.buffers.GBuffer;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.materials.Texture;
import de.s42.mq.util.HaltonSequenceGenerator;

/**
 *
 * @author Benjamin Schiller
 */

/* useful defaults
intensity = 1.0;
bias = 0.0;
scale = 1.0;
radius = 1.0;
randomSize = 12.843;
iterations = 4;
clampThreshold = -0.003;
 */
public class SSAOShader extends Shader
{

	private final static Logger log = LogManager.getLogger(SSAOShader.class.getName());

	protected Texture noiseTexture;
	protected GBuffer inBuffer;
	protected int inBufferResolutionUniform = -1;

	protected int timeUniform = -1;
	protected int intensityUniform = -1;
	protected int biasUniform = -1;
	protected int scaleUniform = -1;
	protected int radiusUniform = -1;
	protected int randomSizeUniform = -1;
	protected int iterationsUniform = -1;
	protected int pointsUniform = -1;
	protected int cameraPositionUniform = -1;
	protected int cameraNearUniform = -1;
	protected int cameraFarUniform = -1;
	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;

	protected FloatData time = new FloatData();

	@AttributeDL(required = true)
	protected FloatData intensity = new FloatData();

	@AttributeDL(required = true)
	protected FloatData bias = new FloatData();

	@AttributeDL(required = true)
	protected FloatData scale = new FloatData();

	@AttributeDL(required = true)
	protected FloatData radius = new FloatData();

	@AttributeDL(required = true)
	protected FloatData randomSize = new FloatData();

	@AttributeDL(required = true)
	protected IntegerData iterations = new IntegerData();

	protected float[] points;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("inPosition", 0);
		setUniform("inNormals", 1);
		setUniform("inNoise", 2);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		inBufferResolutionUniform = getUniformLocationOpt("inBufferResolution");
		intensityUniform = getUniformLocationOpt("intensity");
		biasUniform = getUniformLocationOpt("bias");
		scaleUniform = getUniformLocationOpt("scale");
		radiusUniform = getUniformLocationOpt("radius");
		randomSizeUniform = getUniformLocationOpt("randomSize");
		iterationsUniform = getUniformLocationOpt("iterations");
		timeUniform = getUniformLocationOpt("time");
		cameraPositionUniform = getUniformLocationOpt("cameraPosition");
		cameraNearUniform = getUniformLocationOpt("cameraNear");
		cameraFarUniform = getUniformLocationOpt("cameraFar");

		pointsUniform = getUniformLocationOpt("points[0]");

		HaltonSequenceGenerator gen = new HaltonSequenceGenerator(2);

		points = new float[100];
		for (int i = 0; i < 20; ++i) {

			double[] vec = gen.nextVector();

			log.trace("Halton " + vec[0] + " " + vec[1]);

			points[i * 2] = ((float) vec[0] * 2.0f - 1.0f);
			points[i * 2 + 1] = ((float) vec[1] * 2.0f - 1.0f);
		}
	}

	@Override
	public void beforeRendering()
	{
		assert inBuffer != null;
		assert noiseTexture != null;
		assert camera != null;

		super.beforeRendering();

		setTexture(inBuffer.getPositionRenderBuffer(), 0);
		setTexture(inBuffer.getNormalRenderBuffer(), 1);
		setTexture(noiseTexture.getTextureId(), 2);

		setUniform(inBufferResolutionUniform, 1.0f / (float) inBuffer.getWidth(), 1.0f / (float) inBuffer.getWidth());
		setUniform(intensityUniform, intensity);
		setUniform(biasUniform, bias);
		setUniform(scaleUniform, scale);
		setUniform(radiusUniform, radius);
		setUniform(randomSizeUniform, randomSize);
		setUniform(iterationsUniform, iterations);
		setUniform(timeUniform, time);
		setUniform2(pointsUniform, points);
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
		unsetTexture(0);
		unsetTexture(1);
		unsetTexture(2);

		super.afterRendering();
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

	public int getIntensityUniform()
	{
		return intensityUniform;
	}

	public int getBiasUniform()
	{
		return biasUniform;
	}

	public int getScaleUniform()
	{
		return scaleUniform;
	}

	public int getRadiusUniform()
	{
		return radiusUniform;
	}

	public int getRandomSizeUniform()
	{
		return randomSizeUniform;
	}

	public int getIterationsUniform()
	{
		return iterationsUniform;
	}

	public int getPointsUniform()
	{
		return pointsUniform;
	}

	public FloatData getIntensity()
	{
		return intensity;
	}

	public void setIntensity(FloatData intensity)
	{
		this.intensity = intensity;
	}

	public FloatData getBias()
	{
		return bias;
	}

	public void setBias(FloatData bias)
	{
		this.bias = bias;
	}

	public FloatData getScale()
	{
		return scale;
	}

	public void setScale(FloatData scale)
	{
		this.scale = scale;
	}

	public FloatData getRadius()
	{
		return radius;
	}

	public void setRadius(FloatData radius)
	{
		this.radius = radius;
	}

	public FloatData getRandomSize()
	{
		return randomSize;
	}

	public void setRandomSize(FloatData randomSize)
	{
		this.randomSize = randomSize;
	}

	public IntegerData getIterations()
	{
		return iterations;
	}

	public void setIterations(IntegerData iterations)
	{
		this.iterations = iterations;
	}

	public float[] getPoints()
	{
		return points;
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
