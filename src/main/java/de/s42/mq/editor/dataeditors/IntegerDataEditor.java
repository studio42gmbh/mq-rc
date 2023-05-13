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
import de.s42.mq.data.IntegerData;
import de.s42.mq.editor.AbstractDataEditor;

/**
 *
 * @author Benjamin Schiller
 */
public class IntegerDataEditor extends AbstractDataEditor<IntegerData>
{

	@AttributeDL(required = true)
	protected IntegerData data;

	@AttributeDL(required = false, defaultValue = "0")
	protected int minValue = 0;

	@AttributeDL(required = false, defaultValue = "1")
	protected int maxValue = 1;

	@AttributeDL(required = false, defaultValue = "1")
	protected int step = 1;

	public int getMinValue()
	{
		return minValue;
	}

	public void setMinValue(int minValue)
	{
		this.minValue = minValue;
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
	}

	public int getStep()
	{
		return step;
	}

	public void setStep(int step)
	{
		this.step = step;
	}

	@Override
	public IntegerData getData()
	{
		return data;
	}

	@Override
	public void setData(IntegerData data)
	{
		this.data = data;
	}
}
