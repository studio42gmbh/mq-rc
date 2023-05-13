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
package de.s42.mq.loaders.obj;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.shaders.Shader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.nio.file.Path;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Benjamin Schiller
 */
public class ObjMesh extends Mesh
{

	private final static Logger log = LogManager.getLogger(ObjMesh.class.getName());

	@AttributeDL(required = true)
	@isFile
	private Path source;

	protected int vao = -1;
	protected int vbo = -1;
	protected int ibo = -1;
	protected int triangleCount;
	protected int elementCount;

	public ObjMesh()
	{

	}

	public ObjMesh(Path source)
	{
		assert source != null;

		this.source = source;
	}

	@Override
	public ObjMesh copy()
	{
		ObjMesh copy = (ObjMesh) super.copy();

		copy.vao = vao;
		copy.vbo = vbo;
		copy.ibo = ibo;
		copy.source = source;
		copy.triangleCount = triangleCount;
		copy.elementCount = elementCount;

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		assert source != null;

		if (isLoaded()) {
			return;
		}

		super.load();

		ObjLoader loader = ObjLoader.loadFromSingleFileZipSource(getAssetManager(), source);

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		log.info("Generating mesh");

		// Generate vertex buffer		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		FloatBuffer fb = MemoryUtil.memAllocFloat(loader.faces.size() * 3 * (3 + 3 + 2));

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		IntBuffer ib = MemoryUtil.memAllocInt(loader.faces.size() * 6);

		for (int i = 0; i < loader.faces.size(); i++) {

			//faces.add(new int[]{p1, t1, n1, p2, t2, n2, p3, t3, n3});
			int face[] = loader.faces.get(i);

			// vertex 1
			Vector3f pos = loader.positions.get(face[0]);
			fb.put(pos.x).put(pos.y).put(pos.z);

			Vector3f normal = loader.normals.get(face[2]);
			fb.put(normal.x).put(normal.y).put(normal.z);

			Vector3f textureCoords = loader.texturePositions.get(face[1]);
			fb.put(textureCoords.x).put(textureCoords.y);

			// vertex 2
			pos = loader.positions.get(face[3]);
			fb.put(pos.x).put(pos.y).put(pos.z);

			normal = loader.normals.get(face[2 + 3]);
			fb.put(normal.x).put(normal.y).put(normal.z);

			textureCoords = loader.texturePositions.get(face[1 + 3]);
			fb.put(textureCoords.x).put(textureCoords.y);

			// vertex 3
			pos = loader.positions.get(face[0 + 6]);
			fb.put(pos.x).put(pos.y).put(pos.z);

			normal = loader.normals.get(face[2 + 6]);
			fb.put(normal.x).put(normal.y).put(normal.z);

			textureCoords = loader.texturePositions.get(face[1 + 6]);
			fb.put(textureCoords.x).put(textureCoords.y);

			ib.put(i * 3).put(i * 3 + 1).put(i * 3 + 2);
		}

		fb.flip();
		glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
		MemoryUtil.memFree(fb);

		ib.flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);
		MemoryUtil.memFree(ib);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		triangleCount = loader.faces.size();
		elementCount = triangleCount * 3;
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
		assert material.getShader().isLoaded();
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

		glDrawElements(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0L);

		//glBindBuffer(GL_ARRAY_BUFFER, 0);
		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		//glBindVertexArray(0);
		shader.afterRendering();
		material.afterRendering();
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}
}
