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

import de.s42.dl.exceptions.DLException;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.materials.Texture;
import de.s42.mq.rendering.RenderContext;
import org.joml.Vector3f;
import de.s42.mq.ui.editor;

/**
 *
 * @author Benjamin Schiller
 */
public class ParticlesShader extends Shader
{

	//private final static Logger log = LogManager.getLogger(ParticlesShader.class.getName());
	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int modelMatrixUniform = -1;
	protected int timeUniform = -1;
	protected int spriteMapSizeUniform = -1;
	protected int timeScaleUniform = -1;
	protected int noiseScaleUniform = -1;
	protected int scaleScaleUniform = -1;
	protected int baseScaleUniform = -1;
	protected int baseSpreadUniform = -1;
	protected int noiseSpeedUniform = -1;
	protected int rotationSpeedUniform = -1;
	protected int velocityUniform = -1;
	protected int maxAgeUniform = -1;
	protected int ageJitterUniform = -1;
	protected int tintUniform = -1;
	protected int alphaThresholdUniform = -1;

	protected int positionAttribute = -1;
	protected int uvAttribute = -1;
	protected int seedAttribute = -1;

	protected FloatData time = new FloatData();
	protected Vector2Data spriteMapSize = new Vector2Data();

	@editor
	protected FloatData timeScale = new FloatData();

	@editor
	protected FloatData noiseScale = new FloatData();

	@editor
	protected FloatData scaleScale = new FloatData();

	@editor
	protected FloatData baseScale = new FloatData();

	@editor
	protected FloatData baseSpread = new FloatData();

	@editor
	protected FloatData noiseSpeed = new FloatData();

	@editor
	protected FloatData rotationSpeed = new FloatData();

	@editor
	protected Vector3f velocity = new Vector3f();

	@editor
	protected FloatData maxAge = new FloatData();

	@editor
	protected FloatData ageJitter = new FloatData();

	@editor
	protected ColorData tint = new ColorData();

	@editor
	protected FloatData alphaThreshold = new FloatData();

	protected Texture baseTexture;
	protected Texture noiseTexture;

	@Override
	protected void loadShader() throws DLException
	{
		assert baseTexture != null;
		assert noiseTexture != null;

		baseTexture.load();
		noiseTexture.load();

		setUniform("baseSampler", 0);
		setUniform("noiseSampler", 1);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		spriteMapSizeUniform = getUniformLocationOpt("spriteMapSize");
		timeScaleUniform = getUniformLocationOpt("timeScale");
		noiseScaleUniform = getUniformLocationOpt("noiseScale");
		scaleScaleUniform = getUniformLocationOpt("scaleScale");
		baseScaleUniform = getUniformLocationOpt("baseScale");
		baseSpreadUniform = getUniformLocationOpt("baseSpread");
		noiseSpeedUniform = getUniformLocationOpt("noiseSpeed");
		rotationSpeedUniform = getUniformLocationOpt("rotationSpeed");
		velocityUniform = getUniformLocationOpt("velocity");
		maxAgeUniform = getUniformLocationOpt("maxAge");
		ageJitterUniform = getUniformLocationOpt("ageJitter");
		tintUniform = getUniformLocationOpt("tint");
		timeUniform = getUniformLocationOpt("time");
		alphaThresholdUniform = getUniformLocationOpt("alphaThreshold");

		positionAttribute = getAttributeLocationOpt("position");
		seedAttribute = getAttributeLocationOpt("seed");
		uvAttribute = getAttributeLocationOpt("uv");
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		baseTexture.unload();
		noiseTexture.unload();

		super.unload();
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		super.beforeRendering(context);

		assert camera != null;
		assert mesh != null;

		setTextureOpt(baseTexture, 0);
		setTextureOpt(noiseTexture, 1);

		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());
		setUniform(timeUniform, time);
		setUniform(spriteMapSizeUniform, spriteMapSize);
		setUniform(timeScaleUniform, timeScale);
		setUniform(noiseScaleUniform, noiseScale);
		setUniform(scaleScaleUniform, scaleScale);
		setUniform(baseScaleUniform, baseScale);
		setUniform(baseSpreadUniform, baseSpread);
		setUniform(noiseSpeedUniform, noiseSpeed);
		setUniform(rotationSpeedUniform, rotationSpeed);
		setUniform(velocityUniform, velocity);
		setUniform(maxAgeUniform, maxAge);
		setUniform(ageJitterUniform, ageJitter);
		setUniform(tintUniform, tint);
		setUniform(alphaThresholdUniform, alphaThreshold);

		setDraw1ColorAttachment();
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		unsetTexture(0);
		unsetTexture(1);

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getViewMatrixUniform()
	{
		return viewMatrixUniform;
	}

	public int getProjectionMatrixUniform()
	{
		return projectionMatrixUniform;
	}

	public int getModelMatrixUniform()
	{
		return modelMatrixUniform;
	}

	public Texture getBaseTexture()
	{
		return baseTexture;
	}

	public void setBaseTexture(Texture baseTexture)
	{
		this.baseTexture = baseTexture;
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

	public Texture getNoiseTexture()
	{
		return noiseTexture;
	}

	public void setNoiseTexture(Texture noiseTexture)
	{
		this.noiseTexture = noiseTexture;
	}

	public int getSeedAttribute()
	{
		return seedAttribute;
	}

	public int getPositionAttribute()
	{
		return positionAttribute;
	}

	public int getUvAttribute()
	{
		return uvAttribute;
	}

	public Vector2Data getSpriteMapSize()
	{
		return spriteMapSize;
	}

	public void setSpriteMapSize(Vector2Data spriteMapSize)
	{
		this.spriteMapSize = spriteMapSize;
	}

	public FloatData getTimeScale()
	{
		return timeScale;
	}

	public void setTimeScale(FloatData timeScale)
	{
		this.timeScale = timeScale;
	}

	public FloatData getNoiseScale()
	{
		return noiseScale;
	}

	public void setNoiseScale(FloatData noiseScale)
	{
		this.noiseScale = noiseScale;
	}

	public FloatData getScaleScale()
	{
		return scaleScale;
	}

	public void setScaleScale(FloatData scaleScale)
	{
		this.scaleScale = scaleScale;
	}

	public FloatData getBaseScale()
	{
		return baseScale;
	}

	public void setBaseScale(FloatData baseScale)
	{
		this.baseScale = baseScale;
	}

	public FloatData getNoiseSpeed()
	{
		return noiseSpeed;
	}

	public void setNoiseSpeed(FloatData noiseSpeed)
	{
		this.noiseSpeed = noiseSpeed;
	}

	public FloatData getRotationSpeed()
	{
		return rotationSpeed;
	}

	public void setRotationSpeed(FloatData rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}

	public Vector3f getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector3f velocity)
	{
		this.velocity = velocity;
	}

	public FloatData getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(FloatData maxAge)
	{
		this.maxAge = maxAge;
	}

	public FloatData getAgeJitter()
	{
		return ageJitter;
	}

	public void setAgeJitter(FloatData ageJitter)
	{
		this.ageJitter = ageJitter;
	}

	public int getSpriteMapSizeUniform()
	{
		return spriteMapSizeUniform;
	}

	public int getTimeScaleUniform()
	{
		return timeScaleUniform;
	}

	public int getNoiseScaleUniform()
	{
		return noiseScaleUniform;
	}

	public int getScaleScaleUniform()
	{
		return scaleScaleUniform;
	}

	public int getBaseScaleUniform()
	{
		return baseScaleUniform;
	}

	public int getNoiseSpeedUniform()
	{
		return noiseSpeedUniform;
	}

	public int getRotationSpeedUniform()
	{
		return rotationSpeedUniform;
	}

	public int getVelocityUniform()
	{
		return velocityUniform;
	}

	public int getMaxAgeUniform()
	{
		return maxAgeUniform;
	}

	public int getAgeJitterUniform()
	{
		return ageJitterUniform;
	}

	public int getTintUniform()
	{
		return tintUniform;
	}

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
	}

	public int getAlphaThresholdUniform()
	{
		return alphaThresholdUniform;
	}

	public FloatData getAlphaThreshold()
	{
		return alphaThreshold;
	}

	public void setAlphaThreshold(FloatData alphaThreshold)
	{
		this.alphaThreshold = alphaThreshold;
	}

	public int getBaseSpreadUniform()
	{
		return baseSpreadUniform;
	}

	public FloatData getBaseSpread()
	{
		return baseSpread;
	}

	public void setBaseSpread(FloatData baseSpread)
	{
		this.baseSpread = baseSpread;
	}
	// </editor-fold>
}
