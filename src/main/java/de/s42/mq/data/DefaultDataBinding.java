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

import de.s42.base.beans.InvalidBean;
import de.s42.dl.ui.bindings.DefaultBinding;
import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 * @param <ObjectType>
 * @param <DataType>
 */
public class DefaultDataBinding<ObjectType, DataType extends Data> extends DefaultBinding<ObjectType, DataType>
{

	protected Object currentDataValue;

	public DefaultDataBinding(ObjectType object, String name) throws InvalidBean
	{
		super(object, name);
	}

	@Override
	public boolean updateValue()
	{
		try {
			DataType value = property.read(object);

			// Just update if the value has changed
			if (!Objects.equals(currentValue, value)) {
				currentValue = value;
				if (currentValue != null) {
					currentDataValue = value.getValue();
				} else {
					currentDataValue = null;
				}
				updateListeners(value);
				return true;
			}

			// Just update if the value of the data has changed
			Object dataValue = value.getValue();
			if (!Objects.equals(currentDataValue, dataValue)) {
				currentDataValue = dataValue;
				updateListeners(value);
				return true;
			}

			return false;
		} catch (InvalidBean ex) {
			throw new RuntimeException(ex);
		}
	}
}
