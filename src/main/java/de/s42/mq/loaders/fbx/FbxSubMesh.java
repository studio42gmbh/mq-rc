// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Studio 42 GmbH ( https://www.s42m.de ).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.mq.loaders.fbx;

import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.mq.MQColor;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.shaders.Shader;
import static de.s42.mq.shaders.Shader.*;
import de.s42.mq.util.AABB;
import java.nio.IntBuffer;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
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

	public static class InstanceData
	{

		public Matrix4x3f matrix;
		public int identifier;
		public MQColor tint;
	}

	//private final static Logger log = LogManager.getLogger(FbxSubMesh.class.getName());
	protected int vao = -1;
	protected int vertexArrayBuffer = -1;
	protected int normalArrayBuffer = -1;
	protected int uvArrayBuffer = -1;
	protected int elementArrayBuffer = -1;
	protected int elementCount;

	protected int instanceCount = 1;
	protected int instanceDataBuffer = -1;
	protected float instanceData[] = new float[INSTANCE_DATA_BYTE_SIZE / 4];

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
		copy.instanceDataBuffer = instanceDataBuffer;
		copy.instanceData = instanceData;
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

		// Set initial instance
		Matrix4f transf = new Matrix4f();
		transf.identity();
		Matrix4x3f tr = transf.get4x3(new Matrix4x3f());
		tr.get(instanceData, 0);
		// Tint
		MQColor tint = MQColor.White;
		tint.getRGB(instanceData, 12);
		instanceData[15] = identifier;

		updateInstanceData(1, instanceData);

		log.trace("Loaded mesh " + faceCount);
	}

	/**
	 * The transforms are given as 4x3 matrix (column major) a - the fourth row will be created in the shader to save
	 * data. So each element is 12 float
	 *
	 * @param instanceCount
	 * @param instanceData
	 */
	public void updateInstanceData(int instanceCount, float[] instanceData)
	{
		assert instanceCount > 0 : "instanceCount > 0";
		assert instanceData.length == instanceCount * (INSTANCE_DATA_BYTE_SIZE / 4) : "instanceTransforms.length == instanceCount * (INSTANCE_TRANSFORM_BYTE_SIZE / 4)";

		this.instanceCount = instanceCount;
		this.instanceData = instanceData;

		glBindVertexArray(vao);
		instanceDataBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instanceDataBuffer);
		glBufferData(GL_ARRAY_BUFFER, instanceData, GL_DYNAMIC_DRAW);

		glBindVertexArray(0);
	}

	public void updateInstanceData(List<InstanceData> data)
	{
		assert data != null : "transforms != null";
		assert !data.isEmpty() : "!transforms.isEmpty()";

		int tSize = INSTANCE_DATA_BYTE_SIZE / 4;

		int iCount = data.size();

		float[] instData = new float[iCount * tSize];

		int i = 0;
		for (InstanceData entry : data) {

			entry.matrix.get(instData, i * tSize);
			entry.tint.getRGB(instData, i * tSize + 12);
			instData[i * tSize + 15] = entry.identifier;

			i++;
		}

		updateInstanceData(iCount, instData);
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
		glDeleteBuffers(instanceDataBuffer);

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
		//if (instanceCount > 1 && shader.getInstanceTransformC1Attribute() > -1) {
		glBindBuffer(GL_ARRAY_BUFFER, instanceDataBuffer);
		glBufferData(GL_ARRAY_BUFFER, instanceData, GL_DYNAMIC_DRAW);

		int stride = INSTANCE_DATA_BYTE_SIZE;

		// layout(location = 3) in vec3 instanceTransformC1;
		glEnableVertexAttribArray(LOCATION_INSTANCE_TRANSFORM_C1);
		glVertexAttribPointer(LOCATION_INSTANCE_TRANSFORM_C1, 3, GL_FLOAT, false, stride, 0 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_TRANSFORM_C1, 1);

		// layout(location = 4) in vec3 instanceTransformC2;
		glEnableVertexAttribArray(LOCATION_INSTANCE_TRANSFORM_C2);
		glVertexAttribPointer(LOCATION_INSTANCE_TRANSFORM_C2, 3, GL_FLOAT, false, stride, 1 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_TRANSFORM_C2, 1);

		// layout(location = 5) in vec3 instanceTransformC3;
		glEnableVertexAttribArray(LOCATION_INSTANCE_TRANSFORM_C3);
		glVertexAttribPointer(LOCATION_INSTANCE_TRANSFORM_C3, 3, GL_FLOAT, false, stride, 2 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_TRANSFORM_C3, 1);

		// layout(location = 6) in vec3 instanceTransformC4;
		glEnableVertexAttribArray(LOCATION_INSTANCE_TRANSFORM_C4);
		glVertexAttribPointer(LOCATION_INSTANCE_TRANSFORM_C4, 3, GL_FLOAT, false, stride, 3 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_TRANSFORM_C4, 1);

		// layout(location = 7) in vec3 instanceTint;
		glEnableVertexAttribArray(LOCATION_INSTANCE_TINT);
		glVertexAttribPointer(LOCATION_INSTANCE_TINT, 3, GL_FLOAT, false, stride, 4 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_TINT, 1);

		// layout(location = 8) in float instanceIdentifier;
		glEnableVertexAttribArray(LOCATION_INSTANCE_IDENTIFIER);
		glVertexAttribPointer(LOCATION_INSTANCE_IDENTIFIER, 1, GL_FLOAT, false, stride, 5 * 3 * 4);
		glVertexAttribDivisor(LOCATION_INSTANCE_IDENTIFIER, 1);

		glDrawElementsInstanced(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0, instanceCount);
		MQDebug.incDrawCallCount(1, instanceCount);

		glBindVertexArray(0);

		shader.afterRendering(context);
		mat.afterRendering(context);
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

	public int getInstanceCount()
	{
		return instanceCount;
	}

	public float[] getInstanceData()
	{
		return instanceData;
	}

	public int getVao()
	{
		return vao;
	}
	// </editor-fold>

}
