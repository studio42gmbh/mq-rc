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
import de.s42.mq.data.StringData;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class LocalizedStringData extends StringData
{

	@AttributeDL(required = true)
	protected L10N l10n;

	@AttributeDL(required = true)
	protected String id;

	protected int l10nRevision = -1;

	public LocalizedStringData()
	{
	}

	public LocalizedStringData(L10N l10n, String id)
	{
		assert l10n != null;
		assert id != null;

		this.l10n = l10n;
		this.id = id;
	}

	@Override
	@SuppressWarnings("null")
	public String getStringValue()
	{
		assert l10n != null;
		assert id != null;

		// if l10n didnt change use cached value
		if (l10nRevision == l10n.getRevision()) {
			return value;
		}

		Optional<Object> valOpt = l10n.get(id);

		Object val;

		// default replacement
		if (valOpt.isEmpty()) {
			val = "<" + l10n.getLocale() + ":" + id + ">";
		} else {
			val = valOpt.get();
		}

		// unpack string data statically
		if (val instanceof StringData stringData) {
			val = stringData.getStringValue();
		}

		if (val == null) {
			val = "<" + l10n.getLocale() + ":empty:" + id + ">";
		}

		if (!(val instanceof String)) {
			throw new RuntimeException("l10n data has to be of type string but is " + val.getClass());
		}

		value = (String) val;
		l10nRevision = l10n.getRevision();

		return value;
	}

	@Override
	public void setStringValue(String value)
	{
		throw new RuntimeException("can not set data on LocalizedStringData");
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public L10N getL10n()
	{
		return l10n;
	}

	public void setL10n(L10N l10n)
	{
		this.l10n = l10n;
	}
}
