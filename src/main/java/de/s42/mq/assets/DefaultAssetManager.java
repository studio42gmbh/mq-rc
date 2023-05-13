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
package de.s42.mq.assets;

import de.s42.base.files.FilesHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultAssetManager implements AssetManager
{
	@Override
	public ByteBuffer getSourceAsByteBuffer(Path source) throws IOException
	{
		return FilesHelper.getFileAsMappedByteBuffer(source);
	}

	@Override
	public String getZippedSingleFileSourceAsString(Path source) throws IOException
	{
		return FilesHelper.getZippedSingleFileAsString(source);
	}

	@Override
	public String getSingleFileSourceAsString(Path source) throws IOException
	{
		return FilesHelper.getFileAsString(source);
	}
}
