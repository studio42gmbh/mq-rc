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
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class Vector3DLType extends DefaultDLType
{

	public final static String DEFAULT_SYMBOL = "Vector3";

	public Vector3DLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public Vector3DLType(DLType type)
	{
		this(DEFAULT_SYMBOL);
		
		addParent(type);
	}
	
	public Vector3DLType(String name)
	{
		super(name);
	}

	@Override
	public Object read(Object... sources) throws InvalidValue
	{
		assert sources != null;

		Vector3f vector = new Vector3f();

		if (sources.length == 1) {

			if (sources[0] instanceof Vector3f) {
				return (Vector3f) sources[0];
			}

			if (sources[0] instanceof String) {
				return read((String) sources[0]);
			}

			float val = ((Number) sources[0]).floatValue();

			vector.set(val);
		} else if (sources.length == 2) {
			float x = ((Number) sources[0]).floatValue();
			float y = ((Number) sources[1]).floatValue();

			vector.set(x, y, 0.0f);
		} else if (sources.length == 3) {
			float x = ((Number) sources[0]).floatValue();
			float y = ((Number) sources[1]).floatValue();
			float z = ((Number) sources[2]).floatValue();

			vector.set(x, y, z);
		} else {
			throw new InvalidValue("" + getName() + " has to be either of form <float> or <float>, <float>, <float>");
		}

		return vector;
	}

	@Override
	public Class getJavaDataType()
	{
		return Vector3f.class;
	}
}
