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
import de.s42.mq.data.ColorData;
import de.s42.mq.materials.Texture;

/**
 *
 * @author Benjamin Schiller
 */
public class BasicShader extends Shader
{

	//private final static Logger log = LogManager.getLogger(BasicShader.class.getName());
	@AttributeDL(required = false)
	protected Texture baseTexture;

	@AttributeDL(required = false)
	protected ColorData tint = new ColorData();

	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int modelMatrixUniform = -1;
	protected int identifierUniform = -1;
	protected int tintUniform = -1;

	@Override
	protected void loadShader()
	{
		setUniform("baseSampler", 0);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		identifierUniform = getUniformLocationOpt("identifier");
		tintUniform = getUniformLocationOpt("tint");
		inputPosition = getAttributeLocationOpt("position");
		inputNormal = getAttributeLocationOpt("normal");
		inputTextureCoords = getAttributeLocationOpt("texCoords");
	}

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
	public void beforeRendering()
	{
		assert camera != null;
		assert mesh != null;

		super.beforeRendering();

		setTextureOpt(getBaseTexture(), 0);

		setUniform(tintUniform, getTint());
		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());

		if (mesh.getIdentifier() > 0) {
			setDraw7ColorAttachments();
		} else {
			setDraw6ColorAttachments();
		}
	}

	@Override
	public void afterRendering()
	{
		setTexture(0, 0);

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
