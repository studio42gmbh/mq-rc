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
package de.s42.mq.editor.dataeditors;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.StringData;
import de.s42.mq.editor.AbstractDataEditor;

/**
 *
 * @author Benjamin Schiller
 */
public class StringDataEditor extends AbstractDataEditor<StringData>
{

	@AttributeDL(required = true)
	protected StringData data;

	@Override
	public StringData getData()
	{
		return data;
	}

	@Override
	public void setData(StringData data)
	{
		this.data = data;
	}
}
