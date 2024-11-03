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

/**
 *
 * @author Benjamin Schiller
 */
public class StringData extends AbstractData<String>
{

	protected String value = "";

	public StringData()
	{
	}

	public StringData(String value)
	{
		this.value = value;
	}

	public StringData(String name, String value)
	{
		super(name);

		this.value = value;
	}

	public String getStringValue()
	{
		return value;
	}

	public void setStringValue(String value)
	{
		if ((this.value == null) || !this.value.equals(value)) {
			dirty = true;
		}
		this.value = value;
	}

	public void setValueToString(Object value)
	{
		if (value == null) {
			value = "";
		}

		setValue("" + value);
	}

	@Override
	public String getValue()
	{
		return getStringValue();
	}

	@Override
	public void setValue(String value)
	{
		assert value != null;

		setStringValue(value);
	}

	@Override
	public Class<String> getDataType()
	{
		return String.class;
	}
}
