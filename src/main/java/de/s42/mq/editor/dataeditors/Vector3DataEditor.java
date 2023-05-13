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
import de.s42.mq.data.Vector3Data;
import de.s42.mq.editor.AbstractDataEditor;

/**
 *
 * @author Benjamin Schiller
 */
public class Vector3DataEditor extends AbstractDataEditor<Vector3Data>
{
	@AttributeDL(required = true)	
	protected Vector3Data data;
	
	@AttributeDL(required = false, defaultValue = "0.0")
	protected float minValue = 0.0f;
	
	@AttributeDL(required = false, defaultValue = "1.0")
	protected float maxValue = 1.0f;
	
	@AttributeDL(required = false, defaultValue = "0.1")
	protected float step = 0.1f;

	public float getMinValue()
	{
		return minValue;
	}

	public void setMinValue(float minValue)
	{
		this.minValue = minValue;
	}

	public float getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(float maxValue)
	{
		this.maxValue = maxValue;
	}

	public float getStep()
	{
		return step;
	}

	public void setStep(float step)
	{
		this.step = step;
	}

	@Override
	public Vector3Data getData()
	{
		return data;
	}

	@Override
	public void setData(Vector3Data data)
	{
		this.data = data;
	}
}
