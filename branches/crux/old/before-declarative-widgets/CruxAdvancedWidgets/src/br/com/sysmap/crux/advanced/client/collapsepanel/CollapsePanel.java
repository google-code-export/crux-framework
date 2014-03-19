/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.advanced.client.collapsepanel;

import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseHandler;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseOrBeforeExpandEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandHandler;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.HasBeforeCollapseAndBeforeExpandHandlers;
import br.com.sysmap.crux.advanced.client.titlepanel.TitlePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * Panel based on a 3x3 table, with collapse/expand feature. Similar to GWT's DisclosurePanel
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class CollapsePanel extends TitlePanel implements HasBeforeCollapseAndBeforeExpandHandlers
{
	private static final String DEFAULT_STYLE_NAME = "crux-CollapsePanel" ;
	private boolean collapsible = false;
	private boolean collapsed = false;
	private String height;
	private CollapsePanelImages images;
	private Image image = null;
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param collapsed
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean collapsed)
	{
		this(width, height, styleName, collapsible, collapsed, (CollapsePanelImages) GWT.create(CollapsePanelImages.class));
	}	
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param collapsed
	 * @param images
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean collapsed, CollapsePanelImages images)
	{
		super(width, collapsible ? "" : height, styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		this.images = images;
		this.collapsed = collapsed;
		this.image = createCollapseExpandImage();
		
		setCollapsible(collapsible);
	}
	
	/**
	 * 
	 * @return
	 */
	private Image createCollapseExpandImage()
	{
		// TODO - insert images using CSS  
		
		AbstractImagePrototype proto = collapsed ? images.expand() : images.collapse();
		Image image = proto.createImage();
		image.addClickHandler(new ExpandButtonClickHandler());
		setTopRightWidget(image);
		DOM.setStyleAttribute(image.getElement(), "marginRight", "4px");
		return image;
	}

	/**
	 * Enables or disables the collapse/expand feature
	 * @param collapsible the collapsible to set
	 */
	public void setCollapsible(boolean collapsible)
	{
		this.collapsible = collapsible;
		getTable().setPropertyString("height", collapsible ? "" : this.height);
		setCollapsed(this.collapsed);
	}
	
	/**
	 * Collapses or expands the panel
	 * @param collapsed the collapsed to set
	 */
	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
		
		String display = collapsed ? "none" : "";
		getMiddleLine().getStyle().setProperty("display", display);
		getBottomLine().getStyle().setProperty("display", display);
		
		if(collapsible)
		{
			if(collapsed)
			{
				images.expand().applyTo(image);
			}
			else
			{
				images.collapse().applyTo(image);
			}
		}
		
		image.setVisible(collapsible);
	}

	/**
	 * @return true if collapsible
	 */
	public boolean isCollapsible()
	{
		return collapsible;
	}

	/**
	 * @return true if collapsed
	 */
	public boolean isCollapsed()
	{
		return collapsed;
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.collapseexpand.HasBeforeCollapseHandlers#addBeforeCollapseHandler(br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseHandler)
	 */
	public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseHandler handler)
	{
		return addHandler(handler, BeforeCollapseEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.collapseexpand.HasBeforeExpandHandlers#addBeforeExpandHandler(br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandHandler)
	 */
	public HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler handler)
	{
		return addHandler(handler, BeforeExpandEvent.getType());
	}

	/**
	 * Changes the images of the collapse/expand button
	 * @param images
	 */
	public void setImages(CollapsePanelImages images)
	{
		// TODO - insert images using CSS
		this.images = images;
	}
	
	@Override
	public void setHeight(String height)
	{
		if(!this.collapsible)
		{
			super.setHeight(height);
		}
		else
		{
			super.setHeight("");
		}
	}
}

/**
 * Button that collapses or expands the panel
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
class ExpandButtonClickHandler implements ClickHandler
{
	/**
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event)
	{
		Image img = (Image) event.getSource();
		CollapsePanel panel = (CollapsePanel) img.getParent();
		boolean collapsed = panel.isCollapsed();
		BeforeCollapseOrBeforeExpandEvent preEvent = null;
		
		if(!collapsed)
		{
			preEvent = BeforeCollapseEvent.fire(panel);
		}
		else
		{
			preEvent = BeforeExpandEvent.fire(panel);
		}
		
		if(!preEvent.isCanceled())
		{
			panel.setCollapsed(!panel.isCollapsed());
		}
	}
}