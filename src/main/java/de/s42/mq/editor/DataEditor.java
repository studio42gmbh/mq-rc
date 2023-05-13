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

import de.s42.mq.data.Data;

/**
 *
 * @author Benjamin Schiller
 * @param <DataType>
 */
public interface DataEditor <DataType extends Data>
{
	public String getLabel();
	
	public void setLabel(String label);
	
	public DataType getData();
	
	public void setData(DataType data);
}
