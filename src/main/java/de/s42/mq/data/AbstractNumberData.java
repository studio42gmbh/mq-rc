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
public abstract class AbstractNumberData<DataValueType extends Number> extends AbstractData<DataValueType>
{
	@Override
	abstract public DataValueType getValue();
	@Override
	abstract public void setValue(DataValueType value);
	
	abstract public DataValueType add(DataValueType add);
	abstract public DataValueType subtract(DataValueType sub);
	abstract public DataValueType multiply(DataValueType mul);
	abstract public DataValueType divide(DataValueType divide);
	abstract public DataValueType inc();
	abstract public DataValueType dec();
	abstract public DataValueType getMin();
	abstract public void setMin(DataValueType min);
	abstract public DataValueType getMax();
	abstract public void setMax(DataValueType max);
	abstract public DataValueType getStep();
	abstract public void setStep(DataValueType step);	
}
