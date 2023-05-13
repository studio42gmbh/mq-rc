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
package de.s42.mq.input.mouse;

import de.s42.mq.input.InputHandler;
import de.s42.mq.input.MouseCursorInputHandler;

/**
 *
 * @author Benjamin Schiller
 */
public class MousePositionHandler implements MouseCursorInputHandler, InputHandler
{
	protected double x;
	protected double y;

	@Override
	public void handleMouseCursor(double xPos, double yPos)
	{
		x = xPos;
		y = yPos;
	}

	@Override
	public void update(float elapsedTime)
	{
		// do nothing
	}
	
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
}
