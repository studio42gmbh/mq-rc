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
package de.s42.mq.rendering;

import de.s42.mq.cameras.Camera;
import de.s42.mq.materials.Material;
import de.s42.mq.materials.Texture;
import de.s42.mq.shaders.Shader.CullType;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultRenderContext implements RenderContext
{

	protected int tick;

	protected float deltaTime;

	protected float totalTime;

	protected Material overrideMaterial;

	protected Camera shadowCamera;

	protected Texture shadowTexture;

	protected CullType overrideCullType;

	@Override
	public int getTick()
	{
		return tick;
	}

	public void setTick(int tick)
	{
		this.tick = tick;
	}

	@Override
	public float getDeltaTime()
	{
		return deltaTime;
	}

	public void setDeltaTime(float deltaTime)
	{
		this.deltaTime = deltaTime;
	}

	@Override
	public float getTotalTime()
	{
		return totalTime;
	}

	public void setTotalTime(float totalTime)
	{
		this.totalTime = totalTime;
	}

	@Override
	public Material getOverrideMaterial()
	{
		return overrideMaterial;
	}

	public void setOverrideMaterial(Material overrideMaterial)
	{
		this.overrideMaterial = overrideMaterial;
	}

	@Override
	public Camera getShadowCamera()
	{
		return shadowCamera;
	}

	public void setShadowCamera(Camera shadowCamera)
	{
		this.shadowCamera = shadowCamera;
	}

	@Override
	public Texture getShadowTexture()
	{
		return shadowTexture;
	}

	public void setShadowTexture(Texture shadowTexture)
	{
		this.shadowTexture = shadowTexture;
	}

	public CullType getOverrideCullType()
	{
		return overrideCullType;
	}

	public void setOverrideCullType(CullType overrideCullType)
	{
		this.overrideCullType = overrideCullType;
	}
}
