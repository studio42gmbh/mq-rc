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
package de.s42.mq.tasks;

import de.s42.dl.types.DLContainer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class Tasks implements DLContainer<Task>
{

	protected final List<Task> tasks = new ArrayList<>();

	public void add(Task task)
	{
		assert task != null;

		tasks.add(task);
	}

	public boolean remove(Task task)
	{
		assert task != null;

		return tasks.remove(task);
	}

	public boolean contains(Task task)
	{
		assert task != null;

		return tasks.contains(task);
	}

	public boolean isEmpty()
	{
		return tasks.isEmpty();
	}

	public int size()
	{
		return tasks.size();
	}

	@Override
	public void addChild(String name, Task child)
	{
		assert child != null;

		add(child);
	}

}
