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
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.shaders.Shader;
import static de.s42.mq.shaders.Shader.*;
import de.s42.mq.util.AABB;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIAABB;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import org.lwjgl.system.MemoryUtil;

/**
 * See for ref: https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
 * and https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
 *
 * @author Benjamin Schiller
 */
public class FbxSubMesh extends Mesh
{

	//private final static Logger log = LogManager.getLogger(FbxSubMesh.class.getName());
	protected int vao = -1;
	protected int vertexArrayBuffer = -1;
	protected int normalArrayBuffer = -1;
	protected int uvArrayBuffer = -1;
	protected int elementArrayBuffer = -1;
	protected int elementCount;

	protected int instanceCount = 1;
	protected int instancesPositionBuffer = -1;
	protected float instancePositions[] = new float[3];

	protected AIMesh aiMesh;

	protected final AABB aabb = new AABB();

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
		copy.instanceCount = instanceCount;
		copy.instancesPositionBuffer = instancesPositionBuffer;
		copy.instancePositions = instancePositions;
		copy.aabb.set(aabb);

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

		glEnableVertexAttribArray(LOCATION_POSITION);
		glVertexAttribPointer(LOCATION_POSITION, 3, GL_FLOAT, false, 0, 0);

		normalArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer);
		AIVector3D.Buffer normals = aiMesh.mNormals();
		if (normals != null) {
			nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * normals.remaining(),
				normals.address(), GL_STATIC_DRAW);
		}

		glEnableVertexAttribArray(LOCATION_NORMAL);
		glVertexAttribPointer(LOCATION_NORMAL, 3, GL_FLOAT, false, 0, 0);

		uvArrayBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvArrayBuffer);
		AIVector3D.Buffer uvs = aiMesh.mTextureCoords(0);
		if (uvs != null) {
			nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * uvs.remaining(),
				uvs.address(), GL_STATIC_DRAW);
		}

		glEnableVertexAttribArray(LOCATION_UV);
		glVertexAttribPointer(LOCATION_UV, 2, GL_FLOAT, false, 3 * 4, 0);

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

		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		AIAABB aiAabb = aiMesh.mAABB();

		aabb.setMin(new Vector3f(aiAabb.mMin().x(), aiAabb.mMin().y(), aiAabb.mMin().z()));
		aabb.setMax(new Vector3f(aiAabb.mMax().x(), aiAabb.mMax().y(), aiAabb.mMax().z()));

		//log.debug("AABB", aabb);
		aiMesh = null;

		log.trace("Loaded mesh " + faceCount);
	}

	public void updateInstanceCount(int instanceCount, float[] instancePositions)
	{
		assert instanceCount > 0 : "instanceCount > 0";
		assert instancePositions.length == instanceCount * 3 : "instancePositions.length == instanceCount * 3";

		this.instanceCount = instanceCount;
		this.instancePositions = instancePositions;

		glBindVertexArray(vao);
		instancesPositionBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instancesPositionBuffer);
		glBufferData(GL_ARRAY_BUFFER, instancePositions, GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(LOCATION_INSTANCE_POSITION);
		glVertexAttribPointer(LOCATION_INSTANCE_POSITION, 3, GL_FLOAT, false, 0, 0);
		glVertexAttribDivisor(LOCATION_INSTANCE_POSITION, 1);

		glBindVertexArray(0);
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
		glDeleteBuffers(instancesPositionBuffer);

		super.unload();
	}

	@Override
	public void render(RenderContext context)
	{
		assert context != null : "context != null";
		assert material != null : "material != null";
		assert material.isLoaded() : "material.isLoaded()";
		assert material.getShader().isLoaded() : "material.getShader().isLoaded()";
		assert isLoaded() : "isLoaded()";

		updateModelMatrix();

		// Use override material if given
		Material mat = (context.getOverrideMaterial() != null) ? context.getOverrideMaterial() : material;
		Shader shader = mat.getShader();

		mat.beforeRendering(context);
		shader.setMesh(this);
		shader.beforeRendering(context);

		glBindVertexArray(vao);

		// Instance positions
		if (instanceCount > 1 && shader.getInstancePositionsAttribute() > -1) {

			glBindBuffer(GL_ARRAY_BUFFER, instancesPositionBuffer);
			glBufferData(GL_ARRAY_BUFFER, instancePositions, GL_DYNAMIC_DRAW);

			// layout(location = 3) in vec3 instancePosition;
			glEnableVertexAttribArray(3);
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
			glVertexAttribDivisor(3, 1);

			glDrawElementsInstanced(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0, instanceCount);
		} else {
			glDrawElements(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0);
		}

		glBindVertexArray(0);

		shader.afterRendering(context);
		mat.afterRendering(context);
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

	public AABB getLocalAABB()
	{
		return new AABB(aabb);
	}

	/**
	 * Calculates the current AABB in world coordinates
	 *
	 * @todo optimize performance - like everywhere
	 * @return
	 */
	@Override
	public AABB getAABB()
	{
		//updateModelMatrix();
		AABB result = new AABB();
		result.setMax(new Vector3f(Float.NEGATIVE_INFINITY));
		result.setMin(new Vector3f(Float.POSITIVE_INFINITY));

		Vector3f[] corners = aabb.getCornerVectors();

		Matrix4f matrix = getTransform().getMatrix();

		for (Vector3f corner : corners) {

			Vector4f wPos = (new Vector4f(corner, 1.0f)).mul(matrix);

			corner = new Vector3f(wPos.x / wPos.w, wPos.y / wPos.w, wPos.z / wPos.w);

			result.merge(corner);
		}

		return result;
	}

	public int getInstanceCount()
	{
		return instanceCount;
	}

	public float[] getInstancePositions()
	{
		return instancePositions;
	}
	// </editor-fold>
}
