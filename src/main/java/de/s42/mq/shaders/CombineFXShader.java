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
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.rendering.RenderContext;
import static org.lwjgl.opengl.GL33.glBindSampler;

/**
 *
 * @author Benjamin Schiller
 */
public class CombineFXShader extends BasicFXShader
{

	private final static Logger log = LogManager.getLogger(CombineFXShader.class.getName());

	@AttributeDL(required = false)
	protected FXBuffer inBuffer2;

	protected int inBuffer2ResolutionUniform = -1;

	@Override
	protected void loadShader()
	{
		super.loadShader();

		setUniform("inBuffer2", 1);
		inBuffer2ResolutionUniform = getUniformLocationOpt("inBuffer2Resolution");
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		super.beforeRendering(context);

		if (inBuffer2 != null) {

			setUniform(inBuffer2ResolutionUniform, 1.0f / (float) inBuffer2.getScaledWidth(), 1.0f / (float) inBuffer2.getScaledHeight());
			setTexture(inBuffer2, 1);

			if (samplerActive) {
				glBindSampler(1, samplerId);
			}
		}
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		if (inBuffer2 != null) {
			unsetTexture(1);

			if (samplerActive) {
				glBindSampler(1, 0);
			}
		}

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public FXBuffer getInBuffer2()
	{
		return inBuffer2;
	}

	public void setInBuffer2(FXBuffer inBuffer2)
	{
		this.inBuffer2 = inBuffer2;
	}

	public int getInBuffer2ResolutionUniform()
	{
		return inBuffer2ResolutionUniform;
	}
	// </editor-fold>
}
