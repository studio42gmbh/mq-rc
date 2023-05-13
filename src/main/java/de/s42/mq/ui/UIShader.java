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
package de.s42.mq.ui;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.shaders.*;

/**
 *
 * @author Benjamin Schiller
 */
public class UIShader extends Shader
{

	@AttributeDL(required = false)
	protected Vector2Data dimensionUI = new Vector2Data();

	protected int viewMatrixUniform = -1;
	protected int projectionMatrixUniform = -1;
	protected int modelMatrixUniform = -1;
	protected int identifierUniform = -1;
	protected int dimensionUIUniform = -1;

	@Override
	protected void loadShader() throws DLException
	{
		super.loadShader();

		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
		modelMatrixUniform = getUniformLocationOpt("modelMatrix");
		identifierUniform = getUniformLocationOpt("identifier");
		dimensionUIUniform = getUniformLocationOpt("dimensionUI");
		inputPosition = getAttributeLocationOpt("position");
		inputNormal = getAttributeLocationOpt("normal");
		inputTextureCoords = getAttributeLocationOpt("texCoords");
	}

	@Override
	public void beforeRendering()
	{
		assert camera != null;
		assert mesh != null;

		super.beforeRendering();

		setUniform(identifierUniform, mesh.getIdentifier());
		setUniform(modelMatrixUniform, mesh.getModelMatrix());
		setUniform(viewMatrixUniform, camera.getViewMatrix());
		setUniform(projectionMatrixUniform, camera.getProjectionMatrix());
		setUniform(identifierUniform, mesh.getIdentifier());

		if (mesh instanceof Panel) {

			Panel panel = (Panel) mesh;

			setUniform(dimensionUIUniform, panel.getDimensionUI());
		} else {
			setUniform(dimensionUIUniform, dimensionUI);
		}

		if (mesh.getIdentifier() > 0) {
			setDraw7ColorAttachments();
		} else {
			setDraw1ColorAttachment();
		}
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

	public int getIdentifierUniform()
	{
		return identifierUniform;
	}

	public int getDimensionUIUniform()
	{
		return dimensionUIUniform;
	}

	public Vector2Data getDimensionUI()
	{
		return dimensionUI;
	}

	public void setDimensionUI(Vector2Data dimensionUI)
	{
		this.dimensionUI = dimensionUI;
	}
	// </editor-fold>	
}
