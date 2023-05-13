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

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.buffers.GBuffer;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class SSRRShader extends Shader
{

	private final static Logger log = LogManager.getLogger(SSRRShader.class.getName());

	@AttributeDL(required = true)
	protected GBuffer inBuffer;

	@AttributeDL(required = true)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "0.1")
	protected FloatData intensity = new FloatData();

	@AttributeDL(required = true)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1")
	protected IntegerData iterations = new IntegerData();

	protected int inBufferResolutionUniform = -1;
	protected int iterationsUniform = -1;
	protected int intensityUniform = -1;
	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int cameraPositionUniform = -1;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("inColor", 0);
		setUniform("inPosition", 1);
		setUniform("inNormals", 2);
		setUniform("inSpecial", 3);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		inBufferResolutionUniform = getUniformLocationOpt("inBufferResolution");
		intensityUniform = getUniformLocationOpt("intensity");
		iterationsUniform = getUniformLocationOpt("iterations");
		cameraPositionUniform = getUniformLocationOpt("cameraPosition");
	}

	@Override
	public void beforeRendering()
	{
		assert inBuffer != null;
		assert camera != null;

		super.beforeRendering();

		setUniform(inBufferResolutionUniform, 1.0f / (float) inBuffer.getWidth(), 1.0f / (float) inBuffer.getWidth());
		setTexture(inBuffer.getColorRenderBuffer(), 0);
		setTexture(inBuffer.getPositionRenderBuffer(), 1);
		setTexture(inBuffer.getNormalRenderBuffer(), 2);
		setTexture(inBuffer.getSpecialRenderBuffer(), 3);

		setUniform(intensityUniform, intensity);
		setUniform(iterationsUniform, iterations);

		setUniform(cameraPositionUniform, getCamera().getPosition());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());

		setDraw1ColorAttachment();
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

	public int getInBufferResolutionUniform()
	{
		return inBufferResolutionUniform;
	}

	public int getIntensityUniform()
	{
		return intensityUniform;
	}

	public FloatData getIntensity()
	{
		return intensity;
	}

	public void setIntensity(FloatData intensity)
	{
		this.intensity = intensity;
	}

	public IntegerData getIterations()
	{
		return iterations;
	}

	public void setIterations(IntegerData iterations)
	{
		this.iterations = iterations;
	}
	// </editor-fold>	
}
