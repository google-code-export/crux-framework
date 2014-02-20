/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.widgets.client.colorpicker;

import org.cruxframework.crux.widgets.client.util.ColorUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

public class SaturationLightnessPicker extends Composite implements HasValueChangeHandlers<String>
{
	private Canvas canvas;
	private int hue = 180;
	private int handleX = 90;
	private int handleY = 90;
	private boolean mouseDown;

	public SaturationLightnessPicker()
	{
		canvas = Canvas.createIfSupported();
		canvas.setStylePrimaryName("saturationPicker");
		canvas.setPixelSize(180, 180);
		canvas.setCoordinateSpaceHeight(180);
		canvas.setCoordinateSpaceWidth(180);

		initWidget(canvas);

		canvas.addMouseDownHandler(new MouseDownHandler()
		{
			public void onMouseDown(MouseDownEvent event)
			{
				handleX = event.getRelativeX(canvas.getElement());
				handleY = event.getRelativeY(canvas.getElement());
				drawGradient(false);
				String color = getColorAtPixel(handleX, handleY);
				drawGradient(true);
				fireColorChanged(color);

				mouseDown = true;
			}
		});
		canvas.addMouseMoveHandler(new MouseMoveHandler()
		{
			public void onMouseMove(MouseMoveEvent event)
			{
				if (mouseDown)
				{
					handleX = event.getRelativeX(canvas.getElement());
					handleY = event.getRelativeY(canvas.getElement());
					drawGradient(false);
					String color = getColorAtPixel(handleX, handleY);
					drawGradient(true);
					fireColorChanged(color);
				}
			}
		});
		canvas.addMouseUpHandler(new MouseUpHandler()
		{
			public void onMouseUp(MouseUpEvent event)
			{
				mouseDown = false;
			}
		});
		canvas.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event)
			{
				mouseDown = false;
			}
		});
	}

	@Override
	protected void onAttach()
	{
		super.onAttach();
		drawGradient(true);
	}

	private void drawGradient(boolean drawHandle)
	{
		Context2d ctx = canvas.getContext2d();

		// draw gradient
		for (int x = 0; x <= 179; x++)
		{
			CanvasGradient grad = ctx.createLinearGradient(x, 0, x, 179);
			int s = Math.round(x * 100 / 179);
			String hex = ColorUtils.hsl2hex(hue, s, 0);
			grad.addColorStop(0, "#" + hex); 
			hex = ColorUtils.hsl2hex(hue, s, 100);
			grad.addColorStop(1, "#" + hex); 
			ctx.setFillStyle(grad);
			ctx.fillRect(x, 0, 1, 180);
		}

		// draw handle
		if (drawHandle)
		{
			ctx.beginPath();
			ctx.arc(handleX, handleY, 3, 0, Math.PI * 2, false);
			ctx.closePath();
			ctx.setFillStyle("#ffffff"); 
			ctx.fill();

			ctx.beginPath();
			ctx.arc(handleX, handleY, 2, 0, Math.PI * 2, false);
			ctx.closePath();
			ctx.setFillStyle("#000000"); 
			ctx.fill();
		}
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private void fireColorChanged(String color)
	{
		ValueChangeEvent.fire(this, color);
	}

	private String getColorAtPixel(int x, int y)
	{
		x = Math.max(Math.min(x, 179), 0);
		y = Math.max(Math.min(y, 179), 0);
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.getImageData(x, y, 1, 1);
		CanvasPixelArray data = imageData.getData();
		return ColorUtils.rgb2hex(data.get(0), data.get(1), data.get(2));
	}

	public void setHue(int hue)
	{
		this.hue = hue;
		drawGradient(false);
		String color = getColorAtPixel(handleX, handleY);
		drawGradient(true);
		fireColorChanged(color);
	}

	public String getColor()
	{
		drawGradient(false);
		String color = getColorAtPixel(handleX, handleY);
		drawGradient(true);
		return color;
	}

	public void setColor(String color)
	{
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		hue = hsl[0];
		handleX = (int) Math.min(Math.max(Math.round(hsl[1] * 180d / 100d), 0), 179);
		handleY = (int) Math.min(Math.max(Math.round(hsl[2] * 180d / 100d), 0), 179);
		drawGradient(true);
		fireColorChanged(color);
	}
}
