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
package de.s42.mq.tasks;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.Data;

/**
 *
 * @author Benjamin Schiller
 */
public class SetData extends AbstractTask
{

	@AttributeDL(required = true)
	protected Data data;
	
	@AttributeDL(required = true)
	protected Data value;

	@Override
	protected void runTask()
	{
		assert data != null;
		assert value != null;

		try {
			data.setValue(value.getValue());
		} catch (Exception ex) {
			throw new RuntimeException("Error setting data for " + data.getName() + " from " + value.getName() + " - " + ex.getMessage(), ex);
		}
	}

	public Data getData()
	{
		return data;
	}

	public void setData(Data data)
	{
		this.data = data;
	}

	public Data getValue()
	{
		return value;
	}

	public void setValue(Data value)
	{
		this.value = value;
	}
}
