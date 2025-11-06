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
import de.s42.mq.MQColor;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.shaders.PBRShader;
import de.s42.mq.ui.editor;
import java.nio.file.Path;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class PBRMaterial extends Material
{

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "texture")
	protected Path baseSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path heromeaoSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path normalSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path environmentSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path emtrSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path irradianceSource;

	@AttributeDL(required = false)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	@editor(editable = false, editorGroup = "pbr")
	protected Path brdfLUTSource;

	@AttributeDL(required = false)
	protected CubeTexture environmentTexture;

	@AttributeDL(required = false)
	protected CubeTexture irradianceTexture;

	@AttributeDL(required = false)
	protected Texture brdfLUTTexture;

	protected Texture baseTexture;
	protected Texture heromeaoTexture;
	protected Texture normalTexture;
	protected Texture emtrTexture;

	protected boolean baseTextureLoaded;
	protected boolean heromeaoTextureLoaded;
	protected boolean normalTextureLoaded;
	protected boolean emtrTextureLoaded;
	protected boolean environmentTextureLoaded;
	protected boolean irradianceTextureLoaded;
	protected boolean brdfLUTTextureLoaded;

	@editor(editorGroup = "texture")
	protected MQColor tint = new MQColor(1.0f);

	@editor(editorGroup = "pbr")
	protected Vector2f normalScale = new Vector2f(1.0f);

	@editor(editorGroup = "pbr")
	protected float roughnessScale = 1.0f;

	@editor(editorGroup = "pbr")
	protected float roughnessOffset = 0.0f;

	@editor(editorGroup = "pbr")
	protected float metalnessScale = 1.0f;

	@editor(editorGroup = "pbr")
	protected float metalnessOffset = 0.0f;

	@editor(editorGroup = "pbr")
	protected Vector3f emissiveScale = new Vector3f(1.0f);

	// ATTENTION: This field helps DL to change the type of shader to PBRShader in type reflection
	@AttributeDL(required = false)
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	protected PBRShader shader;

	public PBRMaterial()
	{
		this(null, null, null, null, null, null, null);
	}

	public PBRMaterial(
		Path baseSource,
		Path heromeaoSource,
		Path normalSource,
		Path emtrSource,
		Path environmentSource,
		Path irradianceSource,
		Path brdfLUTSource
	)
	{
		this.baseSource = baseSource;
		this.heromeaoSource = heromeaoSource;
		this.normalSource = normalSource;
		this.emtrSource = emtrSource;
		this.environmentSource = environmentSource;
		this.irradianceSource = irradianceSource;
		this.brdfLUTSource = brdfLUTSource;
	}

	@Override
	public PBRShader getShader()
	{
		return (PBRShader) super.getShader();
	}

	public void setShader(PBRShader shader)
	{
		super.setShader(shader);
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		// load albedo texture if resource given and no texture
		if (baseSource != null && getBaseTexture() == null) {
			baseTexture = new Texture(baseSource);
			baseTexture.load();
			baseTextureLoaded = true;
		}

		// load heromeao texture if resource given and no texture
		if (heromeaoSource != null && getHeromeaoTexture() == null) {
			heromeaoTexture = new Texture(heromeaoSource);
			heromeaoTexture.load();
			heromeaoTextureLoaded = true;
		}

		// load normal texture if resource given and no texture
		if (normalSource != null && getNormalTexture() == null) {
			normalTexture = new Texture(normalSource);
			normalTexture.load();
			normalTextureLoaded = true;
		}

		// load emtr texture if resource given and no texture
		if (emtrSource != null && getEmtrTexture() == null) {
			emtrTexture = new Texture(emtrSource);
			emtrTexture.load();
			emtrTextureLoaded = true;
		}

		// load environment texture if resource given and no texture
		if (environmentSource != null && getEnvironmentTexture() == null) {
			environmentTexture = new CubeTexture();
			environmentTexture.setHDRDefaults();
			environmentTexture.load();
			environmentTextureLoaded = true;
		}

		// load irradiance texture if resource given and no texture
		if (irradianceSource != null && getIrradianceTexture() == null) {
			irradianceTexture = new CubeTexture();
			irradianceTexture.setHDRDefaults();
			irradianceTexture.load();
			irradianceTextureLoaded = true;
		}

		// load bdrf lut texture if resource given and no texture
		if (brdfLUTSource != null && getBrdfLUTTexture() == null) {
			brdfLUTTexture = new Texture(brdfLUTSource);
			brdfLUTTexture.load();
			brdfLUTTextureLoaded = true;
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		if (baseTextureLoaded) {
			baseTexture.unload();
			baseTexture = null;
			baseTextureLoaded = false;
		}

		if (heromeaoTextureLoaded) {
			heromeaoTexture.unload();
			heromeaoTexture = null;
			heromeaoTextureLoaded = false;
		}

		if (normalTextureLoaded) {
			normalTexture.unload();
			normalTexture = null;
			normalTextureLoaded = false;
		}

		if (emtrTextureLoaded) {
			emtrTexture.unload();
			emtrTexture = null;
			emtrTextureLoaded = false;
		}

		if (environmentTextureLoaded) {
			environmentTexture.unload();
			environmentTexture = null;
			environmentTextureLoaded = false;
		}

		if (irradianceTextureLoaded) {
			irradianceTexture.unload();
			irradianceTexture = null;
			irradianceTextureLoaded = false;
		}

		if (brdfLUTTextureLoaded) {
			brdfLUTTexture.unload();
			brdfLUTTexture = null;
			brdfLUTTextureLoaded = false;
		}

		super.unload();
	}

	@Override
	public void beforeRendering(RenderContext context)
	{
		super.beforeRendering(context);

		PBRShader pbrShader = getShader();

		pbrShader.setBaseTexture(getBaseTexture());
		pbrShader.setNormalTexture(getNormalTexture());
		pbrShader.setHeromeaoTexture(getHeromeaoTexture());
		pbrShader.setBrdfLUTTexture(getBrdfLUTTexture());
		pbrShader.setEmtrTexture(getEmtrTexture());
		pbrShader.setEnvironmentTexture(getEnvironmentTexture());
		pbrShader.setIrradianceTexture(getIrradianceTexture());

		pbrShader.getTint().setValue(tint);
		pbrShader.getNormalScale().setValue(normalScale);
		pbrShader.getRoughnessScale().setValue(roughnessScale);
		pbrShader.getRoughnessOffset().setValue(roughnessOffset);
		pbrShader.getMetalnessScale().setValue(metalnessScale);
		pbrShader.getMetalnessOffset().setValue(metalnessOffset);
		pbrShader.getEmissiveScale().setValue(emissiveScale);
	}

	public Path getBaseSource()
	{
		return baseSource;
	}

	public void setBaseSource(Path baseSource)
	{
		this.baseSource = baseSource;
	}

	public Path getHeromeaoSource()
	{
		return heromeaoSource;
	}

	public void setHeromeaoSource(Path heromeaoSource)
	{
		this.heromeaoSource = heromeaoSource;
	}

	public Path getNormalSource()
	{
		return normalSource;
	}

	public void setNormalSource(Path normalSource)
	{
		this.normalSource = normalSource;
	}

	public Path getEnvironmentSource()
	{
		return environmentSource;
	}

	public void setEnvironmentSource(Path environmentSource)
	{
		this.environmentSource = environmentSource;
	}

	public Path getEmtrSource()
	{
		return emtrSource;
	}

	public void setEmtrSource(Path emtrSource)
	{
		this.emtrSource = emtrSource;
	}

	public Path getIrradianceSource()
	{
		return irradianceSource;
	}

	public void setIrradianceSource(Path irradianceSource)
	{
		this.irradianceSource = irradianceSource;
	}

	public Path getBrdfLUTSource()
	{
		return brdfLUTSource;
	}

	public void setBrdfLUTSource(Path brdfLUTSource)
	{
		this.brdfLUTSource = brdfLUTSource;
	}

	public Texture getBaseTexture()
	{
		return baseTexture;
	}

	public void setBaseTexture(Texture baseTexture)
	{
		this.baseTexture = baseTexture;
	}

	public Texture getHeromeaoTexture()
	{
		return heromeaoTexture;
	}

	public void setHeromeaoTexture(Texture heromeaoTexture)
	{
		this.heromeaoTexture = heromeaoTexture;
	}

	public Texture getNormalTexture()
	{
		return normalTexture;
	}

	public void setNormalTexture(Texture normalTexture)
	{
		this.normalTexture = normalTexture;
	}

	public Texture getEmtrTexture()
	{
		return emtrTexture;
	}

	public void setEmtrTexture(Texture emtrTexture)
	{
		this.emtrTexture = emtrTexture;
	}

	public CubeTexture getEnvironmentTexture()
	{
		return environmentTexture;
	}

	public void setEnvironmentTexture(CubeTexture environmentTexture)
	{
		this.environmentTexture = environmentTexture;
	}

	public CubeTexture getIrradianceTexture()
	{
		return irradianceTexture;
	}

	public void setIrradianceTexture(CubeTexture irradianceTexture)
	{
		this.irradianceTexture = irradianceTexture;
	}

	public Texture getBrdfLUTTexture()
	{
		return brdfLUTTexture;
	}

	public void setBrdfLUTTexture(Texture brdfLUTTexture)
	{
		this.brdfLUTTexture = brdfLUTTexture;
	}

	public MQColor getTint()
	{
		return tint;
	}

	public void setTint(MQColor tint)
	{
		this.tint = tint;
	}

	public Vector2f getNormalScale()
	{
		return normalScale;
	}

	public void setNormalScale(Vector2f normalScale)
	{
		this.normalScale = normalScale;
	}

	public float getRoughnessScale()
	{
		return roughnessScale;
	}

	public void setRoughnessScale(float roughnessScale)
	{
		this.roughnessScale = roughnessScale;
	}

	public float getRoughnessOffset()
	{
		return roughnessOffset;
	}

	public void setRoughnessOffset(float roughnessOffset)
	{
		this.roughnessOffset = roughnessOffset;
	}

	public float getMetalnessScale()
	{
		return metalnessScale;
	}

	public void setMetalnessScale(float metalnessScale)
	{
		this.metalnessScale = metalnessScale;
	}

	public float getMetalnessOffset()
	{
		return metalnessOffset;
	}

	public void setMetalnessOffset(float metalnessOffset)
	{
		this.metalnessOffset = metalnessOffset;
	}

	public Vector3f getEmissiveScale()
	{
		return emissiveScale;
	}

	public void setEmissiveScale(Vector3f emissiveScale)
	{
		this.emissiveScale = emissiveScale;
	}
}
