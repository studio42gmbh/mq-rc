/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.dl.annotations;

import de.s42.dl.annotations.AbstractDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class EditableDLAnnotation extends AbstractDLAnnotation
{
	/*
	private final static Logger log = LogManager.getLogger(EditableDLAnnotation.class.getName());

	private static class EditableDLInstanceValidator implements DLInstanceValidator
	{

		private final DLAttribute attribute;

		EditableDLInstanceValidator(DLAttribute attribute)
		{
			assert attribute != null;

			this.attribute = attribute;
		}

		@Override
		public void validate(DLInstance instance) throws InvalidInstance
		{
			assert instance != null;

			try {
				DLInstance val = instance.getInstance(attribute.getName());

				if (val != null) {
					//log.info("Editable for " + instance.getName() + "." + attribute.getName());
					val.set(DEFAULT_SYMBOL, true);
				}
			} catch (ClassCastException ex) {
				throw new InvalidInstance("Instance " + instance.getName() + " attribute " + attribute.getName() + " has to be an instance - " + ex.getMessage(), ex);
			}
		}
	}

	public final static String DEFAULT_SYMBOL = "editable";

	public EditableDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public EditableDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		if (parameters != null && parameters.length != 0) {
			throw new InvalidAnnotation("has to have no parameters");
		}

		((DefaultDLType) type).addInstanceValidator(new EditableDLInstanceValidator(attribute));
	}
	 */
}
