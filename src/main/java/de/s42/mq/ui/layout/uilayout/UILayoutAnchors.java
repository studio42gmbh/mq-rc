// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.mq.ui.layout.uilayout;

import org.joml.Vector2f;

/**
 *
 * @author Benjamin Schiller
 */
public final class UILayoutAnchors
{

	private UILayoutAnchors()
	{

	}

	public final static Vector2f LEFT_TOP = new Vector2f(-1.0f, 1.0f);
	public final static Vector2f LEFT_CENTER = new Vector2f(-1.0f, 0.0f);
	public final static Vector2f LEFT_BOTTOM = new Vector2f(-1.0f, -1.0f);
	public final static Vector2f CENTER_TOP = new Vector2f(0.0f, 1.0f);
	public final static Vector2f CENTER_CENTER = new Vector2f(0.0f, 0.0f);
	public final static Vector2f CENTER_BOTTOM = new Vector2f(0.0f, -1.0f);
	public final static Vector2f RIGHT_TOP = new Vector2f(1.0f, 1.0f);
	public final static Vector2f RIGHT_CENTER = new Vector2f(1.0f, 0.0f);
	public final static Vector2f RIGHT_BOTTOM = new Vector2f(1.0f, -1.0f);
}
