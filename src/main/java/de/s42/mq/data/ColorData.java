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

import de.s42.mq.MQColor;

/**
 *
 * @author Benjamin Schiller
 */
public class ColorData extends AbstractData<MQColor>
{
	protected MQColor value = new MQColor();

	public ColorData()
	{
	}

	public ColorData(MQColor value)
	{
		this.value = value;
	}

	@Override
	public MQColor getValue()
	{
		return value;
	}

	@Override
	public void setValue(MQColor value)
	{
		assert value != null;

		if (this.value != value) {
			dirty = true;
		}
		this.value = value;
	}

	@Override
	public Class<MQColor> getDataType()
	{
		return MQColor.class;
	}
}
