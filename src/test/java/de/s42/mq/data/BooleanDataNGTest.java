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

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class BooleanDataNGTest
{
	
	@Test
	public void validBooleanDataInitialObjectState()
	{
		BooleanData data = new BooleanData(true);
		BooleanData negateData = new BooleanData();
		
		//shall set the correct value into not
		data.setNegate(negateData);
		
		// validate data
		assertEquals(true, (boolean)data.getValue());
		assertEquals(false, data.isValueEqual(null));
		assertEquals(false, data.isValueEqual(false));
		assertEquals(true, data.isValueEqual(true));		
		assertEquals(true, data.isDirty());		
		
		// validate notData
		assertEquals(false, (boolean)negateData.getValue());
		assertEquals(false, negateData.isValueEqual(null));
		assertEquals(true, negateData.isValueEqual(false));
		assertEquals(false, negateData.isValueEqual(true));		
		assertEquals(true, negateData.isDirty());		
		
		// shall clear the dirty flag
		data.handleUpdate();
		
		// both should be updated
		assertEquals(false, data.isDirty());		
		assertEquals(false, negateData.isDirty());		
		
		// shall chain to also set notData
		data.setValue(false);
		
		// revalidate data
		assertEquals(false, (boolean)data.getValue());
		assertEquals(true, data.isValueEqual(false));
		assertEquals(false, data.isValueEqual(true));		
		assertEquals(true, data.isDirty());		
		
		// revalidate notData
		assertEquals(true, (boolean)negateData.getValue());
		assertEquals(false, negateData.isValueEqual(false));
		assertEquals(true, negateData.isValueEqual(true));		
		assertEquals(true, negateData.isDirty());		
	}	
}
