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
package de.s42.mq.input;

/**
 *
 * @author Benjamin Schiller
 */
public class Key
{

	protected final int key;
	protected final int scancode;
	protected final int action;
	protected final int mods;

	public static Key of(int key, int scancode, int action, int mods)
	{
		return new Key(key, scancode, action, mods);
	}

	public Key(int key, int scancode, int action, int mods)
	{
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
	}

	public int getKey()
	{
		return key;
	}

	public int getScancode()
	{
		return scancode;
	}

	public int getAction()
	{
		return action;
	}

	public int getMods()
	{
		return mods;
	}
}
