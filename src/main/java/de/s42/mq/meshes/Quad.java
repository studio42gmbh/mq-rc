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
package de.s42.mq.meshes;

import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.shaders.Shader;
import java.util.Arrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Quad extends Mesh
{

	private final static Logger log = LogManager.getLogger(Quad.class.getName());

	protected final static float QUAD_VERTICES[] = {
		// positions | normal | uvs
		-0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // l, b
		0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // r, b
		0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, // r, t
		0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, // r, t
		-0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // l, t
		-0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // l, b
	};

	protected int vao = -1;
	protected int vbo = -1;
	protected float quadVertices[];
	protected boolean dynamicVertices;
	protected boolean dirtyVertices;

	public Quad()
	{
	}

	@Override
	public Quad copy()
	{
		Quad copy = (Quad) super.copy();

		// @todo finalize proper copying
		copy.vao = vao;
		copy.vbo = vbo;
		copy.quadVertices = quadVertices;
		copy.dynamicVertices = dynamicVertices;
		copy.dirtyVertices = dirtyVertices;

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		log.debug("Generating quad");

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		if (dynamicVertices) {
			quadVertices = Arrays.copyOf(QUAD_VERTICES, QUAD_VERTICES.length);

			// Generate vertex buffer
			vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_DYNAMIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		} else {
			// Generate vertex buffer
			vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, QUAD_VERTICES, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}

		glBindVertexArray(0);
	}

	public void setUvs(float left, float top, float right, float bottom) throws IllegalStateException
	{
		if (!isDynamicVertices()) {
			throw new IllegalStateException("Can not set uvs on static Quad");
		}

		// l, b
		quadVertices[0 * (3 + 3 + 2) + 6] = left;
		quadVertices[0 * (3 + 3 + 2) + 7] = bottom;
		// r, b
		quadVertices[1 * (3 + 3 + 2) + 6] = right;
		quadVertices[1 * (3 + 3 + 2) + 7] = bottom;
		// r, t
		quadVertices[2 * (3 + 3 + 2) + 6] = right;
		quadVertices[2 * (3 + 3 + 2) + 7] = top;
		// r, t
		quadVertices[3 * (3 + 3 + 2) + 6] = right;
		quadVertices[3 * (3 + 3 + 2) + 7] = top;
		// l, t
		quadVertices[4 * (3 + 3 + 2) + 6] = left;
		quadVertices[4 * (3 + 3 + 2) + 7] = top;
		// l, b
		quadVertices[5 * (3 + 3 + 2) + 6] = left;
		quadVertices[5 * (3 + 3 + 2) + 7] = bottom;

		setDirtyVertices(true);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		super.unload();

		glDeleteBuffers(vbo);
		vbo = -1;
		glDeleteVertexArrays(vao);
		vao = -1;
	}

	@Override
	public void render()
	{
		assert material != null;
		assert material.isLoaded();
		assert isLoaded();

		updateModelMatrix();

		Shader shader = material.getShader();
		material.beforeRendering();
		shader.setMesh(this);
		shader.beforeRendering();

		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		if (isDirtyVertices()) {
			glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_DYNAMIC_DRAW);
		}

		if (shader.getInputPosition() > -1) {
			glVertexAttribPointer(shader.getInputPosition(), 3, GL_FLOAT, false, (3 + 3 + 2) * 4, 0L);
			glEnableVertexAttribArray(shader.getInputPosition());
		}
		if (shader.getInputNormal() > -1) {
			glVertexAttribPointer(shader.getInputNormal(), 3, GL_FLOAT, false, (3 + 3 + 2) * 4, 3 * 4L);
			glEnableVertexAttribArray(shader.getInputNormal());
		}
		if (shader.getInputTextureCoords() > -1) {
			glVertexAttribPointer(shader.getInputTextureCoords(), 2, GL_FLOAT, false, (3 + 3 + 2) * 4, (3 + 3) * 4L);
			glEnableVertexAttribArray(shader.getInputTextureCoords());
		}

		glDrawArrays(GL_TRIANGLES, 0, 6);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		shader.afterRendering();
	}

	public boolean isDynamicVertices()
	{
		return dynamicVertices;
	}

	public void setDynamicVertices(boolean dynamicVertices)
	{
		this.dynamicVertices = dynamicVertices;
	}

	public boolean isDirtyVertices()
	{
		return dirtyVertices;
	}

	public void setDirtyVertices(boolean dirtyVertices)
	{
		this.dirtyVertices = dirtyVertices;
	}
}
