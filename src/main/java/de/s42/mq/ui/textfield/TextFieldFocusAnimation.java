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

import de.s42.mq.MQColor;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshAnimation;
import de.s42.mq.ui.UIManager;

/**
 *
 * @author Benjamin Schiller
 */
public class TextFieldFocusAnimation implements MeshAnimation
{

	@Override
	public void update(Mesh mesh, float deltaTime)
	{
		Textfield textfield = (Textfield) mesh;
		UIManager uiManager = textfield.getUiManager();

		if (uiManager.getFocusedComponent() == textfield) {
			textfield.getPanelComponent().getBorderColor().setValue(new MQColor(0.0f, 0.5f, 0.9f, 0.7f));
			textfield.getPanelComponent().getBackgroundColor().setValue(new MQColor(0.0f, 0.25f, 0.45f, 0.3f));
			textfield.getPanelComponent().getBorderWidth().setValue(2.0f);
		} else {
			textfield.getPanelComponent().getBorderColor().setValue(new MQColor(0.5f, 0.5f, 0.5f, 0.5f));
			textfield.getPanelComponent().getBackgroundColor().setValue(new MQColor(0.2f, 0.2f, 0.2f, 0.3f));
			textfield.getPanelComponent().getBorderWidth().setValue(2.0f);
		}
	}

	@Override
	public TextFieldFocusAnimation copy()
	{
		TextFieldFocusAnimation copy = new TextFieldFocusAnimation();

		return copy;
	}

}
