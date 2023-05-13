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
package de.s42.mq.loaders.fbx;

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.shaders.Shader;
import java.nio.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.lwjgl.assimp.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.system.MemoryUtil;

/**
 * See for ref: https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
 * and https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
 * @author Benjamin Schiller
 */
public class FbxSubMesh extends Mesh
{
	private final static Logger log = LogManager.getLogger(FbxSubMesh.class.getName());

	protected int vao = -1;
	protected int vertexArrayBuffer = -1;
	protected int normalArrayBuffer = -1;
	protected int uvArrayBuffer = -1;
	protected int elementArrayBuffer = -1;
	protected int elementCount;

	protected AIMesh aiMesh;

	public FbxSubMesh()
	{
	}

	@Override
	public FbxSubMesh copy()
	{
		FbxSubMesh copy = (FbxSubMesh) super.copy();

		copy.vao = vao;
		copy.vertexArrayBuffer = vertexArrayBuffer;
		copy.normalArrayBuffer = normalArrayBuffer;
		copy.uvArrayBuffer = uvArrayBuffer;
		copy.elementArrayBuffer = elementArrayBuffer;
		copy.elementCount = elementCount;

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		assert aiMesh != null;
		
		super.load();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vertexArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexArrayBuffer);
		AIVector3D.Buffer vertices = aiMesh.mVertices();
		nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * vertices.remaining(),
			vertices.address(), GL_STATIC_DRAW);

		normalArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer);
		AIVector3D.Buffer normals = aiMesh.mNormals();
		if (normals != null) {
			nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * normals.remaining(),
				normals.address(), GL_STATIC_DRAW);
		}

		uvArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvArrayBuffer);
		AIVector3D.Buffer uvs = aiMesh.mTextureCoords(0);
		if (uvs != null) {
			nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * uvs.remaining(),
				uvs.address(), GL_STATIC_DRAW);
		}

		int faceCount = aiMesh.mNumFaces();
		elementCount = faceCount * 3;
		IntBuffer elementArrayBufferData = MemoryUtil.memAllocInt(elementCount);
		AIFace.Buffer facesBuffer = aiMesh.mFaces();
		for (int i = 0; i < faceCount; ++i) {
			AIFace face = facesBuffer.get(i);
			if (face.mNumIndices() != 3) {
				throw new InvalidInstance("AIFace.mNumIndices() != 3");
			}
			elementArrayBufferData.put(face.mIndices());
		}
		elementArrayBufferData.flip();
		elementArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArrayBufferData,
			GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		glBindVertexArray(0);
		
		aiMesh = null;

		log.debug("Loaded mesh " + faceCount);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		glDeleteVertexArrays(vao);
		vao = -1;
		glDeleteBuffers(vertexArrayBuffer);
		glDeleteBuffers(normalArrayBuffer);
		glDeleteBuffers(elementArrayBuffer);
		elementCount = 0;

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

		if (shader.getInputPosition() > -1) {
			glBindBuffer(GL_ARRAY_BUFFER, vertexArrayBuffer);
			glEnableVertexAttribArray(shader.getInputPosition());
			glVertexAttribPointer(shader.getInputPosition(), 3, GL_FLOAT, false, 0, 0);
		}

		if (shader.getInputNormal() > -1) {
			glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer);
			glEnableVertexAttribArray(shader.getInputNormal());
			glVertexAttribPointer(shader.getInputNormal(), 3, GL_FLOAT, false, 0, 0);
		}

		if (shader.getInputTextureCoords() > -1) {
			glBindBuffer(GL_ARRAY_BUFFER, uvArrayBuffer);
			glEnableVertexAttribArray(shader.getInputTextureCoords());
			glVertexAttribPointer(shader.getInputTextureCoords(), 2, GL_FLOAT, false, 3 * 4, 0);
		}

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer);

		glDrawElements(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0);

		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		//glBindBuffer(GL_ARRAY_BUFFER, 0);
		//glBindVertexArray(0);
		shader.afterRendering();
		material.afterRendering();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public AIMesh getAiMesh()
	{
		return aiMesh;
	}

	public void setAiMesh(AIMesh aiMesh)
	{
		this.aiMesh = aiMesh;
	}
	// </editor-fold>	
}
