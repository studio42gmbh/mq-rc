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
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.meshes.Sphere;
import de.s42.mq.pcg.images.PCGImage;
import de.s42.mq.pcg.images.StandardPCGImage;
import de.s42.mq.pcg.points.PCGPoints;
import de.s42.mq.pcg.points.StandardPCGPoints;
import de.s42.mq.util.AABB;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGContext implements PCGContext
{

	protected boolean debug;
	protected AABB bounds;
	protected MeshGroup meshes;
	protected Material gizmoMaterial;
	protected String logComponent = "Unknown";
	protected Logger log;

	protected final List<Mesh> gizmos = new ArrayList<>();

	public StandardPCGContext()
	{
	}

	@Override
	public PCGImage loadImage(String id)
	{
		assert id != null : "id != null";

		// @todo
		StandardPCGImage image = new StandardPCGImage(id);
		image.load();

		return image;
	}

	@Override
	public PCGPoints createPoints(int count)
	{
		assert count >= 0 : "count >= 0";

		return new StandardPCGPoints(count);
	}

	@Override
	public PCGPoints createPoints(int count, int extendedComponentSize)
	{
		assert count >= 0 : "count >= 0";
		assert extendedComponentSize >= 0 : "extendedComponentSize >= 0";

		return new StandardPCGPoints(count, extendedComponentSize);
	}

	public void removeGizmos()
	{
		assert meshes != null : "meshes != null";

		for (Mesh gizmo : gizmos) {
			meshes.removeMesh(gizmo);
		}
	}

	@Override
	public void createDebugSphereGizmos(PCGPoints points)
	{
		assert points != null : "points != null";

		if (!debug) {
			return;
		}

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
					gizmo.setLayers("transparent");
					gizmo.load();
					gizmos.add(gizmo);
					meshes.addMesh(gizmo);
				}
			}
		} catch (DLException ex) {
			throw new RuntimeException();
		}
	}

	@Override
	public void error(Object... message)
	{
		if (log == null) {
			log = LogManager.getLogger(logComponent);
		}

		log.error(message);
	}

	@Override
	public void warn(Object... message)
	{
		if (log == null) {
			log = LogManager.getLogger(logComponent);
		}

		log.warn(message);
	}

	@Override
	public void info(Object... message)
	{
		if (log == null) {
			log = LogManager.getLogger(logComponent);
		}

		log.info(message);
	}

	@Override
	public void debug(Object... message)
	{
		if (log == null) {
			log = LogManager.getLogger(logComponent);
		}

		log.debug(message);
	}

	@Override
	public int getBoundsId()
	{
		return bounds.hashCode();
	}

	@Override
	public AABB getBounds()
	{
		return bounds;
	}

	public void setBounds(AABB bounds)
	{
		this.bounds = bounds;
	}

	@Override
	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public MeshGroup getMeshes()
	{
		return meshes;
	}

	public void setMeshes(MeshGroup meshes)
	{
		this.meshes = meshes;
	}

	public List<Mesh> getGizmos()
	{
		return gizmos;
	}

	public Material getGizmoMaterial()
	{
		return gizmoMaterial;
	}

	public void setGizmoMaterial(Material gizmoMaterial)
	{
		this.gizmoMaterial = gizmoMaterial;
	}

	public String getLogComponent()
	{
		return logComponent;
	}

	public void setLogComponent(String logComponent)
	{
		this.logComponent = logComponent;
	}
}
