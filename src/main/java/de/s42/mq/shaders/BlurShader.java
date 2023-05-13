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

import de.s42.mq.data.Vector2Data;

/**
 *
 * @author Benjamin Schiller
 */
public class BlurShader extends BasicFXShader
{
	protected int directionUniform = -1;

	protected Vector2Data direction = new Vector2Data();

	@Override
	protected void loadShader()
	{
		super.loadShader();

		directionUniform = getUniformLocationOpt("direction");
	}

	@Override
	public void beforeRendering()
	{
		assert direction != null;

		super.beforeRendering();

		setUniform(directionUniform, direction);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public int getDirectionUniform()
	{
		return directionUniform;
	}

	public Vector2Data getDirection()
	{
		return direction;
	}

	public void setDirection(Vector2Data direction)
	{
		this.direction = direction;
	}
	// </editor-fold>	
}
