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
package de.s42.mq.rc;

import de.s42.base.resources.ResourceHelper;
import java.io.IOException;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public class Version
{

	private final static Logger log = LogManager.getLogger(Version.class.getName());

	public static String getVersion()
	{
		try {
			return ResourceHelper.getResourceAsString(Version.class, "mq-rc.version").get();
		}
		catch (IOException ex) {
			log.error("Unable to load version from resources " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

}
