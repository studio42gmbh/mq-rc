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
import de.s42.mq.loaders.fbx.MQDebug;
import de.s42.mq.materials.Material;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.shaders.Shader;
import de.s42.mq.util.AABB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Cube extends Mesh
{

	private final static Logger log = LogManager.getLogger(Cube.class.getName());

	public static Cube fromAABB(AABB aabb)
	{
		Cube cube = new Cube();

		cube.setPosition(aabb.getCenter());
		cube.setScale(aabb.getDelta().mul(0.5f));

		return cube;
	}

	public final float CUBE_VERTICES[] = {
		// back face
		-1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // bottom-left
		1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f, // top-right
		1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, // bottom-right
		1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f, // top-right
		-1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // bottom-left
		-1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, // top-left
		// front face
		-1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom-left
		1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, // bottom-right
		1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // top-right
		1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // top-right
		-1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // top-left
		-1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom-left
		// left face
		-1.0f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top-right
		-1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top-left
		-1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // bottom-left
		-1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // bottom-left
		-1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // bottom-right
		-1.0f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top-right
		// right face
		1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top-left
		1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // bottom-right
		1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top-right
		1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // bottom-right
		1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top-left
		1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // bottom-left
		// bottom face
		-1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, // top-right
		1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, // top-left
		1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, // bottom-left
		1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, // bottom-left
		-1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, // bottom-right
		-1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, // top-right
		// top face
		-1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // top-left
		1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom-right
		1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, // top-right
		1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom-right
		-1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // top-left
		-1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f // bottom-left
	};

	protected int vao = -1;
	protected int vbo = -1;

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		log.trace("Generating cube");

		vbo = glGenBuffers();
		// fill buffer
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, CUBE_VERTICES, GL_STATIC_DRAW);
		// link vertex attributes
		/*glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0L);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4L);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4L);*/
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		glDeleteBuffers(vbo);
		vbo = -1;
		glDeleteVertexArrays(vao);
		vao = -1;

		super.unload();
	}

	@Override
	public void render(RenderContext context)
	{
		assert context != null : "context != null";
		assert material != null : "material != null";
		assert material.isLoaded() : "material.isLoaded()";
		assert isLoaded() : "isLoaded()";

		// Use override material if given
		Material mat = (context.getOverrideMaterial() != null) ? context.getOverrideMaterial() : material;
		Shader shader = mat.getShader();

		updateModelMatrix();

		mat.beforeRendering(context);
		shader.setMesh(this);
		shader.beforeRendering(context);

		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);

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

		glDrawArrays(GL_TRIANGLES, 0, 36);
		MQDebug.incDrawCallData(1, 1, 12);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		shader.afterRendering(context);
		mat.afterRendering(context);
	}
}
