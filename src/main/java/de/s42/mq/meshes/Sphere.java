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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.shaders.Shader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Benjamin Schiller
 * @param <ChildType>
 */
public class Sphere<ChildType extends Object> extends Mesh<ChildType>
{

	private final static Logger log = LogManager.getLogger(Sphere.class.getName());

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float radius = 1.0f;

	@AttributeDL(required = false, defaultValue = "10")
	protected int rings;

	@AttributeDL(required = false, defaultValue = "10")
	protected int sectors;

	protected int vao = -1;
	protected int vbo = -1;
	protected int ibo = -1;

	public Sphere()
	{
	}

	public Sphere(float radius, int rings, int sectors)
	{
		assert radius > 0.0;
		assert rings > 3;
		assert sectors > 3;

		this.radius = radius;
		this.rings = rings;
		this.sectors = sectors;
	}

	@Override
	public void load() throws DLException
	{
		assert radius > 0.0;
		assert rings > 3;
		assert sectors > 3;

		if (isLoaded()) {
			return;
		}

		super.load();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		log.debug("Generating sphere", rings * sectors);

		// Generate vertex buffer		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		float PI = (float) Math.PI;
		float R = 1f / (rings - 1);
		float S = 1f / (sectors - 1);
		FloatBuffer fb = MemoryUtil.memAllocFloat(rings * sectors * (3 + 3 + 2));
		for (int r = 0; r < rings; r++) {
			for (int s = 0; s < sectors; s++) {

				// create positions
				float x = (float) (Math.cos(2 * PI * s * S) * Math.sin(PI * r * R)) * radius;
				float y = (float) Math.sin(-PI / 2 + PI * r * R) * radius;
				float z = (float) (Math.sin(2 * PI * s * S) * Math.sin(PI * r * R)) * radius;
				fb.put(x).put(y).put(z);

				// create normals
				Vector3f normal = new Vector3f(x, y, z);
				normal.normalize();
				if (isFlipNormals()) {
					normal.negate();
				}
				fb.put(normal.x).put(normal.y).put(normal.z);

				// create texture coords
				fb.put(s * S).put(1.0f - r * R);
			}
		}
		fb.flip();
		glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
		MemoryUtil.memFree(fb);

		// Generate index/element buffer 
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		IntBuffer ib = MemoryUtil.memAllocInt((rings - 1) * (sectors - 1) * 6);
		for (int r = 0; r < rings - 1; r++) {
			for (int s = 0; s < sectors - 1; s++) {
				ib.put(r * sectors + s).put((r + 1) * sectors + s).put((r + 1) * sectors + s + 1);
				ib.put((r + 1) * sectors + s + 1).put(r * sectors + s + 1).put(r * sectors + s);
			}
		}
		ib.flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);
		MemoryUtil.memFree(ib);
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
		glDeleteBuffers(ibo);
		vbo = -1;
		ibo = -1;
		glDeleteVertexArrays(vao);
		vao = -1;

		super.unload();
	}

	@Override
	public void render()
	{
		assert material != null;
		assert material.isLoaded();
		assert isLoaded();

		Shader shader = material.getShader();

		updateModelMatrix();

		material.beforeRendering();
		shader.setMesh(this);
		shader.beforeRendering();

		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

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

		glDrawElements(GL_TRIANGLES, (rings - 1) * (sectors - 1) * 6, GL_UNSIGNED_INT, 0L);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		shader.afterRendering();
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public int getRings()
	{
		return rings;
	}

	public void setRings(int rings)
	{
		this.rings = rings;
	}

	public int getSectors()
	{
		return sectors;
	}

	public void setSectors(int sectors)
	{
		this.sectors = sectors;
	}
}
