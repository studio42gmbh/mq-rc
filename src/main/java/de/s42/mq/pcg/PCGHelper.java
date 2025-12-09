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
package de.s42.mq.pcg;

import de.s42.dl.exceptions.DLException;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Cube;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.meshes.Sphere;
import de.s42.mq.pcg.points.PCGPoints;
import de.s42.mq.pcg.points.StandardPCGPoints;
import de.s42.mq.pcg.voxels.PCGVoxels;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public final class PCGHelper
{

	private PCGHelper()
	{
		// Never instantiated
	}

	public static void createGizmos(PCGPoints points, MeshGroup meshes, Material gizmoMaterial)
	{
		assert points != null : "points != null";
		assert meshes != null : "meshes != null";
		assert gizmoMaterial != null : "gizmoMaterial != null";

		try {
			float[] data = points.getData();
			int componentSize = points.getComponentSize();
			for (int i = 0; i < data.length; i += componentSize) {

				if (StandardPCGPoints.retrieveIsVisible(data, i)) {
					Sphere gizmo = new Sphere(0.1f, 10, 10);
					gizmo.setPosition(new Vector3f(data[i], data[i + 1], data[i + 2]));
					gizmo.setMaterial(gizmoMaterial);
					gizmo.setLayers("gizmos");
					gizmo.setCustomProperty("type", "Gizmo");
					gizmo.load();
					meshes.addMesh(gizmo);
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException();
		}
	}

	public static void createGizmos(PCGVoxels voxels, MeshGroup meshes, Material gizmoMaterial)
	{
		assert voxels != null : "voxels != null";
		assert meshes != null : "meshes != null";
		assert gizmoMaterial != null : "gizmoMaterial != null";

		try {
			int width = voxels.getWidth();
			int height = voxels.getHeight();
			int depth = voxels.getDepth();

			float spacing = 1.0f;

			Vector3f origin = voxels.getOrigin();

			for (int x = 0; x < width; ++x) {
				for (int y = 0; y < height; ++y) {
					for (int z = 0; z < depth; ++z) {

						int value = voxels.get(x, y, z);

						if (value == 0) {
							continue;
						}

						Cube gizmo = new Cube();
						gizmo.setPosition(new Vector3f((float) x * spacing + origin.x, (float) y * spacing + origin.y, (float) z * spacing + origin.z));
						gizmo.setMaterial(gizmoMaterial);
						gizmo.setLayers("gizmos");
						gizmo.setCustomProperty("type", "Gizmo");
						gizmo.setScale(new Vector3f(0.5f));
						gizmo.updateModelMatrix(true);
						gizmo.load();
						meshes.addMesh(gizmo);
					}
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException();
		}
	}
}
