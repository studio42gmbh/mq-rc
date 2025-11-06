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
import de.s42.dl.exceptions.DLException;
import de.s42.mq.MQColor;
import de.s42.mq.data.ColorData;
import de.s42.mq.materials.Texture;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.editor;

/**
 *
 * @author Benjamin Schiller
 */
public class BasicShader extends PlainShader
{

	//private final static Logger log = LogManager.getLogger(BasicShader.class.getName());
	@AttributeDL(required = false)
	protected Texture baseTexture;

	@AttributeDL(required = false)
	@editor(editorGroup = "texture")
	protected ColorData tint = new ColorData(MQColor.White);

	protected int identifierUniform = -1;
	protected int tintUniform = -1;
	protected int alphaDiscardUniform = -1;
	protected int totalTimeUniform = -1;
	protected int deltaTimeUniform = -1;
	protected int tickUniform = -1;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		if (getBaseTexture() != null) {
			getBaseTexture().load();
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		if (getBaseTexture() != null) {
			getBaseTexture().unload();
		}

		super.unload();
	}

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		// Samplers
		setUniform("baseSampler", 0);

		// Uniforms
		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		identifierUniform = getUniformLocationOpt("identifier");
		tintUniform = getUniformLocationOpt("tint");
		alphaDiscardUniform = getUniformLocationOpt("alphaDiscard");
		totalTimeUniform = getUniformLocationOpt("totalTime");
		deltaTimeUniform = getUniformLocationOpt("deltaTime");
		tickUniform = getUniformLocationOpt("tick");

		// Attributes
		inputPosition = getAttributeLocationOpt("position");
		inputNormal = getAttributeLocationOpt("normal");
		inputTextureCoords = getAttributeLocationOpt("texCoords");
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		assert camera != null;
		assert mesh != null;

		super.beforeRendering(context);

		setTextureOpt(getBaseTexture(), 0);

		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());
		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(tintUniform, tint);
		setUniform(alphaDiscardUniform, alphaDiscard);
		setUniform(totalTimeUniform, context.getTotalTime());
		setUniform(deltaTimeUniform, context.getDeltaTime());
		setUniform(tickUniform, context.getTick());

		if (mesh.getIdentifier() > 0) {
			setDraw7ColorAttachments();
		} else {
			setDraw6ColorAttachments();
		}
	}

	@Override
	public void afterRendering(RenderContext context)
	{
		unsetTexture(0);

		super.afterRendering(context);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Texture getBaseTexture()
	{
		return baseTexture;
	}

	public void setBaseTexture(Texture baseTexture)
	{
		this.baseTexture = baseTexture;
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
}
