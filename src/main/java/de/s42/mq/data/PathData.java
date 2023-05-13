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
package de.s42.mq.data;

import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class PathData extends AbstractData<Path>
{
	protected Path value;

	public PathData()
	{
	}

	public PathData(Path value)
	{
		this.value = value;
	}

	public PathData(String name, Path value)
	{
		super(name);

		this.value = value;
	}

	public void setValueToString(Object value)
	{
		if (value == null) {
			value = "";
		}

		setValue(Path.of("" + value));
	}

	@Override
	public Path getValue()
	{
		return value;
	}

	@Override
	public void setValue(Path value)
	{
		assert value != null;

		this.value = value;
	}

	@Override
	public Class<Path> getDataType()
	{
		return Path.class;
	}
}
