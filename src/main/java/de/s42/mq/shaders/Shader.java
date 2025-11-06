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
package de.s42.mq.shaders;

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.exceptions.DLException;
import de.s42.dlt.DLT;
import de.s42.dlt.parser.CompiledTemplate;
import de.s42.dlt.parser.DefaultTemplateContext;
import de.s42.dlt.parser.TemplateCompilerOptions;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.*;
import de.s42.mq.dlt.UniformOrConst;
import de.s42.mq.materials.CubeTexture;
import de.s42.mq.materials.Texture;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.editor;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_SRC1_ALPHA;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glGetActiveUniformName;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class Shader extends AbstractAsset
{

	private final static Logger log = LogManager.getLogger(Shader.class.getName());

	public enum CullType
	{
		BACK(GL_BACK),
		FRONT(GL_FRONT),
		FRONT_AND_BACK(GL_FRONT_AND_BACK),
		NONE(GL_NONE);

		public final int glFormat;

		private CullType(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	// https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glDepthFunc.xhtml
	public enum DepthFunc
	{
		NEVER(GL_NEVER),
		LESS(GL_LESS),
		EQUAL(GL_EQUAL),
		LEQUAL(GL_LEQUAL),
		GREATER(GL_GREATER),
		NOTEQUAL(GL_NOTEQUAL),
		GEQUAL(GL_GEQUAL),
		ALWAYS(GL_ALWAYS);

		public final int glFormat;

		private DepthFunc(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	public enum BlendFunc
	{
		ZERO(GL_ZERO),
		ONE(GL_ONE),
		SRC_COLOR(GL_SRC_COLOR),
		ONE_MINUS_SRC_COLOR(GL_ONE_MINUS_SRC_COLOR),
		DST_COLOR(GL_DST_COLOR),
		ONE_MINUS_DST_COLOR(GL_ONE_MINUS_DST_COLOR),
		SRC_ALPHA(GL_SRC_ALPHA),
		ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA(GL_DST_ALPHA),
		ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
		CONSTANT_COLOR(GL_CONSTANT_COLOR),
		ONE_MINUS_CONSTANT_COLOR(GL_ONE_MINUS_CONSTANT_COLOR),
		CONSTANT_ALPHA(GL_CONSTANT_ALPHA),
		ONE_MINUS_CONSTANT_ALPHA(GL_ONE_MINUS_CONSTANT_ALPHA),
		SRC_ALPHA_SATURATE(GL_SRC_ALPHA_SATURATE),
		SRC1_COLOR(GL_SRC1_COLOR),
		ONE_MINUS_SRC1_COLOR(GL_ONE_MINUS_SRC1_COLOR),
		SRC1_ALPHA(GL_SRC1_ALPHA),
		ONE_MINUS_SRC1_ALPHA(GL_ONE_MINUS_SRC1_ALPHA);

		public final int glFormat;

		private BlendFunc(int glFormat)
		{
			this.glFormat = glFormat;
		}
	}

	@AttributeDL(defaultValue = "BACK")
	@editor(editorGroup = "culling")
	protected CullType cullType = CullType.BACK;

	@AttributeDL(defaultValue = "ONE")
	@editor(editorGroup = "blending")
	protected BlendFunc sourceFunc = BlendFunc.ONE;

	@AttributeDL(defaultValue = "ONE")
	@editor(editorGroup = "blending")
	protected BlendFunc destFunc = BlendFunc.ONE;

	@AttributeDL(defaultValue = "LESS")
	@editor(editorGroup = "depth")
	protected DepthFunc depthFunc = DepthFunc.LESS;

	@AttributeDL(defaultValue = "true")
	@editor(editorGroup = "depth")
	protected boolean depthTest = true;

	@AttributeDL(defaultValue = "true")
	@editor(editorGroup = "depth")
	protected boolean depthWrite = true;

	@AttributeDL(defaultValue = "true")
	@editor(editorGroup = "culling")
	protected boolean cullFace = true;

	@AttributeDL(defaultValue = "false")
	@editor(editorGroup = "culling")
	protected boolean stencilTest = false;

	@AttributeDL(defaultValue = "false")
	@editor
	protected boolean wireframe = false;

	@AttributeDL(defaultValue = "false")
	@editor(editorGroup = "blending")
	protected boolean renderTransparent = false;

	@AttributeDL(defaultValue = "0.0000001")
	@editor(editorGroup = "blending")
	protected float alphaDiscard = 0.0000001f;

	@AttributeDL(defaultValue = "false")
	@editor(editable = false, editorGroup = "develop")
	protected boolean develop = false;

	protected Camera camera;

	@AttributeDL(ignore = true)
	protected Mesh mesh;

	@required
	@isFile
	protected Path vertexShaderSource;

	@required
	@isFile
	protected Path fragmentShaderSource;

	@dontPersist
	protected final Map<String, Integer> uniforms = new HashMap<>();

	@dontPersist
	protected final Map<String, Integer> attributes = new HashMap<>();

	@dontPersist
	protected int shaderProgramId = -1;

	@dontPersist
	protected int inputPosition = -1;

	@dontPersist
	protected int inputNormal = -1;

	@dontPersist
	protected int inputTextureCoords = -1;

	@dontPersist
	private final FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);

	@dontPersist
	private final FloatBuffer vector2Buffer = MemoryUtil.memAllocFloat(2);

	@dontPersist
	private final FloatBuffer vector3Buffer = MemoryUtil.memAllocFloat(3);

	@dontPersist
	private final FloatBuffer vector4Buffer = MemoryUtil.memAllocFloat(4);

	public Shader()
	{
	}

	public Shader(Path vertexShaderSource, Path fragmentShaderSource)
	{
		this.vertexShaderSource = vertexShaderSource;
		this.fragmentShaderSource = fragmentShaderSource;
	}

	protected void loadShader() throws DLException
	{
		// Implement
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		log.debug("Loading", getClass().getName());

		int program = glCreateProgram();

		log.debug("Loading vertex shader", FilesHelper.createMavenNetbeansFileConsoleLink(getVertexShaderSource()));

		int vshader;
		try {
			vshader = createShader(getVertexShaderSource(), GL_VERTEX_SHADER);
		} catch (Exception ex) {
			throw new IllegalStateException("Error creating vertex shader '" + getVertexShaderSource() + "' - " + ex.getMessage(), ex);
		}
		glAttachShader(program, vshader);

		log.debug("Loading fragment shader", FilesHelper.createMavenNetbeansFileConsoleLink(getFragmentShaderSource()));

		int fshader;
		try {
			fshader = createShader(getFragmentShaderSource(), GL_FRAGMENT_SHADER);
		} catch (Exception ex) {
			throw new IllegalStateException("Error creating fragment shader '" + getFragmentShaderSource() + "' - " + ex.getMessage(), ex);
		}
		glAttachShader(program, fshader);

		glLinkProgram(program);
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(program);

		if (programLog.trim().length() > 0) {
			log.error(programLog);
		}

		if (linked == 0) {
			throw new IllegalStateException("Could not link program - " + programLog);
		}

		setShaderProgramId(program);

		glUseProgram(program);

		fetchUniforms();
		fetchAttributes();

		loadShader();

		glUseProgram(0);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		if (getShaderProgramId() != -1) {
			glDeleteProgram(getShaderProgramId());
			setShaderProgramId(-1);
		}

		super.unload();
	}

	public void beforeRendering(RenderContext context)
	{
		assert getCullType() != null;

		glUseProgram(getShaderProgramId());

		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

		if (isCullFace() && getCullType() != CullType.NONE) {
			glCullFace(getCullType().glFormat);
			glEnable(GL_CULL_FACE);
		} else {
			glDisable(GL_CULL_FACE);
		}

		if (isDepthTest()) {
			glEnable(GL_DEPTH_TEST);
		} else {
			glDisable(GL_DEPTH_TEST);
		}

		glDepthMask(isDepthWrite());
		glDepthFunc(getDepthFunc().glFormat);

		if (isStencilTest()) {
			glEnable(GL_STENCIL_TEST);
		} else {
			glDisable(GL_STENCIL_TEST);
		}

		if (isWireframe()) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		} else {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}

		if (isRenderTransparent()) {
			glEnablei(GL_BLEND, 0);
			// @todo later allow even more complex blending?
			//glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
			//glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
			glBlendFunc(getSourceFunc().glFormat, getDestFunc().glFormat);
		} else {
			glDisablei(GL_BLEND, 0);
		}
	}

	public void afterRendering(RenderContext context)
	{
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glUseProgram(0);

		if (isRenderTransparent()) {
			glDisablei(GL_BLEND, 0);
			glBlendFunc(BlendFunc.ONE.glFormat, BlendFunc.ZERO.glFormat);
		}
	}

	protected int createShader(Path source, int type) throws Exception
	{
		assert source != null : "source != null";

		if (!Files.isRegularFile(source)) {
			throw new Exception("Error creating shader " + source.toString() + " is not a regular file");
		}

		ByteBuffer sourceBuf = null;
		String evaluated = null;

		// Support DLT dynamic shaders
		if (source.getFileName().toString().contains(".dlt.")) {
			log.debug("Found dynamic shader source", source);

			TemplateCompilerOptions options = new TemplateCompilerOptions(source.toString());
			options.setClassName("DLTShader");
			options.setDebug(false);
			options.setCacheCompiledTemplate(true);
			CompiledTemplate compiled = DLT.compile(source, options);

			DefaultTemplateContext context = new DefaultTemplateContext(source.toAbsolutePath().toString());
			context.addModifier(new UniformOrConst());
			context.setBinding("shader", this);
			evaluated = compiled.evaluate(context);

			log.trace("Generated:\n", evaluated);

		} // Otherwise load file memory mapped
		else {
			sourceBuf = getAssetManager().getSourceAsByteBuffer(source);
		}

		int shaderId = glCreateShader(type);

		// Somehow wrapping the string into a ByteBuffer crashes the VM ... so we do the CharSequence based glShaderSource
		if (evaluated != null) {
			glShaderSource(shaderId, evaluated);
		} else if (sourceBuf != null) {

			PointerBuffer strings = BufferUtils.createPointerBuffer(1);
			IntBuffer lengths = BufferUtils.createIntBuffer(1);

			strings.put(0, sourceBuf);
			lengths.put(0, sourceBuf.remaining());

			glShaderSource(shaderId, strings, lengths);
		}

		glCompileShader(shaderId);

		int compiled = glGetShaderi(shaderId, GL_COMPILE_STATUS);

		String shaderLog = glGetShaderInfoLog(shaderId);

		if (compiled == 0) {
			if (evaluated != null) {
				log.error("Invalid shader:\n" + evaluated);
			} else if (sourceBuf != null) {
				//sourceBuf.reset();
				//log.error("Invalid shader:\n" + (new String(sourceBuf.array())));
			}
			throw new Exception("Could not compile shader: " + shaderLog);
		}

		// Will issue warnings
		if (shaderLog.trim().length() > 0) {
			log.warn(shaderLog);
		}

		return shaderId;
	}

	protected void fetchAttributes()
	{
		int len = glGetProgrami(shaderProgramId, GL_ACTIVE_ATTRIBUTES);

		for (int i = 0; i < len; i++) {

			IntBuffer size = BufferUtils.createIntBuffer(1);
			IntBuffer type = BufferUtils.createIntBuffer(1);
			String attribName = glGetActiveAttrib(shaderProgramId, i, size, type);
			int location = glGetAttribLocation(shaderProgramId, attribName);
			attributes.put(attribName, location);
			log.trace("Attribute", attribName, location, size.get(0), type.get(0));
		}
	}

	public int getAttributeLocation(String name)
	{
		assert attributes.containsKey(name);

		return attributes.get(name);
	}

	public int getAttributeLocationOpt(String name)
	{
		Integer location = attributes.get(name);

		return location != null ? location : -1;
	}

	protected void fetchUniforms()
	{
		int len = glGetProgrami(shaderProgramId, GL_ACTIVE_UNIFORMS);

		for (int i = 0; i < len; i++) {
			String uniformName = glGetActiveUniformName(shaderProgramId, i);
			int location = glGetUniformLocation(shaderProgramId, uniformName);
			uniforms.put(uniformName, location);
			log.trace("Uniform", uniformName, location);
		}
	}

	public int getUniformLocation(String name)
	{
		assert uniforms.containsKey(name) : "uniforms.containsKey(name) - " + name;

		return uniforms.get(name);
	}

	public int getUniformLocationOpt(String name)
	{
		Integer location = uniforms.get(name);

		return location != null ? location : -1;
	}

	public void setUniform(String name, FloatData value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value);
	}

	public void setUniformOpt(int uniformLocation, FloatData value)
	{
		if (value == null) {
			return;
		}

		if (uniformLocation == -1) {
			return;
		}

		setUniform(uniformLocation, value.getFloatValue());
	}

	public void setUniform(int uniformLocation, FloatData value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		setUniform(uniformLocation, value.getFloatValue());
	}

	public void setUniform(String name, float value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value);
	}

	public void setUniform(int uniformLocation, float value)
	{
		if (uniformLocation == -1) {
			return;
		}

		glUniform1f(uniformLocation, value);
	}

	/**
	 * Sets an array as vec2 value
	 *
	 * @param uniformLocation
	 * @param value
	 */
	public void setUniform2(int uniformLocation, float[] value)
	{
		if (uniformLocation == -1) {
			return;
		}

		glUniform2fv(uniformLocation, value);
	}

	public void setUniform(String name, IntegerData value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value);
	}

	public void setUniform(int uniformLocation, IntegerData value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		setUniform(uniformLocation, value.getIntegerValue());
	}

	public void setUniform(String name, int value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		glUniform1i(location, value);
	}

	public void setUniform(int uniformLocation, int value)
	{
		if (uniformLocation == -1) {
			return;
		}

		glUniform1i(uniformLocation, value);
	}

	public void setUniform(String name, float value, float value2)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value, value2);
	}

	public void setUniform(int uniformLocation, float value, float value2)
	{
		if (uniformLocation == -1) {
			return;
		}

		glUniform2f(uniformLocation, value, value2);
	}

	public void setUniform(String name, Matrix4f value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value);
	}

	public void setUniform(int uniformLocation, Vector2f value)
	{
		assert value != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniform2fv(uniformLocation, value.get(vector2Buffer));
	}

	public void setUniform(int uniformLocation, Vector3f value)
	{
		assert value != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniform3fv(uniformLocation, value.get(vector3Buffer));
	}

	public void setUniform(int uniformLocation, Vector2Data value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniform2fv(uniformLocation, value.getValue().get(vector2Buffer));
	}

	public void setUniform(int uniformLocation, Vector3Data value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniform3fv(uniformLocation, value.getValue().get(vector3Buffer));
	}

	public void setUniform(int uniformLocation, Vector4Data value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniform4fv(uniformLocation, value.getValue().get(vector4Buffer));
	}

	public void setUniform(int uniformLocation, Matrix4f value)
	{
		assert value != null;

		if (uniformLocation == -1) {
			return;
		}

		glUniformMatrix4fv(uniformLocation, false, value.get(matrixBuffer));
	}

	public void setUniformOpt(int uniformLocation, Matrix4f value)
	{
		if (value == null) {
			return;
		}

		if (uniformLocation == -1) {
			return;
		}

		glUniformMatrix4fv(uniformLocation, false, value.get(matrixBuffer));
	}

	public void setUniform(String name, ColorData value)
	{
		Integer location = uniforms.get(name);
		if (location == null) {
			return;
		}

		setUniform(location, value);
	}

	public void setUniform(int uniformLocation, ColorData value)
	{
		assert value != null;
		assert value.getValue() != null;

		if (uniformLocation == -1) {
			return;
		}

		MQColor col = value.getValue();
		glUniform4f(uniformLocation, col.getR(), col.getG(), col.getB(), col.getA());
	}

	public void setCubeTextureOpt(CubeTexture texture, int index)
	{
		setCubeTexture((texture != null) ? texture.getTextureId() : 0, index);
	}

	public void setCubeTexture(CubeTexture texture, int index)
	{
		assert texture != null;

		setCubeTexture(texture.getTextureId(), index);
	}

	public void setCubeTexture(int textureId, int index)
	{
		assert index >= 0;
		assert index < 32;

		glActiveTexture(GL_TEXTURE0 + index);
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
	}

	public void unsetCubeTexture(int index)
	{
		setCubeTexture(0, index);
	}

	public void setTexture(FXBuffer buffer, int index)
	{
		assert buffer != null;

		setTexture(buffer.getBufferTextureId(), index);
	}

	public void setTexture(Texture texture, int index)
	{
		assert texture != null;

		setTexture(texture.getTextureId(), index);
	}

	public void setTextureOpt(Texture texture, int index)
	{
		setTexture((texture != null) ? texture.getTextureId() : 0, index);
	}

	public void unsetTexture(int index)
	{
		setTexture(0, index);
	}

	public void setTexture(int textureId, int index)
	{
		assert index >= 0;
		assert index < 32;

		glActiveTexture(GL_TEXTURE0 + index);
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	public void setDraw0ColorAttachment()
	{
		glDrawBuffers(new int[]{});
	}

	public void setDraw1ColorAttachment()
	{
		glDrawBuffers(new int[]{
			GL_COLOR_ATTACHMENT0
		});
	}

	public void setDraw6ColorAttachments()
	{
		glDrawBuffers(new int[]{
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2,
			GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4,
			GL_COLOR_ATTACHMENT5
		});
	}

	public void setDraw7ColorAttachments()
	{
		glDrawBuffers(new int[]{
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2,
			GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4,
			GL_COLOR_ATTACHMENT5,
			GL_COLOR_ATTACHMENT6
		});
	}

	public void setDraw8ColorAttachments()
	{
		glDrawBuffers(new int[]{
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2,
			GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4,
			GL_COLOR_ATTACHMENT5,
			GL_COLOR_ATTACHMENT6,
			GL_COLOR_ATTACHMENT7
		});
	}

	public int getShaderProgramId()
	{
		return shaderProgramId;
	}

	public void setShaderProgramId(int shaderProgramId)
	{
		this.shaderProgramId = shaderProgramId;
	}

	public Path getVertexShaderSource()
	{
		return vertexShaderSource;
	}

	public void setVertexShaderSource(Path vertexShaderSource)
	{
		this.vertexShaderSource = vertexShaderSource;
	}

	public Path getFragmentShaderSource()
	{
		return fragmentShaderSource;
	}

	public void setFragmentShaderSource(Path fragmentShaderSource)
	{
		this.fragmentShaderSource = fragmentShaderSource;
	}

	public Map<String, Integer> getUniforms()
	{
		return Collections.unmodifiableMap(uniforms);
	}

	public Map<String, Integer> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	public int getInputPosition()
	{
		return inputPosition;
	}

	public int getInputNormal()
	{
		return inputNormal;
	}

	public int getInputTextureCoords()
	{
		return inputTextureCoords;
	}

	public Mesh getMesh()
	{
		return mesh;
	}

	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}

	public CullType getCullType()
	{
		return cullType;
	}

	public void setCullType(CullType cullType)
	{
		this.cullType = cullType;
	}

	public boolean isDepthTest()
	{
		return depthTest;
	}

	public void setDepthTest(boolean depthTest)
	{
		this.depthTest = depthTest;
	}

	public boolean isCullFace()
	{
		return cullFace;
	}

	public void setCullFace(boolean cullFace)
	{
		this.cullFace = cullFace;
	}

	public boolean isStencilTest()
	{
		return stencilTest;
	}

	public void setStencilTest(boolean stencilTest)
	{
		this.stencilTest = stencilTest;
	}

	public boolean isWireframe()
	{
		return wireframe;
	}

	public void setWireframe(boolean wireframe)
	{
		this.wireframe = wireframe;
	}

	public boolean isDepthWrite()
	{
		return depthWrite;
	}

	public void setDepthWrite(boolean depthWrite)
	{
		this.depthWrite = depthWrite;
	}

	public boolean isRenderTransparent()
	{
		return renderTransparent;
	}

	public void setRenderTransparent(boolean renderTransparent)
	{
		this.renderTransparent = renderTransparent;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	public BlendFunc getSourceFunc()
	{
		return sourceFunc;
	}

	public void setSourceFunc(BlendFunc sourceFunc)
	{
		this.sourceFunc = sourceFunc;
	}

	public BlendFunc getDestFunc()
	{
		return destFunc;
	}

	public void setDestFunc(BlendFunc destFunc)
	{
		this.destFunc = destFunc;
	}

	public DepthFunc getDepthFunc()
	{
		return depthFunc;
	}

	public void setDepthFunc(DepthFunc depthFunc)
	{
		this.depthFunc = depthFunc;
	}

	public float getAlphaDiscard()
	{
		return alphaDiscard;
	}

	public void setAlphaDiscard(float alphaDiscard)
	{
		this.alphaDiscard = alphaDiscard;
	}

	public boolean isDevelop()
	{
		return develop;
	}

	public void setDevelop(boolean develop)
	{
		this.develop = develop;
	}
}
