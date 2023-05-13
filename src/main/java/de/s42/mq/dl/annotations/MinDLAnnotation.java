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
public class MinDLAnnotation extends AbstractDLAnnotation
{
	/*
	private final static Logger log = LogManager.getLogger(MinDLAnnotation.class.getName());

	private static class MinDLInstanceValidator implements DLInstanceValidator
	{

		private final DLAttribute attribute;
		private final double min;

		MinDLInstanceValidator(DLAttribute attribute, double min)
		{
			assert attribute != null;

			this.attribute = attribute;
			this.min = min;
		}

		@Override
		public void validate(DLInstance instance)
		{
			assert instance != null;

			DLInstance val = instance.getInstance(attribute.getName());

			if (val == null) {
				return;
			}

			//log.info("Min for " + instance.getName() + "." + attribute.getName() + " = " + min);
			DLAttribute attrib = val.getType().getAttribute(DEFAULT_SYMBOL).orElseThrow();
			val.set(DEFAULT_SYMBOL, ConversionHelper.convert(min, attrib.getType().getJavaDataType()));
		}
	}

	public final static String DEFAULT_SYMBOL = "min";

	public MinDLAnnotation()
	{
		this(DEFAULT_SYMBOL);
	}

	public MinDLAnnotation(String name)
	{
		super(name);
	}

	@Override
	public void bindToAttribute(DLCore core, DLType type, DLAttribute attribute, Object... parameters) throws InvalidAnnotation
	{
		assert type != null;
		assert attribute != null;
		
		parameters = validateParameters(parameters, new Class[]{Double.class});

		double min = (Double) parameters[0];

		((DefaultDLType) type).addInstanceValidator(new MinDLInstanceValidator(attribute, min));
	}
	 */
}
