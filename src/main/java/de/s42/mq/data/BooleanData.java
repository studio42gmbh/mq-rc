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
public class BooleanData extends AbstractData<Boolean>
{
	//private final static Logger log = LogManager.getLogger(BooleanData.class.getName());

	protected boolean value;
	protected BooleanData negate;

	public BooleanData()
	{
	}

	public BooleanData(boolean value)
	{
		this.value = value;
	}

	public BooleanData(boolean value, BooleanData negate)
	{
		assert negate != null;

		this.value = value;
		this.negate = negate;
	}

	public static boolean isTrue(BooleanData data)
	{
		if (data == null) {
			return false;
		}

		return data.getBooleanValue();
	}

	public static boolean isFalse(BooleanData data, boolean defaultValue)
	{
		if (data == null) {
			return true;
		}

		return !data.getBooleanValue();
	}

	public boolean getBooleanValue()
	{
		return value;
	}

	public void setBooleanValue(boolean value)
	{
		if (!isValueEqual(value)) {

			setDirty(true);

			this.value = value;

			if (getNegate() != null) {
				getNegate().setBooleanValue(!value);
			}
		}
	}

	public boolean toggle()
	{
		setBooleanValue(!getBooleanValue());

		return getBooleanValue();
	}

	@Override
	public void handleUpdate()
	{
		super.handleUpdate();

		if (negate != null) {
			negate.handleUpdate();
		}
	}

	@Override
	public boolean isValueEqual(Boolean value)
	{
		if (value == null) {
			return false;
		}

		return this.value == value;
	}

	@Override
	public Boolean getValue()
	{
		return getBooleanValue();
	}

	@Override
	public void setValue(Boolean value)
	{
		assert value != null;

		setBooleanValue(value);
	}

	@Override
	public Class<Boolean> getDataType()
	{
		return Boolean.class;
	}

	public BooleanData getNegate()
	{
		return negate;
	}

	public void setNegate(BooleanData negate)
	{
		this.negate = negate;

		if (negate != null) {
			this.negate.setBooleanValue(!value);
		}
	}
}
