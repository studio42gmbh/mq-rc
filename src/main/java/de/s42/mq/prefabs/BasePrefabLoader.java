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
package de.s42.mq.prefabs;

import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.Asset;
import de.s42.mq.assets.Assets;
import de.s42.mq.core.Copyable;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class BasePrefabLoader implements PrefabLoader
{

	protected Assets prepareInstance(Prefab prefab, Object... context)
	{
		assert prefab != null : "prefab != null";

		// Copy first asset and create
		Assets instance = new Assets();

		// Copy or link assets
		for (Asset asset : ((List<Asset>) prefab.getAssets())) {
			if (asset instanceof Copyable copyable) {
				instance.add((Asset) copyable.copy());
			} else {
				instance.add(asset);
			}
		}

		return instance;
	}

	protected void preLoadInstance(Prefab prefab, Assets instance, Object... context) throws DLException
	{
		// IMPLEMENT
	}

	protected void loadInstance(Prefab prefab, Assets instance, Object... context) throws DLException
	{
		assert prefab != null : "prefab != null";
		assert instance != null : "instance != null";

		instance.load();
	}

	protected void postLoadInstance(Prefab prefab, Assets instance, Object... context) throws DLException
	{
		// IMPLEMENT
	}

	@Override
	public Assets instantiate(Prefab prefab, Object... context) throws DLException
	{
		assert prefab != null : "prefab != null";

		// Copy first asset and create
		Assets instance = prepareInstance(prefab, context);

		preLoadInstance(prefab, instance, context);

		loadInstance(prefab, instance, context);

		postLoadInstance(prefab, instance, context);

		return instance;
	}
}
