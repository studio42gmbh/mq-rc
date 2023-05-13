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

import de.s42.dl.annotations.files.IsFileDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class MQIsFileDLAnnotation extends IsFileDLAnnotation
{
/*
	protected static class MQIsFileDLInstanceValidator extends IsFileDLInstanceValidator
	{

		protected MQIsFileDLInstanceValidator(String attributeName)
		{
			super(attributeName);
		}

		@Override
		protected void validateValue(Object val) throws InvalidAnnotation
		{
			if (val == null) {
				return;
			}

			if (val instanceof DLInstance) {

				Class javaDataType = ((DLInstance) val).getType().getJavaDataType();

				if (!PathData.class.isAssignableFrom(javaDataType)) {
					throw new InvalidAnnotation("Data type of instance is wrong " + javaDataType);
				}

				return;
			}

			// added PathData support to @isFile annotation
			if (val instanceof PathData) {

				PathData data = (PathData) val;

				if (!(data.getValue() instanceof Path)) {
					throw new InvalidAnnotation("Values of PathData in " + DEFAULT_SYMBOL + " annotation are required to be of class Path an may not be null");
				}

				if (!Files.isRegularFile(data.getValue())) {
					throw new InvalidAnnotation("Path " + data.getValue().toAbsolutePath() + " is not a valid file");
				}

				return;
			}

			super.validateValue(val);
		}
	}

	public MQIsFileDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public MQIsFileDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;

		if (parameters != null && parameters.length != 0) {
			throw new InvalidAnnotation(DEFAULT_SYMBOL + " has to have no parameters");
		}

		MQIsFileDLInstanceValidator validator = new MQIsFileDLInstanceValidator(attribute.getName());

		if (attribute instanceof DefaultDLAttribute) {
			((DefaultDLAttribute) attribute).addValidator(validator);
		}

		((DefaultDLType) type).addInstanceValidator(validator);
	}
*/
}
