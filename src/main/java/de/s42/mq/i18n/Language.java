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
package de.s42.mq.i18n;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.core.AbstractEntity;

/**
 *
 * @author Benjamin Schiller
 */
public class Language extends AbstractEntity
{

	@AttributeDL(required = true)
	protected String locale;

	@AttributeDL(required = true)
	protected String display;

	public String getDisplay()
	{
		return display;
	}

	public void setDisplay(String display)
	{
		this.display = display;
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
	}

}
