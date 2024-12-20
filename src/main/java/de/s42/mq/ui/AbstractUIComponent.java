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
package de.s42.mq.ui;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.core.AbstractEntity;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractUIComponent extends AbstractEntity implements UIComponent
{

	protected int identifier = -1;

	protected boolean focusable;

	@AttributeDL(ignore = true)
	protected UIManager uiManager;

	protected Layout layout;

	protected LayoutOptions layoutOptions;

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		// Implement
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		// Implement
	}

	@Override
	public void handleChar(String chars)
	{
		// Implement
	}

	@Override
	public int getIdentifier()
	{
		return identifier;
	}

	@Override
	public void setIdentifier(int identifier)
	{
		this.identifier = identifier;
	}

	@Override
	@AttributeDL(ignore = true)
	public UIManager getUiManager()
	{
		return uiManager;
	}

	@Override
	@AttributeDL(ignore = true)
	public void setUiManager(UIManager uiManager)
	{
		this.uiManager = uiManager;
	}

	@Override
	public Layout getLayout()
	{
		return layout;
	}

	@Override
	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	@Override
	public LayoutOptions getLayoutOptions()
	{
		return layoutOptions;
	}

	@Override
	public void setLayoutOptions(LayoutOptions layoutOptions)
	{
		this.layoutOptions = layoutOptions;
	}

	@Override
	public boolean isFocusable()
	{
		return focusable;
	}

	@Override
	public void setFocusable(boolean focusable)
	{
		this.focusable = focusable;
	}
}
