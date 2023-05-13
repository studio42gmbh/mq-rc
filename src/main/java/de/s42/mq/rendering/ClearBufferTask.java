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
package de.s42.mq.rendering;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.buffers.*;
import de.s42.mq.tasks.*;

/**
 *
 * @author Benjamin Schiller
 */
public class ClearBufferTask extends AbstractTask
{
	@AttributeDL(required = true)
	protected FrameBuffer buffer;

	@Override
	protected void runTask()
	{
		assert buffer != null;
		
		buffer.clearBuffer();		
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}

	// "Getters/Setters" </editor-fold>	
}
