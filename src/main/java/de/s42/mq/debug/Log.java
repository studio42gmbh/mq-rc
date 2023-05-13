/*
 * The MIT License
 *
 * Copyright 2021 Gods Playground (Cooperation F12, Studio 42 GmbH and John Matyas).
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
package de.s42.mq.debug;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.data.StringData;
import de.s42.mq.tasks.AbstractTask;

/**
 *
 * @author Benjamin Schiller
 */
public class Log extends AbstractTask
{
	private final static Logger log = LogManager.getLogger(Log.class.getName());

	@AttributeDL(required = true)
	protected StringData text;
	
	@AttributeDL(required = true, defaultValue = "INFO")
	protected LogLevel level;
		
	protected Object[] data;

	@Override
	protected void runTask()
	{
		assert getText() != null;

		// @todo this algorithm is rather simple and could well be optimized making the interpolation probably more powerful (formatting) and faster
		String interpolatedText = getText().getStringValue();

		if (getData() != null) {
			int i = 0;
			for (Object d : getData()) {
				interpolatedText = interpolatedText.replaceAll("\\$" + i, "" + d);
				i++;
			}
		}

		log.log(getLevel().level, interpolatedText);
	}

	public Object[] getData()
	{
		return data;
	}

	public void setData(Object[] data)
	{
		this.data = data;
	}

	public StringData getText()
	{
		return text;
	}

	public void setText(StringData text)
	{
		this.text = text;
	}

	public LogLevel getLevel()
	{
		return level;
	}

	public void setLevel(LogLevel level)
	{
		this.level = level;
	}
}
