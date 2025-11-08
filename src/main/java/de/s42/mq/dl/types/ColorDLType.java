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
package de.s42.mq.dl.types;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.DefaultDLType;
import de.s42.mq.MQColor;

/**
 *
 * @author Benjamin Schiller
 */
public class ColorDLType extends DefaultDLType
{

	//private final static Logger log = LogManager.getLogger(ColorDLType.class.getName());
	public final static String DEFAULT_SYMBOL = "Color";

	public ColorDLType()
	{
		this(DEFAULT_SYMBOL);
	}

	public ColorDLType(DLType type)
	{
		this(DEFAULT_SYMBOL);

		addParent(type);
	}

	public ColorDLType(String name)
	{
		super(name);
	}

	// @todo MQ clean up parsing of color ... very messy atm
	@Override
	public Object read(Object... sources) throws InvalidValue
	{
		assert sources != null;

		//log.debug("Sources " + Arrays.toString(sources));
		MQColor color = new MQColor();

		switch (sources.length) {
			case 1 -> {
				switch (sources[0]) {
					case MQColor mQColor -> {
						return mQColor;
					}
					case String string -> {
						sources = string.split("\\s*,\\s*");

						if (sources.length == 1) {

							if (string.startsWith("#")) {
								// parse html color code
								color.set(string);

								return color;
							}
						}

						sources = ConversionHelper.convertArray(sources, new Class[]{Number.class});

						float r = ((Number) sources[0]).floatValue();
						float g = ((Number) sources[1]).floatValue();
						float b = ((Number) sources[2]).floatValue();

						color.set(r, g, b);

						return color;
					}
					default -> {
					}
				}
				float c = ((Number) sources[0]).floatValue();
				color.set(c, c, c);
			}
			case 3 -> {
				float r = ((Number) sources[0]).floatValue();
				float g = ((Number) sources[1]).floatValue();
				float b = ((Number) sources[2]).floatValue();
				color.set(r, g, b);
			}
			case 4 -> {
				float r = ((Number) sources[0]).floatValue();
				float g = ((Number) sources[1]).floatValue();
				float b = ((Number) sources[2]).floatValue();
				float a = ((Number) sources[3]).floatValue();
				color.set(r, g, b, a);
			}
			default ->
				throw new InvalidValue("" + getName() + " has to be either of form <float> or <float>, <float>, <float> or <float>, <float>, <float>, <float>");
		}

		return color;
	}

	@Override
	public Class getJavaDataType()
	{
		return MQColor.class;
	}
}
