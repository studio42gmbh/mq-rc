/*
 * Copyright Studio 42 GmbH 2025. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.dlt;

import de.s42.base.validation.ValidationHelper;
import de.s42.dlt.parser.Modifier;

/**
 * It true return "uniform" otherwise "const". Is intended to be used in shader templates.<br>
 * Example:<br>
 * <pre>
 * {@code
 * ${shader.develop:uniformOrConst} float val = 1.0;
 * }
 * </pre>
 *
 * This will either make it a uniform or a const in non develop mode for performance
 *
 *
 * @author Benjamin Schiller
 */
public class UniformOrConst implements Modifier<Object, String>
{

	@Override
	public String apply(Object value)
	{
		if (ValidationHelper.isBooleanTrue(value)) {
			return "uniform";
		} else {
			return "const";
		}
	}
}
