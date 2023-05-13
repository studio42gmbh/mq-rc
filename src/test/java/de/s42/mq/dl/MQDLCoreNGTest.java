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
package de.s42.mq.dl;

import de.s42.dl.exceptions.DLException;
import de.s42.dl.util.CoreHelper;
import java.io.IOException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class MQDLCoreNGTest
{

	private final static Logger log = LogManager.getLogger(MQDLCoreNGTest.class.getName());

	public MQDLCoreNGTest()
	{
	}

	@Test
	public void loadCore() throws IOException, DLException
	{
		MQDLCore core = new MQDLCore();
		core.init();
	}

	@Test
	@SuppressWarnings("UseSpecificCatch")
	public void validateReferencedClassesOfTypes() throws Exception
	{
		MQDLCore core = new MQDLCore();
		core.init();

		CoreHelper.validateCoreJavaTypes(core);
	}
}
