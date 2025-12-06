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
package de.s42.mq.shaders.postfx;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.materials.Texture;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.shaders.Shader;
import de.s42.mq.ui.editor;

/**
 *
 * @author Benjamin Schiller
 */
public class PostFXShader extends Shader
{
	//private final static Logger log = LogManager.getLogger(PostFXShader.class.getName());

	protected int width;
	protected int height;

	protected int blitModeUniform = -1;
	protected int exposureUniform = -1;
	protected int vignetteStartUniform = -1;
	protected int vignetteEndUniform = -1;
	protected int vignetteColorUniform = -1;
	protected int chromaticAbberationStrengthUniform = -1;
	protected int inBufferResolutionUniform = -1;
	protected int lookupIntensityUniform = -1;
	protected int barrelPowerUniform = -1;
	protected int cameraFarUniform = -1;

	@AttributeDL(defaultValue = "TONEMAP_ACES")
	@editor
	protected BlitMode blitMode = BlitMode.TONEMAP_ACES;

	@AttributeDL(required = true)
	protected IntegerData inBufferId = new IntegerData(-1);

	protected Texture lookup;

	@editor
	protected FloatData exposure = new FloatData();

	@editor
	protected FloatData vignetteStart = new FloatData();

	@editor
	protected FloatData vignetteEnd = new FloatData();

	@editor
	protected ColorData vignetteColor = new ColorData();

	@editor
	protected FloatData lookupIntensity = new FloatData();

	@editor
	protected FloatData barrelPower = new FloatData(1.0f);

	@editor
	protected FloatData chromaticAbberationStrength = new FloatData();

	protected Camera gameCamera;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		setUniform("inBuffer", 0);
		setUniform("identifierBuffer", 1);
		setUniform("inLookup", 2);

		blitModeUniform = getUniformLocation("blitMode");
		exposureUniform = getUniformLocation("exposure");
		vignetteStartUniform = getUniformLocation("vignetteStart");
		vignetteEndUniform = getUniformLocation("vignetteEnd");
		vignetteColorUniform = getUniformLocation("vignetteColor");
		chromaticAbberationStrengthUniform = getUniformLocation("chromaticAbberationStrength");
		inBufferResolutionUniform = getUniformLocation("inBufferResolution");
		lookupIntensityUniform = getUniformLocationOpt("lookupIntensity");
		barrelPowerUniform = getUniformLocationOpt("barrelPower");
		cameraFarUniform = getUniformLocationOpt("cameraFar");
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		assert blitMode != null;
		assert exposure != null;
		assert exposure.getValue() != null;
		assert vignetteStart != null;
		assert vignetteStart.getValue() != null;
		assert vignetteEnd != null;
		assert vignetteEnd.getValue() != null;
		assert vignetteColor != null;
		assert vignetteColor.getValue() != null;
		assert chromaticAbberationStrength != null;
		assert chromaticAbberationStrength.getValue() != null;
		assert inBufferId != null;
		assert inBufferId.getIntegerValue() != -1;

		super.beforeRendering(context);

		setUniform(blitModeUniform, blitMode.blitModeId);
		setUniform(exposureUniform, exposure);
		setUniform(vignetteStartUniform, vignetteStart);
		setUniform(vignetteEndUniform, vignetteEnd);
		setUniform(vignetteColorUniform, vignetteColor);
		setUniform(chromaticAbberationStrengthUniform, chromaticAbberationStrength);
		setUniform(inBufferResolutionUniform, 1.0f / (float) getWidth(), 1.0f / (float) getHeight());
		setUniform(lookupIntensityUniform, lookupIntensity);
		setUniform(barrelPowerUniform, barrelPower);

		setTexture(inBufferId.getIntegerValue(), 0);
		setTexture(inBufferId.getIntegerValue(), 1);
		if (lookup != null) {
			setTexture(lookup.getTextureId(), 2);
		}

		if (gameCamera != null) {
			setUniform(cameraFarUniform, gameCamera.getFar());
		}

		// @todo -> causes 1281 OpenGL error in default buffer as glfw uses GL_BACK_LEFT
		// see https://www.glfw.org/docs/3.3/window_guide.html#window_attributes
		// how to not set drawbuffers from shaders?
		setDraw1ColorAttachment();
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		unsetTexture(0);
		unsetTexture(1);
		if (lookup != null) {
			unsetTexture(2);
		}

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getBlitModeUniform()
	{
		return blitModeUniform;
	}

	public BlitMode getBlitMode()
	{
		return blitMode;
	}

	public void setBlitMode(BlitMode blitMode)
	{
		this.blitMode = blitMode;
	}

	public BlitMode nextBlitMode()
	{
		blitMode = blitMode.getNextBlitMode();
		return blitMode;
	}

	public BlitMode previousBlitMode()
	{
		blitMode = blitMode.getPreviousBlitMode();
		return blitMode;
	}

	public FloatData getExposure()
	{
		return exposure;
	}

	public void setExposure(FloatData exposure)
	{
		this.exposure = exposure;
	}

	public IntegerData getInBufferId()
	{
		return inBufferId;
	}

	public void setInBufferId(IntegerData inBufferId)
	{
		this.inBufferId = inBufferId;
	}

	public int getExposureUniform()
	{
		return exposureUniform;
	}

	public int getVignetteStartUniform()
	{
		return vignetteStartUniform;
	}

	public int getVignetteEndUniform()
	{
		return vignetteEndUniform;
	}

	public int getVignetteColorUniform()
	{
		return vignetteColorUniform;
	}

	public FloatData getVignetteStart()
	{
		return vignetteStart;
	}

	public void setVignetteStart(FloatData vignetteStart)
	{
		this.vignetteStart = vignetteStart;
	}

	public FloatData getVignetteEnd()
	{
		return vignetteEnd;
	}

	public void setVignetteEnd(FloatData vignetteEnd)
	{
		this.vignetteEnd = vignetteEnd;
	}

	public ColorData getVignetteColor()
	{
		return vignetteColor;
	}

	public void setVignetteColor(ColorData vignetteColor)
	{
		this.vignetteColor = vignetteColor;
	}

	public int getChromaticAbberationStrengthUniform()
	{
		return chromaticAbberationStrengthUniform;
	}

	public FloatData getChromaticAbberationStrength()
	{
		return chromaticAbberationStrength;
	}

	public void setChromaticAbberationStrength(FloatData chromaticAbberationStrength)
	{
		this.chromaticAbberationStrength = chromaticAbberationStrength;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public FloatData getLookupIntensity()
	{
		return lookupIntensity;
	}

	public void setLookupIntensity(FloatData lookupIntensity)
	{
		this.lookupIntensity = lookupIntensity;
	}

	public Texture getLookup()
	{
		return lookup;
	}

	public void setLookup(Texture lookup)
	{
		this.lookup = lookup;
	}

	public FloatData getBarrelPower()
	{
		return barrelPower;
	}

	public void setBarrelPower(FloatData barrelPower)
	{
		this.barrelPower = barrelPower;
	}

	public Camera getGameCamera()
	{
		return gameCamera;
	}

	public void setGameCamera(Camera gameCamera)
	{
		this.gameCamera = gameCamera;
	}
	// </editor-fold>
}
