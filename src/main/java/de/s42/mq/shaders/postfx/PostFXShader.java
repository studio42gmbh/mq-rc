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

import de.s42.mq.data.*;
import de.s42.mq.shaders.Shader;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class PostFXShader extends Shader
{
	private final static Logger log = LogManager.getLogger(PostFXShader.class.getName());

	protected int width;
	protected int height;

	protected int blitModeUniform;
	protected int exposureUniform;
	protected int vignetteStartUniform;
	protected int vignetteEndUniform;
	protected int vignetteColorUniform;
	protected int chromaticAbberationStrengthUniform;
	protected int inBufferResolutionUniform;
	protected BlitMode blitMode = BlitMode.DEFAULT;
	protected IntegerData inBufferId = new IntegerData(-1);
	protected FloatData exposure = new FloatData();

	// VIGNETTE
	protected FloatData vignetteStart = new FloatData();
	protected FloatData vignetteEnd = new FloatData();
	protected ColorData vignetteColor = new ColorData();

	// CHROMATIC ABBERATION
	protected FloatData chromaticAbberationStrength = new FloatData();

	@Override
	protected void loadShader()
	{
		blitModeUniform = getUniformLocation("blitMode");
		exposureUniform = getUniformLocation("exposure");
		vignetteStartUniform = getUniformLocation("vignetteStart");
		vignetteEndUniform = getUniformLocation("vignetteEnd");
		vignetteColorUniform = getUniformLocation("vignetteColor");
		chromaticAbberationStrengthUniform = getUniformLocation("chromaticAbberationStrength");
		inBufferResolutionUniform = getUniformLocation("inBufferResolution");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

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

		setUniform(blitModeUniform, blitMode.blitModeId);
		setUniform(exposureUniform, exposure);
		setUniform(vignetteStartUniform, vignetteStart);
		setUniform(vignetteEndUniform, vignetteEnd);
		setUniform(vignetteColorUniform, vignetteColor);
		setUniform(chromaticAbberationStrengthUniform, chromaticAbberationStrength);
		setUniform(inBufferResolutionUniform, 1.0f / (float) getWidth(), 1.0f / (float) getHeight());

		setTexture(inBufferId.getIntegerValue(), 0);

		// @todo -> causes 1281 OpenGL error in default buffer as glfw uses GL_BACK_LEFT
		// see https://www.glfw.org/docs/3.3/window_guide.html#window_attributes
		// how to not set drawbuffers from shaders?
		setDraw1ColorAttachment();
	}

	@Override
	public void afterRendering()
	{
		setTexture(0, 0);
		super.afterRendering();
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

	// </editor-fold>	
}
