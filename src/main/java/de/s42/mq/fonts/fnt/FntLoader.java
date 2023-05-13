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
package de.s42.mq.fonts.fnt;

import de.s42.base.files.FilesHelper;
import de.s42.mq.assets.AssetManager;
import de.s42.mq.fonts.*;
import de.s42.mq.fonts.fnt.parser.*;
import de.s42.mq.fonts.fnt.parser.FntParserParser.BoolContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.CharRContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.CommonContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.InfoContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.IntegerContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.KerningContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.PageRContext;
import de.s42.mq.fonts.fnt.parser.FntParserParser.StringContext;
import de.s42.mq.materials.Texture;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureWrap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 * http://www.angelcode.com/products/bmfont/ .fnt File
 *
 * @author Benjamin Schiller
 */
public class FntLoader extends FntParserBaseListener
{
	private final static Logger log = LogManager.getLogger(FntLoader.class.getName());

	private static class ErrorHandler extends BaseErrorListener
	{

		@Override
		public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int line, int position, String message, RecognitionException re)
		{
			message = message + " at " + line + ", " + position;

			throw new RuntimeException(message, re);
		}
	}

	private Font font;
	private GlyphPage currentPage;

	protected String get(StringContext string)
	{
		return string.getText();
	}

	protected int get(IntegerContext integer)
	{
		return Integer.parseInt(integer.getText());
	}

	protected float getFloat(IntegerContext integer)
	{
		return Float.parseFloat(integer.getText());
	}

	protected boolean get(BoolContext bool)
	{
		return Integer.parseInt(bool.getText()) == 1;
	}

	@Override
	public void exitInfo(InfoContext ctx)
	{
		font.setAntiAlias(get(ctx.aa));
		font.setBold(get(ctx.bold));
		font.setItalic(get(ctx.italic));
		font.setSmooth(get(ctx.smooth));
		font.setFace(get(ctx.face));
		font.setSize(getFloat(ctx.size));
		font.setStretchH(getFloat(ctx.stretchH));
		font.setPaddingTop(getFloat(ctx.paddingTop));
		font.setPaddingLeft(getFloat(ctx.paddingLeft));
		font.setPaddingBottom(getFloat(ctx.paddingBottom));
		font.setPaddingRight(getFloat(ctx.paddingRight));
		font.setSpacingX(getFloat(ctx.spacingX));
		font.setSpacingY(getFloat(ctx.spacingY));
	}

	@Override
	public void exitCommon(CommonContext ctx)
	{
		font.setLineHeight(getFloat(ctx.lineHeight));
		font.setBase(getFloat(ctx.base));
		font.setScaleW(getFloat(ctx.scaleW));
		font.setScaleH(getFloat(ctx.scaleH));
	}

	@Override
	public void enterPageR(PageRContext ctx)
	{
		// setup new page
		currentPage = new GlyphPage();

		Path pageTextureFile = font.getTextureDirectory().resolve(get(ctx.file));

		log.debug("Found pageTextureFile " + FilesHelper.createMavenNetbeansFileConsoleLink(pageTextureFile));

		Texture pageTexture = new Texture(pageTextureFile);
		pageTexture.setGenerateMipMap(false);
		pageTexture.setEnableAnisotropic(false);
		pageTexture.setMaxMipLevel(0);
		pageTexture.setMaxLod(0);
		pageTexture.setMinFilter(TextureFiltering.LINEAR);
		pageTexture.setMagFilter(TextureFiltering.LINEAR);
		pageTexture.setWrapR(TextureWrap.CLAMP_TO_EDGE);
		pageTexture.setWrapS(TextureWrap.CLAMP_TO_EDGE);
		pageTexture.setWrapT(TextureWrap.CLAMP_TO_EDGE);
		currentPage.setTexture(pageTexture);

		font.addPage(currentPage);
	}

	@Override
	public void exitCharR(CharRContext ctx)
	{
		Glyph glyph = new Glyph();

		glyph.setId(get(ctx.id));
		glyph.setWidth(getFloat(ctx.width));
		glyph.setHeight(getFloat(ctx.height));
		glyph.setX(getFloat(ctx.x));
		glyph.setY(getFloat(ctx.y));
		glyph.setxAdvance(getFloat(ctx.xadvance));
		glyph.setxOffset(getFloat(ctx.xoffset));
		glyph.setyOffset(getFloat(ctx.yoffset));
		glyph.setChannel(get(ctx.chnl));

		currentPage.addGlyph(glyph);
		font.addGlyph(glyph);
	}

	@Override
	public void exitKerning(KerningContext ctx)
	{
		int firstId = get(ctx.first);
		int secondId = get(ctx.second);
		float amount = getFloat(ctx.amount);

		font.getGlyph(firstId).addKerning(secondId, amount);
	}

	public static Font loadFromFile(AssetManager assetManager, Path source)
	{
		return loadFromFile(assetManager, source, new Font());
	}

	public static Font loadFromFile(AssetManager assetManager, Path source, Font font)
	{
		assert assetManager != null;
		assert source != null;
		assert Files.isRegularFile(source);
		assert font != null;
		assert !font.isLoaded();

		try {
			String fileContent = assetManager.getSingleFileSourceAsString(source);

			FntLoader loader = new FntLoader();
			loader.font = font;

			FntParserLexer lexer = new FntParserLexer(CharStreams.fromString(fileContent));
			lexer.removeErrorListeners();
			lexer.addErrorListener(new ErrorHandler());
			TokenStream tokens = new CommonTokenStream(lexer);

			FntParserParser parser = new FntParserParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new ErrorHandler());
			FntParserParser.FontContext context = parser.font();
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(loader, context);

			return font;
		}
		catch (IOException ex) {
			throw new RuntimeException("File not found " + source + " - " + ex.getMessage(), ex);
		}
	}
}
