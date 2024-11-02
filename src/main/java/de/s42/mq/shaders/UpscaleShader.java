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
import de.s42.mq.data.FloatData;

/**
 *
 * @author Benjamin Schiller
 */
public class UpscaleShader extends BasicFXShader
{

	//private final static Logger log = LogManager.getLogger(UpscaleShader.class.getName());
	@AttributeDL(required = true)
	protected FloatData sampleScale = new FloatData();

	@AttributeDL(required = true)
	protected FloatData intensity = new FloatData();

	protected int sampleScaleUniform = -1;
	protected int intensityUniform = -1;

	@Override
	protected void loadShader()
	{
		super.loadShader();

		sampleScaleUniform = getUniformLocationOpt("sampleScale");
		intensityUniform = getUniformLocationOpt("intensity");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		assert sampleScale != null;

		setUniform(sampleScaleUniform, sampleScale);
		setUniform(intensityUniform, intensity);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public FloatData getSampleScale()
	{
		return sampleScale;
	}

	public void setSampleScale(FloatData sampleScale)
	{
		this.sampleScale = sampleScale;
	}

	public FloatData getIntensity()
	{
		return intensity;
	}

	public void setIntensity(FloatData intensity)
	{
		this.intensity = intensity;
	}

	public int getSampleScaleUniform()
	{
		return sampleScaleUniform;
	}

	public int getIntensityUniform()
	{
		return intensityUniform;
	}
	// </editor-fold>
}
