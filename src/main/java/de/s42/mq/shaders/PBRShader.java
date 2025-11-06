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
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.data.Vector3Data;
import de.s42.mq.materials.CubeTexture;
import de.s42.mq.materials.Texture;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.editor;
import de.s42.mq.util.HaltonSequenceGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class PBRShader extends BasicShader
{

	private final static Logger log = LogManager.getLogger(PBRShader.class.getName());

	protected int normalScaleUniform = -1;
	protected int roughnessScaleUniform = -1;
	protected int roughnessOffsetUniform = -1;
	protected int metalnessScaleUniform = -1;
	protected int metalnessOffsetUniform = -1;
	protected int emissiveScaleUniform = -1;

	protected Texture heromeaoTexture;
	protected Texture normalTexture;
	protected Texture emtrTexture;
	protected CubeTexture environmentTexture;
	protected CubeTexture irradianceTexture;
	protected Texture brdfLUTTexture;

	@editor
	protected Vector2Data normalScale = new Vector2Data(new Vector2f(1.0f));

	@editor
	protected FloatData roughnessScale = new FloatData(1.0f);

	@editor
	protected FloatData roughnessOffset = new FloatData(0.0f);

	@editor
	protected FloatData metalnessScale = new FloatData(1.0f);

	@editor
	protected FloatData metalnessOffset = new FloatData(0.0f);

	@editor
	protected Vector3Data emissiveScale = new Vector3Data(new Vector3f(1.0f));

	protected float[] points;
	protected int pointsOffset;

	// Shadow data
	protected int shadowMatrixUniform = -1;
	protected int shadowDirectionUniform = -1;
	protected int shadowBiasUniform = -1;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("heromeaoSampler", 1);
		setUniform("normalSampler", 2);
		setUniform("emtrSampler", 3);
		setUniform("environmentSampler", 4);
		setUniform("irradianceSampler", 5);
		setUniform("brdfLUTSampler", 6);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		identifierUniform = getUniformLocationOpt("identifier");
		tintUniform = getUniformLocationOpt("tint");
		normalScaleUniform = getUniformLocationOpt("normalScale");
		roughnessScaleUniform = getUniformLocationOpt("roughnessScale");
		roughnessOffsetUniform = getUniformLocationOpt("roughnessOffset");
		metalnessScaleUniform = getUniformLocationOpt("metalnessScale");
		metalnessOffsetUniform = getUniformLocationOpt("metalnessOffset");
		emissiveScaleUniform = getUniformLocationOpt("emissiveScale");

		HaltonSequenceGenerator gen = new HaltonSequenceGenerator(2);

		points = new float[100];
		for (int i = 0; i < 20; ++i) {

			double[] vec = gen.nextVector();

			log.trace("Halton " + vec[0] + " " + vec[1]);

			points[i * 2] = ((float) vec[0] * 2.0f - 1.0f);
			points[i * 2 + 1] = ((float) vec[1] * 2.0f - 1.0f);
		}

		// Shadow
		setUniform("shadowSampler", 7);
		shadowMatrixUniform = getUniformLocationOpt("shadowMatrix");
		shadowDirectionUniform = getUniformLocationOpt("shadowDirection");
		shadowBiasUniform = getUniformLocationOpt("shadowBias");

	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		assert camera != null;
		assert mesh != null;

		super.beforeRendering(context);

		setTextureOpt(getHeromeaoTexture(), 1);
		setTextureOpt(getNormalTexture(), 2);
		setTextureOpt(getEmtrTexture(), 3);
		setCubeTexture(getEnvironmentTexture(), 4);
		setCubeTexture(getIrradianceTexture(), 5);
		setTexture(getBrdfLUTTexture(), 6);
		setTextureOpt(context.getShadowTexture(), 7);

		setUniform(normalScaleUniform, normalScale);
		setUniform(roughnessScaleUniform, roughnessScale);
		setUniform(roughnessOffsetUniform, roughnessOffset);
		setUniform(metalnessScaleUniform, metalnessScale);
		setUniform(metalnessOffsetUniform, metalnessOffset);
		setUniform(emissiveScaleUniform, emissiveScale);

		Camera shadowCamera = context.getShadowCamera();
		if (shadowCamera != null) {
			setUniform(shadowMatrixUniform, shadowCamera.getViewProjectionMatrix());
			Vector3f shadowDirection = shadowCamera.getLook().normalize().negate();
			float shadowBias = -0.01f;
			setUniform(shadowDirectionUniform, shadowDirection);
			setUniform(shadowBiasUniform, shadowBias);
		}
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		unsetTexture(0);
		unsetTexture(1);
		unsetTexture(2);
		unsetTexture(3);
		unsetCubeTexture(4);
		unsetCubeTexture(5);
		unsetTexture(6);
		unsetTexture(7);

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Texture getHeromeaoTexture()
	{
		return heromeaoTexture;
	}

	public void setHeromeaoTexture(Texture heromeaoTexture)
	{
		this.heromeaoTexture = heromeaoTexture;
	}

	public Texture getNormalTexture()
	{
		return normalTexture;
	}

	public void setNormalTexture(Texture normalTexture)
	{
		this.normalTexture = normalTexture;
	}

	public CubeTexture getEnvironmentTexture()
	{
		return environmentTexture;
	}

	public void setEnvironmentTexture(CubeTexture environmentTexture)
	{
		this.environmentTexture = environmentTexture;
	}

	public Texture getEmtrTexture()
	{
		return emtrTexture;
	}

	public void setEmtrTexture(Texture emtrTexture)
	{
		this.emtrTexture = emtrTexture;
	}

	public CubeTexture getIrradianceTexture()
	{
		return irradianceTexture;
	}

	public void setIrradianceTexture(CubeTexture irradianceTexture)
	{
		this.irradianceTexture = irradianceTexture;
	}

	public Texture getBrdfLUTTexture()
	{
		return brdfLUTTexture;
	}

	public void setBrdfLUTTexture(Texture brdfLUTTexture)
	{
		this.brdfLUTTexture = brdfLUTTexture;
	}

	public int getIdentifierUniform()
	{
		return identifierUniform;
	}

	public int getTintUniform()
	{
		return tintUniform;
	}

	public Vector2Data getNormalScale()
	{
		return normalScale;
	}

	public void setNormalScale(Vector2Data normalScale)
	{
		this.normalScale = normalScale;
	}

	public FloatData getRoughnessScale()
	{
		return roughnessScale;
	}

	public void setRoughnessScale(FloatData roughnessScale)
	{
		this.roughnessScale = roughnessScale;
	}

	public FloatData getRoughnessOffset()
	{
		return roughnessOffset;
	}

	public void setRoughnessOffset(FloatData roughnessOffset)
	{
		this.roughnessOffset = roughnessOffset;
	}

	public FloatData getMetalnessScale()
	{
		return metalnessScale;
	}

	public void setMetalnessScale(FloatData metalnessScale)
	{
		this.metalnessScale = metalnessScale;
	}

	public FloatData getMetalnessOffset()
	{
		return metalnessOffset;
	}

	public void setMetalnessOffset(FloatData metalnessOffset)
	{
		this.metalnessOffset = metalnessOffset;
	}

	public Vector3Data getEmissiveScale()
	{
		return emissiveScale;
	}

	public void setEmissiveScale(Vector3Data emissiveScale)
	{
		this.emissiveScale = emissiveScale;
	}
	// </editor-fold>
}
