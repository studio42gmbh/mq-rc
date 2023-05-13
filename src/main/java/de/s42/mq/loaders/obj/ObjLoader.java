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

import de.s42.mq.assets.AssetManager;
import de.s42.mq.loaders.obj.parser.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.joml.Vector3f;

/**
 * https://en.wikipedia.org/wiki/Wavefront_.obj_file
 *
 * @author Benjamin Schiller
 */
public class ObjLoader extends ObjParserBaseListener
{

	private final static Logger log = LogManager.getLogger(ObjLoader.class.getName());

	private static class ErrorHandler extends BaseErrorListener
	{

		@Override
		public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int line, int position, String message, RecognitionException re)
		{
			message = message + " at " + line + ", " + position;

			throw new RuntimeException(message, re);
		}
	}

	final List<Vector3f> positions = new ArrayList();
	final List<Vector3f> normals = new ArrayList();
	final List<Vector3f> texturePositions = new ArrayList();
	final List<int[]> faces = new ArrayList();
	final List<String> faceMaterial = new ArrayList();
	final List<Boolean> faceSmooth = new ArrayList();
	String currentMaterial = "Default";
	boolean smoothNormals = false;

	@Override
	public void enterSmooth(ObjParserParser.SmoothContext ctx)
	{
		smoothNormals = ctx.KEYWORD_OFF() == null;
	}

	@Override
	public void enterUsematerial(ObjParserParser.UsematerialContext ctx)
	{
		currentMaterial = ctx.SYMBOL().getText();
	}

	@Override
	public void enterPosition(ObjParserParser.PositionContext ctx)
	{
		float x = Float.parseFloat(ctx.vector3().FLOAT(0).getText());
		float y = Float.parseFloat(ctx.vector3().FLOAT(1).getText());
		float z = Float.parseFloat(ctx.vector3().FLOAT(2).getText());

		//log.trace("POS", x, y, z);
		positions.add(new Vector3f(x, y, z));
	}

	@Override
	public void enterNormal(ObjParserParser.NormalContext ctx)
	{
		float x = Float.parseFloat(ctx.vector3().FLOAT(0).getText());
		float y = Float.parseFloat(ctx.vector3().FLOAT(1).getText());
		float z = Float.parseFloat(ctx.vector3().FLOAT(2).getText());

		//log.trace("NORMAL", x, y, z);
		normals.add(new Vector3f(x, y, z));
	}

	@Override
	public void enterTextureposition(ObjParserParser.TexturepositionContext ctx)
	{
		float u = Float.parseFloat(ctx.vector2().FLOAT(0).getText());
		float v = Float.parseFloat(ctx.vector2().FLOAT(1).getText());

		//log.trace("TEX", u, v);
		texturePositions.add(new Vector3f(u, 1.0f - v, 0.0f));
	}

	@Override
	public void enterFace(ObjParserParser.FaceContext ctx)
	{
		int p1 = Integer.parseInt(ctx.vertex(0).positionIndex().getText()) - 1;
		int t1 = Integer.parseInt(ctx.vertex(0).textureIndex().getText()) - 1;
		int n1 = Integer.parseInt(ctx.vertex(0).normalIndex().getText()) - 1;
		int p2 = Integer.parseInt(ctx.vertex(1).positionIndex().getText()) - 1;
		int t2 = Integer.parseInt(ctx.vertex(1).textureIndex().getText()) - 1;
		int n2 = Integer.parseInt(ctx.vertex(1).normalIndex().getText()) - 1;
		int p3 = Integer.parseInt(ctx.vertex(2).positionIndex().getText()) - 1;
		int t3 = Integer.parseInt(ctx.vertex(2).textureIndex().getText()) - 1;
		int n3 = Integer.parseInt(ctx.vertex(2).normalIndex().getText()) - 1;

		//log.trace("FACE", p1, t1, n1, p2, t2, n2, p3, t3, n3);		
		faces.add(new int[]{p1, t1, n1, p2, t2, n2, p3, t3, n3});
		faceMaterial.add(currentMaterial);
		faceSmooth.add(smoothNormals);
	}

	static ObjLoader loadFromSingleFileZipSource(AssetManager assetManager, Path source)
	{
		assert source != null;
		assert Files.isRegularFile(source);

		try {
			String fileContent = assetManager.getZippedSingleFileSourceAsString(source);

			ObjLoader loader = new ObjLoader();

			ObjParserLexer lexer = new ObjParserLexer(CharStreams.fromString(fileContent));
			lexer.removeErrorListeners();
			lexer.addErrorListener(new ErrorHandler());
			TokenStream tokens = new CommonTokenStream(lexer);

			ObjParserParser parser = new ObjParserParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new ErrorHandler());
			ObjParserParser.MeshesContext context = parser.meshes();
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(loader, context);

			log.debug("Loaded "
				+ loader.faces.size() + " triangles "
				+ loader.positions.size() + " positions "
				+ loader.normals.size() + " normals "
				+ loader.texturePositions.size() + " texturePositions"
				+ " from " + source.toAbsolutePath().toString()
			);

			//ObjMesh loadedMesh = new ObjMesh(loader);
			//loadedMesh.load();
			//return loadedMesh;
			return loader;
		} catch (IOException ex) {
			throw new RuntimeException("Resource not found " + source + " - " + ex.getMessage(), ex);
		}
	}
}
