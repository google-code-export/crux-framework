/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.menu;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.TextChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Context for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
class MenuContext extends WidgetCreatorContext
{
	LinkedList<String> itemStack = new LinkedList<String>();
	String currentItem;
	String selectHandler;
}

/**
 * Factory for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
@DeclarativeFactory(id="menu", library=Constants.LIBRARY_NAME, targetWidget=Menu.class, 
	description="A menu class based in nav, ul and li html tags.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="largeType", type=LargeType.class, defaultValue="VerticalAccordion"),
	@TagAttributeDeclaration(value="smallType", type=SmallType.class, defaultValue="VerticalAccordion")
})
@TagChildren({
	@TagChild(MenuFactory.MenuItemProcessor.class)
})
public class MenuFactory extends WidgetCreator<MenuContext> implements HasSelectionHandlersFactory<WidgetCreatorContext>
{
	
	protected static Logger logger = Logger.getLogger(MenuFactory.class.getName());
	
	@Override
	public void processAttributes(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		
		LargeType largeType = LargeType.VERTICAL_ACCORDION;
		String largeTypeProp = context.readWidgetProperty("largeType");
		if (largeTypeProp != null && largeTypeProp.length() > 0)
		{
			largeType = LargeType.getByName(largeTypeProp);
		}

		SmallType smallType = SmallType.VERTICAL_ACCORDION;
		String smallTypeProp = context.readWidgetProperty("smallType");
		if (smallTypeProp != null && smallTypeProp.length() > 0)
		{
			smallType = SmallType.getByName(smallTypeProp);
		}
		
		out.println("final "+className + " " + context.getWidget()+" = new "+ className +
				"("+LargeType.class.getCanonicalName()+"."+largeType.name()+","+SmallType.class.getCanonicalName()+"."+smallType.name()+");");
	}
	
	@Override
	public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		context.itemStack.add(context.getWidget());
	}
	
	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded", description="The menu item.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="open", type=Boolean.class, description="open or close an item."),
		@TagAttributeDeclaration(value="style", description="the item style."),
		@TagAttributeDeclaration(value="disabled", type=Boolean.class, defaultValue = "false", description="indicate if the item should be disabled or not."),
		@TagAttributeDeclaration(value="styleName", supportsResources=true, description="the item style name."),
		@TagAttributeDeclaration(value="id", description="The component id."),
		@TagAttributeDeclaration(value="value", description="Any value that will be associated with this menu item.")
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onSelect")
	})
	@TagChildren({
		@TagChild(ChoiceItemProcessor.class),
		@TagChild(MenuItemProcessor.class)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<MenuContext> implements HasPostProcessor<MenuContext>
	{
		StyleProcessor styleProcessor;
		StyleNameProcessor styleNameProcessor;
		boolean rootProcessed;
		
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			String item = getWidgetCreator().createVariableName("item");

			context.currentItem = item;
			
			context.selectHandler = context.readChildProperty("onSelect");
		}
		
		/**
		 * Sets the item attributes before adding it to the parent.
		 * @param out
		 * @param context
		 * @param item
		 */
		private void setItemAttributes(SourcePrinter out, MenuContext context, String item)
		{
			if(context.readBooleanChildProperty("open", false))
			{
				out.println(item + ".open();");					
			}
			
			String id = context.readChildProperty("id");
			if(!StringUtils.isEmpty(id))
			{
				out.println(item + ".setId("+ EscapeUtils.quote(id) +");");					
			}
			
			if(context.readBooleanChildProperty("disabled", false))
			{
				out.println(item + ".setEnabled(false);");	
			}

			String style = context.readChildProperty("style");
			if (!StringUtils.isEmpty(style))
			{
				if (styleProcessor == null)
				{
			        styleProcessor = new StyleProcessor(getWidgetCreator());
				}
				styleProcessor.processAttribute(out, item, style);
			}
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				if (styleNameProcessor == null)
				{
			        styleNameProcessor = new StyleNameProcessor(getWidgetCreator());
				}
				styleNameProcessor.processAttribute(out, item, styleName);
			}
			
			String itemValue = context.readChildProperty("value");
			if (!StringUtils.isEmpty(itemValue))
			{
				out.println(item + ".setValue(" + EscapeUtils.quote(itemValue) + ");");
			}
		}			
		
		@Override
		public void postProcessChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
		{
			String onSelectEvent = context.selectHandler;
			if (onSelectEvent != null && onSelectEvent.length() > 0)
			{
				out.println(context.currentItem+".addSelectHandler(new "+SelectHandler.class.getCanonicalName()+"(){");
				out.println("public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");

				EvtProcessor.printEvtCall(out, onSelectEvent, "onSelect", SelectEvent.class, "event", getWidgetCreator());

				out.println("}");
				out.println("});");
			}
			
			
			String item = context.itemStack.removeFirst();			
			setItemAttributes(out, context, item);
		}
	}
	
	@TagChildren({
		@TagChild(ItemLabelProcessor.class),
		@TagChild(ItemHTMLProcessor.class),
		@TagChild(ItemWidgetProcessor.class)
	})
	@TagConstraints
	public static class ChoiceItemProcessor extends ChoiceChildProcessor<MenuContext>
	{
	}
	
	@TagConstraints(tagName="itemLabel", type=String.class, description="Create an item with a simple Label.")
	public static class ItemLabelProcessor extends WidgetChildProcessor<MenuContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			JSONObject jsonLabel = context.getChildElement();
			String label = "";
			try {
				label = jsonLabel.getString("_text");
			} catch (JSONException e) 
			{
				logger.log(Level.SEVERE, e.getMessage());
			}
			
			String itemClassName = MenuItem.class.getCanonicalName();
			
			if(label != null && label.length() > 0)
			{
				label = getWidgetCreator().getDeclaredMessage(label);
			}
			
			if(context.itemStack.size() == 1)
			{
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem("+ label +");");
			} 
			else
			{
				String parentItem = context.itemStack.getFirst();
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem(" +parentItem +","+ label +");");
			}
			
			context.itemStack.addFirst(context.currentItem);
		}
	}
	
	@TagConstraints(tagName="itemHtml", description="Create an item with a HTML body.")
	@TagChildren({
		@TagChild(HTMLProcessor.class)
	})
	public static class ItemHTMLProcessor extends WidgetChildProcessor<MenuContext> 
	{
	}
	
	@TagConstraints(type=HTMLTag.class)
	public static class HTMLProcessor extends TextChildProcessor<MenuContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			String itemClassName = MenuItem.class.getCanonicalName();
			
			String safeHtmlStr = "(new "+SafeHtmlBuilder.class.getCanonicalName()+".appendHtmlConstant("
					+getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId())+")).toSafeHtml()";
			
			if(context.itemStack.size() == 1)
			{
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem("+ safeHtmlStr +");");
			}
			else
			{
				String parentItem = context.itemStack.getFirst();
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem(" +parentItem +","+ safeHtmlStr +");");
			}
			
			context.itemStack.addFirst(context.currentItem);
		}
	}
	
	@TagConstraints(tagName="itemWidget")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})
	public static class ItemWidgetProcessor extends WidgetChildProcessor<MenuContext> 
	{
	}
	
	@TagConstraints(type=AnyWidget.class, description="The widget inserted into the item.")
	public static class WidgetProcessor extends WidgetChildProcessor<MenuContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			
			String itemClassName = MenuItem.class.getCanonicalName();
			
			if(context.itemStack.size() == 1)
			{
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem("+ widget +");");
			}
			else
			{
				String parentItem = context.itemStack.getFirst();
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem(" +parentItem +","+ widget +");");
			}
			
			context.itemStack.addFirst(context.currentItem);
			
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}
	
	@Override
    public MenuContext instantiateContext()
    {
	    return new MenuContext();
    }
}