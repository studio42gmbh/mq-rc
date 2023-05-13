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
package de.s42.mq.fonts;

import de.s42.dl.exceptions.DLException;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.shaders.*;
import de.s42.mq.materials.Texture;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class FontShader extends Shader
{

	private final static Logger log = LogManager.getLogger(FontShader.class.getName());

	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int modelMatrixUniform = -1;
	protected int identifierUniform = -1;

	protected int tintUniform = -1;
	protected int tintMultiplierUniform = -1;
	protected int edgeSizeUniform = -1;
	protected int edgeUniform = -1;

	protected int tint2Uniform = -1;
	protected int tint2MultiplierUniform = -1;
	protected int edge2SizeUniform = -1;
	protected int edge2Uniform = -1;

	protected FloatData edge = new FloatData();
	protected FloatData edgeSize = new FloatData();
	protected ColorData tint = new ColorData();
	protected FloatData tintMultiplier = new FloatData();

	protected FloatData edge2 = new FloatData();
	protected FloatData edge2Size = new FloatData();
	protected ColorData tint2 = new ColorData();
	protected FloatData tint2Multiplier = new FloatData();

	protected Texture baseTexture;

	@Override
	protected void loadShader()
	{
		setUniform("baseSampler", 0);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		identifierUniform = getUniformLocationOpt("identifier");
		inputPosition = getAttributeLocationOpt("position");
		inputNormal = getAttributeLocationOpt("normal");
		inputTextureCoords = getAttributeLocationOpt("uv");
		tintUniform = getUniformLocationOpt("tint");
		tintMultiplierUniform = getUniformLocationOpt("tintMultiplier");
		edgeUniform = getUniformLocationOpt("edge");
		edgeSizeUniform = getUniformLocationOpt("edgeSize");
		tint2Uniform = getUniformLocationOpt("tint2");
		tint2MultiplierUniform = getUniformLocationOpt("tint2Multiplier");
		edge2Uniform = getUniformLocationOpt("edge2");
		edge2SizeUniform = getUniformLocationOpt("edge2Size");
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
		super.beforeRendering();

		assert camera != null;
		assert mesh != null;

		setTextureOpt(getBaseTexture(), 0);

		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());
		setUniform(tintUniform, tint);
		setUniform(tintMultiplierUniform, tintMultiplier);
		setUniform(edgeUniform, edge);
		setUniform(edgeSizeUniform, edgeSize);
		setUniform(tint2Uniform, tint2);
		setUniform(tint2MultiplierUniform, tint2Multiplier);
		setUniform(edge2Uniform, edge2);
		setUniform(edge2SizeUniform, edge2Size);

		if (mesh.getIdentifier() > 0) {
			setDraw7ColorAttachments();
		} else {
			setDraw1ColorAttachment();
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

	public int getIdentifierUniform()
	{
		return identifierUniform;
	}

	public int getTintUniform()
	{
		return tintUniform;
	}

	public int getTintMultiplierUniform()
	{
		return tintMultiplierUniform;
	}

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
	}

	public FloatData getTintMultiplier()
	{
		return tintMultiplier;
	}

	public void setTintMultiplier(FloatData tintMultiplier)
	{
		this.tintMultiplier = tintMultiplier;
	}

	public FloatData getEdgeSize()
	{
		return edgeSize;
	}

	public void setEdgeSize(FloatData edgeSize)
	{
		this.edgeSize = edgeSize;
	}

	public int getEdgeSizeUniform()
	{
		return edgeSizeUniform;
	}

	public int getEdgeUniform()
	{
		return edgeUniform;
	}

	public int getTint2Uniform()
	{
		return tint2Uniform;
	}

	public int getTint2MultiplierUniform()
	{
		return tint2MultiplierUniform;
	}

	public int getEdge2SizeUniform()
	{
		return edge2SizeUniform;
	}

	public int getEdge2Uniform()
	{
		return edge2Uniform;
	}

	public FloatData getEdge2Size()
	{
		return edge2Size;
	}

	public void setEdge2Size(FloatData edge2Size)
	{
		this.edge2Size = edge2Size;
	}

	public ColorData getTint2()
	{
		return tint2;
	}

	public void setTint2(ColorData tint2)
	{
		this.tint2 = tint2;
	}

	public FloatData getTint2Multiplier()
	{
		return tint2Multiplier;
	}

	public void setTint2Multiplier(FloatData tint2Multiplier)
	{
		this.tint2Multiplier = tint2Multiplier;
	}

	public FloatData getEdge()
	{
		return edge;
	}

	public void setEdge(FloatData edge)
	{
		this.edge = edge;
	}

	public FloatData getEdge2()
	{
		return edge2;
	}

	public void setEdge2(FloatData edge2)
	{
		this.edge2 = edge2;
	}
	// </editor-fold>	
}
