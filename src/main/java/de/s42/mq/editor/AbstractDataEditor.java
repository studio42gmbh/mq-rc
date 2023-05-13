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
package de.s42.mq.editor;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.Data;

/**
 *
 * @author Benjamin Schiller
 * @param <DataType>
 */
public abstract class AbstractDataEditor<DataType extends Data> implements DataEditor<DataType>
{
	@AttributeDL(required = false, defaultValue = "-")
	protected String label;

	@Override
	abstract public DataType getData();

	@Override
	abstract public void setData(DataType data);

	@Override
	public String getLabel()
	{
		return label;
	}

	@Override
	public void setLabel(String label)
	{
		this.label = label;
	}

}
