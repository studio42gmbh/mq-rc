// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2024 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.mq.ui.textfield;

import de.s42.base.math.MathHelper;
import de.s42.mq.fonts.Text;
import static de.s42.mq.fonts.Text.HorizontalAlignment.CENTER;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshAnimation;
import de.s42.mq.ui.UIManager;
import de.s42.mq.ui.layout.uilayout.UILayoutOptions;
import org.joml.Vector2f;

/**
 *
 * @author Benjamin Schiller
 */
public class TextFieldCaretAnimation implements MeshAnimation
{

	protected float time;

	@Override
	public void update(Mesh mesh, float deltaTime)
	{
		Textfield textfield = (Textfield) mesh;

		UIManager uiManager = textfield.getUiManager();

		time += deltaTime;

		// Not focused -> hide caret
		if (uiManager.getFocusedComponent() != textfield) {
			textfield.getCaretComponent().getText().setValue("");
			return;
		}

		String text = textfield.getText().getValue();
		Text textComponent = textfield.getTextComponent();
		Text caretComponent = textfield.getCaretComponent();
		UILayoutOptions options = textComponent.getLayoutOptions().copy();

		// Translate to startpos
		Vector2f textDimension = textComponent.computeDimension(text);

		if (textComponent.getHorizontalAlignment() == CENTER) {
			options.translateLeftTop(textDimension.x * -0.5f, 0.0f);
			options.translateRightBottom(textDimension.x * -0.5f, 0.0f);
		} else if (textComponent.getHorizontalAlignment() == Text.HorizontalAlignment.RIGHT) {
			options.translateLeftTop(textDimension.x * -1.0f, 0.0f);
			options.translateRightBottom(textDimension.x * -1.0f, 0.0f);
		}

		// Translate caret offset
		Vector2f caretDimension = caretComponent.computeDimension("|");

		if (textComponent.getHorizontalAlignment() == Text.HorizontalAlignment.RIGHT) {
			options.translateLeftTop(caretDimension.x * 0.5f, 0.0f);
			options.translateRightBottom(caretDimension.x * 0.5f, 0.0f);
		} else if (textComponent.getHorizontalAlignment() == Text.HorizontalAlignment.LEFT) {
			options.translateLeftTop(caretDimension.x * -0.5f, 0.0f);
			options.translateRightBottom(caretDimension.x * -0.5f, 0.0f);
		}

		// Translate to caret pos
		Vector2f caretBeforeDimension = textComponent.computeDimension(text.substring(0, textfield.getCaretPosition()));
		options.translateLeftTop(caretBeforeDimension.x, 0.0f);
		options.translateRightBottom(caretBeforeDimension.x, 0.0f);

		caretComponent.setLayoutOptions(options);

		if (MathHelper.fract(time) > 0.5) {
			textfield.getCaretComponent().getText().setValue("");
		} else {
			textfield.getCaretComponent().getText().setValue("|");
		}
	}

	@Override
	public TextFieldCaretAnimation copy()
	{
		TextFieldCaretAnimation copy = new TextFieldCaretAnimation();

		copy.time = time;

		return copy;
	}
}
