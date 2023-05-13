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

import de.s42.mq.materials.Texture;

/**
 *
 * @author Benjamin Schiller
 */
public class EquirectangularToCubemapShader extends Shader
{
	protected int viewMatrixUniform;
	protected int projectionMatrixUniform;
	protected int filterStrengthUniform;

	protected Texture equirectangularTexture;
	protected float filterStrength;

	@Override
	protected void loadShader()
	{
		setUniform("equirectangularSampler", 0);

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		filterStrengthUniform = getUniformLocationOpt("filterStrength");
		inputPosition = getAttributeLocationOpt("vertexPosition");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		assert camera != null;
		assert equirectangularTexture != null;

		setTexture(equirectangularTexture, 0);

		setUniform(viewMatrixUniform, getCamera().getViewMatrix());
		setUniform(projectionMatrixUniform, getCamera().getProjectionMatrix());
		setUniform(filterStrengthUniform, filterStrength);

		setDraw1ColorAttachment();
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

	public Texture getEquirectangularTexture()
	{
		return equirectangularTexture;
	}

	public void setEquirectangularTexture(Texture equirectangularTexture)
	{
		this.equirectangularTexture = equirectangularTexture;
	}

	public int getFilterStrengthUniform()
	{
		return filterStrengthUniform;
	}

	public float getFilterStrength()
	{
		return filterStrength;
	}

	public void setFilterStrength(float filterStrength)
	{
		this.filterStrength = filterStrength;
	}
	// </editor-fold>	
}
