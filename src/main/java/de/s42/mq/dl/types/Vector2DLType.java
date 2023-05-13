/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.dl.types;

import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.DefaultDLType;
import org.joml.Vector2f;

/**
 *
 * @author Benjamin Schiller
 */
public class Vector2DLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Vector2";

	public Vector2DLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public Vector2DLType(DLType type)
	{
		this(DEFAULT_SYMBOL);
		
		addParent(type);
	}
	
	public Vector2DLType(String name)
	{
		super(name);
	}

	@Override
	public Object read(Object... sources) throws InvalidValue
	{
		assert sources != null;

		Vector2f vector = new Vector2f();

		if (sources.length == 1) {

			if (sources[0] instanceof Vector2f) {
				return (Vector2f) sources[0];
			}

			if (sources[0] instanceof String) {
				return read((String) sources[0]);
			}

			float val = ((Number) sources[0]).floatValue();

			vector.set(val);
		} else if (sources.length == 2) {
			float x = ((Number) sources[0]).floatValue();
			float y = ((Number) sources[1]).floatValue();

			vector.set(x, y);
		} else {
			throw new InvalidValue("" + getName() + " has to be either of form <float> or <float>, <float>");
		}

		return vector;
	}

	@Override
	public Class getJavaDataType()
	{
		return Vector2f.class;
	}
}
