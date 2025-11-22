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
package de.s42.mq.collision;

import de.s42.log.LogLevel;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.joml.Vector3f;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class SphereColliderTest
{

	private final static Logger log = LogManager.getLogger(SphereColliderTest.class.getName());

	protected final static Ray[] RAYS = {
		new Ray(new Vector3f(), (new Vector3f(1.0f, 0.0f, 0.0f)).normalize()),
		new Ray(new Vector3f(), (new Vector3f(1.0f, 0.1f, 0.0f)).normalize()),
		new Ray(new Vector3f(), (new Vector3f(1.0f, 0.0f, 0.2f)).normalize())};

	protected final static int COUNT = 1000000000;

	@Test(enabled = false)
	public void testIntersectPerformance()
	{
		for (int r = 0; r < 10; ++r) {

			SphereCollider collider = new SphereCollider(new Vector3f(2.0f, (float) Math.random() * 0.01f, 0.0f), 0.2f);
			Vector3f result = new Vector3f();
			int c = 0;
			int h = 0;

			log.start("testIntersect");

			while (c < COUNT) {

				for (int i = 0; i < RAYS.length; ++i) {

					if (collider.intersect(RAYS[i], result)) {
						h++;
					}

					c++;
				}
			}

			log.stop(LogLevel.INFO, "testIntersect", c);
			log.info("Hits", h);
		}
	}

}
