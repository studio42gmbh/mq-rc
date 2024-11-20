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

import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.data.*;
import de.s42.mq.materials.CubeTexture;
import de.s42.mq.materials.Texture;
import de.s42.mq.util.HaltonSequenceGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public class PBRShader extends Shader
{

	private final static Logger log = LogManager.getLogger(PBRShader.class.getName());

	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int modelMatrixUniform = -1;
	protected int identifierUniform = -1;
	protected int tintUniform = -1;
	protected int normalScaleUniform = -1;
	protected int roughnessScaleUniform = -1;
	protected int roughnessOffsetUniform = -1;
	protected int metalnessScaleUniform = -1;
	protected int metalnessOffsetUniform = -1;
	protected int emissiveScaleUniform = -1;
	protected int jitterUniform = -1;

	protected Texture baseTexture;
	protected Texture heromeaoTexture;
	protected Texture normalTexture;
	protected Texture emtrTexture;
	protected CubeTexture environmentTexture;
	protected CubeTexture irradianceTexture;
	protected Texture brdfLUTTexture;
	protected ColorData tint = new ColorData(new MQColor(1.0f));
	protected Vector2Data normalScale = new Vector2Data(new Vector2f(1.0f));
	protected FloatData roughnessScale = new FloatData(1.0f);
	protected FloatData roughnessOffset = new FloatData(0.0f);
	protected FloatData metalnessScale = new FloatData(1.0f);
	protected FloatData metalnessOffset = new FloatData(0.0f);
	protected Vector3Data emissiveScale = new Vector3Data(new Vector3f(1.0f));
	protected Vector4Data jitter = new Vector4Data(new Vector4f(0.0f));

	protected float[] points;
	protected int pointsOffset;

	@Override
	protected void loadShader()
	{
		setUniform("baseSampler", 0);
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
		jitterUniform = getUniformLocationOpt("jitter");
		inputPosition = getAttributeLocationOpt("position");
		inputNormal = getAttributeLocationOpt("normal");
		inputTextureCoords = getAttributeLocationOpt("texCoords");

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
		assert camera != null;
		assert mesh != null;

		super.beforeRendering();

		setTextureOpt(getBaseTexture(), 0);
		setTextureOpt(getHeromeaoTexture(), 1);
		setTextureOpt(getNormalTexture(), 2);
		setTextureOpt(getEmtrTexture(), 3);
		setCubeTexture(getEnvironmentTexture(), 4);
		setCubeTexture(getIrradianceTexture(), 5);
		setTexture(getBrdfLUTTexture(), 6);

		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());
		setUniform(tintUniform, tint);
		setUniform(normalScaleUniform, normalScale);
		setUniform(roughnessScaleUniform, roughnessScale);
		setUniform(roughnessOffsetUniform, roughnessOffset);
		setUniform(metalnessScaleUniform, metalnessScale);
		setUniform(metalnessOffsetUniform, metalnessOffset);
		setUniform(emissiveScaleUniform, emissiveScale);

		/* @todo implement TAA support
		jitter.getValue().x = 3.0f * points[pointsOffset * 2] / 1280.0f;
		jitter.getValue().y = 3.0f * points[pointsOffset * 2 + 1] / 768.0f;
		pointsOffset = (pointsOffset+1) % (points.length / 2);
		setUniform(jitterUniform, jitter);
		 */
		if (mesh.getIdentifier() > 0) {
			setDraw7ColorAttachments();
		} else {
			setDraw6ColorAttachments();
		}
	}

	@Override
	public void afterRendering()
	{
		unsetTexture(0);
		unsetTexture(1);
		unsetTexture(2);
		unsetTexture(3);
		unsetCubeTexture(4);
		unsetCubeTexture(5);
		unsetTexture(6);

		super.afterRendering();
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

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
	}
	// </editor-fold>

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
}
