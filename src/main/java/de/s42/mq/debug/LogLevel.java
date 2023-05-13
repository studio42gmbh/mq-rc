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
package de.s42.mq.debug;

/**
 *
 * @author Benjamin Schiller
 */
public enum LogLevel
{
	FATAL(de.s42.log.LogLevel.FATAL),
	ERROR(de.s42.log.LogLevel.ERROR),
	WARN(de.s42.log.LogLevel.WARN),
	INFO(de.s42.log.LogLevel.INFO),
	DEBUG(de.s42.log.LogLevel.DEBUG),
	TRACE(de.s42.log.LogLevel.TRACE);

	public final de.s42.log.LogLevel level;

	private LogLevel(de.s42.log.LogLevel level)
	{
		this.level = level;
	}
}
