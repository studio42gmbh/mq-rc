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

/**
 *
 * @author Benjamin Schiller
 */
public class SkyShader extends Shader
{

	//private final static Logger log = LogManager.getLogger(SkyShader.class.getName());
	private int viewMatrixUniform;
	private int projectionMatrixUniform;

	@Override
	protected void loadShader()
	{
		viewMatrixUniform = getUniformLocationOpt("viewMatrix");
		projectionMatrixUniform = getUniformLocationOpt("projectionMatrix");
	}

	@Override
	public void beforeRendering()
	{
		super.beforeRendering();

		assert camera != null;

		setUniform(viewMatrixUniform, getCamera().getViewMatrix());
		setUniform(projectionMatrixUniform, getCamera().getProjectionMatrix());

		setDraw1ColorAttachment();
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

	// </editor-fold>
}
