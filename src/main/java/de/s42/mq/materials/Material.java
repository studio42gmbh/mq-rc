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
package de.s42.mq.materials;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.cameras.Camera;
import de.s42.mq.shaders.Shader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class Material extends AbstractAsset
{

	@AttributeDL(required = false)
	protected Camera camera;

	@AttributeDL(required = false)
	protected Shader shader;

	protected boolean shaderLoaded;

	protected final Map<String, Object> customProperties = new HashMap<>();

	protected Material()
	{
	}

	protected Material(Shader shader)
	{
		assert shader != null;

		this.shader = shader;
	}

	public void beforeRendering()
	{
		if (shader != null && camera != null) {
			shader.setCamera(camera);
		}
	}

	public void afterRendering()
	{
		// prepare stuff
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		if (shader != null && !shader.isLoaded()) {
			shader.load();
			shaderLoaded = true;
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		if (shader != null && shaderLoaded) {
			shader.unload();
			shaderLoaded = false;
		}

		super.unload();
	}

	public Map<String, Object> getCustomProperties()
	{
		return customProperties;
	}

	public <PropertyType extends Object> PropertyType getCustomProperty(String name)
	{
		return (PropertyType) customProperties.get(name);
	}

	public void setCustomProperty(String name, Object value)
	{
		customProperties.put(name, value);
	}

	public boolean containsCustomProperty(String name)
	{
		return customProperties.containsKey(name);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	public Shader getShader()
	{
		return shader;
	}

	public void setShader(Shader shader)
	{
		this.shader = shader;
	}
	// </editor-fold>
}
