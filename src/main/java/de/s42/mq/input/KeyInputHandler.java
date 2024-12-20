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
package de.s42.mq.input;

/**
 *
 * @author Benjamin Schiller
 */
public interface KeyInputHandler
{

	public void handleChar(String chars);

	public void handleKey(int key, int scancode, int action, int mods);

	default void handleKey(Key key)
	{
		assert key != null : "key != null";

		handleKey(key.getKey(), key.getScancode(), key.getAction(), key.getMods());
	}
}
