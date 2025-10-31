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
package de.s42.mq.data;

import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;

/**
 *
 * @author Benjamin Schiller
 */
public class BeanPropertyData extends AbstractData
{

	protected final Object bean;
	protected final BeanProperty property;

	public BeanPropertyData(Object bean, BeanProperty property)
	{
		super(property.getName());
		this.bean = bean;
		this.property = property;
	}

	@Override
	public Object getValue()
	{
		try {
			return property.read(bean);
		} catch (InvalidBean ex) {
			throw new RuntimeException("Error reading value - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void setValue(Object value)
	{
		try {
			property.write(bean, value);
		} catch (InvalidBean ex) {
			throw new RuntimeException("Error writing value - " + ex.getMessage(), ex);
		}
	}

	@Override
	public Class getDataType()
	{
		return property.getPropertyClass();
	}
}
