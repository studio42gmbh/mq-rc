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
import de.s42.dl.language.DLVersion;
import java.io.IOException;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class DLHelper
{

	private final static Logger log = LogManager.getLogger(DLHelper.class.getName());

	public static MQDLCore loadCore() throws ClassNotFoundException, IOException, DLException
	{
		log.info("Loading core {}", DLVersion.getVersion());

		MQDLCore core = new MQDLCore();

		core.init();

		return core;
	}

}
