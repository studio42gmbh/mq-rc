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
 * @param <DataValueType>
 */
public interface Data<DataValueType extends Object>
{

	public String getName();

	public Class<DataValueType> getDataType();

	public DataValueType getValue();

	public void setValue(DataValueType value);

	public boolean isEditable();

	/**
	 * This method shall be called by the thread that shall dispatch the change
	 */
	public void handleUpdate();
}
