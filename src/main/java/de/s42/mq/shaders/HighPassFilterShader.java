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

import de.s42.mq.data.FloatData;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class HighPassFilterShader extends BasicFXShader
{

	private final static Logger log = LogManager.getLogger(HighPassFilterShader.class.getName());

	protected int thresholdLowUniform = -1;
	protected int thresholdHighUniform = -1;
	protected int intensityUniform = -1;

	protected FloatData thresholdLow = new FloatData();
	protected FloatData thresholdHigh = new FloatData();
	protected FloatData intensity = new FloatData();

	@Override
	protected void loadShader()
	{
		super.loadShader();

		thresholdLowUniform = getUniformLocationOpt("thresholdLow");
		thresholdHighUniform = getUniformLocationOpt("thresholdHigh");
		intensityUniform = getUniformLocationOpt("intensity");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		assert thresholdLow != null;
		assert thresholdHigh != null;
		assert intensity != null;

		setUniform(thresholdLowUniform, thresholdLow);
		setUniform(thresholdHighUniform, thresholdHigh);
		setUniform(intensityUniform, intensity);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getThresholdLowUniform()
	{
		return thresholdLowUniform;
	}

	public int getThresholdHighUniform()
	{
		return thresholdHighUniform;
	}

	public FloatData getThresholdLow()
	{
		return thresholdLow;
	}

	public void setThresholdLow(FloatData thresholdLow)
	{
		this.thresholdLow = thresholdLow;
	}

	public FloatData getThresholdHigh()
	{
		return thresholdHigh;
	}

	public void setThresholdHigh(FloatData thresholdHigh)
	{
		this.thresholdHigh = thresholdHigh;
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
	// </editor-fold>	
}
