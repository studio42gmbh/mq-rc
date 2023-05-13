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
import de.s42.mq.data.IntegerData;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class SSLFShader extends BasicFXShader
{
	private final static Logger log = LogManager.getLogger(SSLFShader.class.getName());

	protected int intensityUniform = -1;
	protected int haloIntensityUniform = -1;
	protected int haloRadiusUniform = -1;
	protected int ghostStepDistanceUniform = -1;
	protected int chromaticDistorsionScaleUniform = -1;
	protected int flareFadeUniform = -1;
	protected int haloFadeUniform = -1;
	protected int samplesUniform = -1;

	protected FloatData intensity = new FloatData();
	protected FloatData haloIntensity = new FloatData();
	protected FloatData haloRadius = new FloatData();
	protected FloatData ghostStepDistance = new FloatData();
	protected FloatData chromaticDistorsionScale = new FloatData();
	protected FloatData flareFade = new FloatData();
	protected FloatData haloFade = new FloatData();
	protected IntegerData samples = new IntegerData();

	@Override
	protected void loadShader()
	{
		super.loadShader();

		intensityUniform = getUniformLocationOpt("intensity");
		haloIntensityUniform = getUniformLocationOpt("haloIntensity");
		haloRadiusUniform = getUniformLocationOpt("haloRadius");
		ghostStepDistanceUniform = getUniformLocationOpt("ghostStepDistance");
		chromaticDistorsionScaleUniform = getUniformLocationOpt("chromaticDistorsionScale");
		flareFadeUniform = getUniformLocationOpt("flareFade");
		haloFadeUniform = getUniformLocationOpt("haloFade");
		samplesUniform = getUniformLocationOpt("samples");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		assert getIntensity() != null;
		assert getHaloIntensity() != null;
		assert getHaloRadius() != null;
		assert getGhostStepDistance() != null;
		assert getChromaticDistorsionScale() != null;
		assert getFlareFade() != null;
		assert getHaloFade() != null;
		assert getSamples() != null;

		setUniform(getIntensityUniform(), getIntensity());
		setUniform(getHaloIntensityUniform(), getHaloIntensity());
		setUniform(getHaloRadiusUniform(), getHaloRadius());
		setUniform(getGhostStepDistanceUniform(), getGhostStepDistance());
		setUniform(getChromaticDistorsionScaleUniform(), getChromaticDistorsionScale());
		setUniform(getFlareFadeUniform(), getFlareFade());
		setUniform(getHaloFadeUniform(), getHaloFade());
		setUniform(getSamplesUniform(), getSamples());
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getIntensityUniform()
	{
		return intensityUniform;
	}

	public int getHaloIntensityUniform()
	{
		return haloIntensityUniform;
	}

	public int getHaloRadiusUniform()
	{
		return haloRadiusUniform;
	}

	public int getGhostStepDistanceUniform()
	{
		return ghostStepDistanceUniform;
	}

	public int getChromaticDistorsionScaleUniform()
	{
		return chromaticDistorsionScaleUniform;
	}

	public int getFlareFadeUniform()
	{
		return flareFadeUniform;
	}

	public int getHaloFadeUniform()
	{
		return haloFadeUniform;
	}

	public int getSamplesUniform()
	{
		return samplesUniform;
	}

	public FloatData getIntensity()
	{
		return intensity;
	}

	public void setIntensity(FloatData intensity)
	{
		this.intensity = intensity;
	}

	public FloatData getHaloIntensity()
	{
		return haloIntensity;
	}

	public void setHaloIntensity(FloatData haloIntensity)
	{
		this.haloIntensity = haloIntensity;
	}

	public FloatData getHaloRadius()
	{
		return haloRadius;
	}

	public void setHaloRadius(FloatData haloRadius)
	{
		this.haloRadius = haloRadius;
	}

	public FloatData getGhostStepDistance()
	{
		return ghostStepDistance;
	}

	public void setGhostStepDistance(FloatData ghostStepDistance)
	{
		this.ghostStepDistance = ghostStepDistance;
	}

	public FloatData getChromaticDistorsionScale()
	{
		return chromaticDistorsionScale;
	}

	public void setChromaticDistorsionScale(FloatData chromaticDistorsionScale)
	{
		this.chromaticDistorsionScale = chromaticDistorsionScale;
	}

	public FloatData getFlareFade()
	{
		return flareFade;
	}

	public void setFlareFade(FloatData flareFade)
	{
		this.flareFade = flareFade;
	}

	public FloatData getHaloFade()
	{
		return haloFade;
	}

	public void setHaloFade(FloatData haloFade)
	{
		this.haloFade = haloFade;
	}

	public IntegerData getSamples()
	{
		return samples;
	}

	public void setSamples(IntegerData samples)
	{
		this.samples = samples;
	}
	// </editor-fold>	
}
