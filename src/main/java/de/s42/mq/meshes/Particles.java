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

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.data.IntegerData;
import de.s42.mq.shaders.ParticlesShader;
import de.s42.mq.util.MQMath;
import java.security.SecureRandom;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import de.s42.mq.dl.annotations.InDegreesDLAnnotation;
import de.s42.mq.dl.annotations.MaxDLAnnotation;
import de.s42.mq.dl.annotations.MinDLAnnotation;
import de.s42.mq.dl.annotations.StepDLAnnotation;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Particles extends Mesh
{

	private final static Logger log = LogManager.getLogger(Particles.class.getName());

	@AttributeDL(required = true)
	protected int count;
	
	@AttributeDL(required = true)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "1000000")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1")
	protected IntegerData displayCount = new IntegerData();

	protected final static float quadVertices[] = {
		// positions // uvs
		0.5f, -0.5f, 1.0f, 0.0f,
		-0.5f, 0.5f, 0.0f, 1.0f,
		-0.5f, -0.5f, 0.0f, 0.0f,
		-0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, -0.5f, 1.0f, 0.0f,
		0.5f, 0.5f, 1.0f, 1.0f
	};

	protected int vao = -1;
	protected int vbo = -1;
	protected int seedsBuffer = -1;

	public Particles()
	{
	}

	public Particles(int count)
	{
		assert count > 0;
		this.count = count;
	}
	
	@Override
	public Particles copy()
	{
		Particles copy = (Particles)super.copy();
		
		copy.count = count;
		copy.displayCount = displayCount;
		copy.vao = vao;
		copy.vbo = vbo;
		copy.seedsBuffer = seedsBuffer;

		return copy;
	}	

	@Override
	public void load() throws DLException
	{
		assert count > 0;
		
		if (isLoaded()) {
			return;
		}
		
		super.load();
		
		log.info("Generating particles {}", count);

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// Generate seeds for random id of each particle
		float seeds[] = new float[count];

		SecureRandom random = new SecureRandom();

		for (int i = 0; i < count; ++i) {
			seeds[i] = random.nextFloat();
		}

		seedsBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, seedsBuffer);
		glBufferData(GL_ARRAY_BUFFER, seeds, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Generate vertex buffer		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
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
	public void render()
	{
		assert material != null;
		assert material.isLoaded();
		assert isLoaded();
		assert material.getShader() instanceof ParticlesShader;

		ParticlesShader shader = (ParticlesShader) material.getShader();
		
		updateModelMatrix();
		
		material.beforeRendering();
		shader.setMesh(this);
		shader.beforeRendering();

		glBindVertexArray(vao);

		// positions
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(shader.getPositionAttribute());
		glVertexAttribPointer(shader.getPositionAttribute(), 2, GL_FLOAT, false, 4 * 4, 0L);

		// uvs
		glEnableVertexAttribArray(shader.getUvAttribute());
		glVertexAttribPointer(shader.getUvAttribute(), 2, GL_FLOAT, false, 4 * 4, 2L * 4L);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// seeds
		glBindBuffer(GL_ARRAY_BUFFER, seedsBuffer);
		glEnableVertexAttribArray(shader.getSeedAttribute());
		glVertexAttribPointer(shader.getSeedAttribute(), 1, GL_FLOAT, false, 4, 0L);
		glVertexAttribDivisor(shader.getSeedAttribute(), 1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glDrawArraysInstanced(GL_TRIANGLES, 0, 6, (int) MQMath.clamp(displayCount.getIntegerValue(), 0, count));

		glBindVertexArray(0);

		shader.afterRendering();
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public IntegerData getDisplayCount()
	{
		return displayCount;
	}

	public void setDisplayCount(IntegerData displayCount)
	{
		this.displayCount = displayCount;
	}
}
