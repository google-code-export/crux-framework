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
package br.com.sysmap.crux.gwt.rebind;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.gwt.client.ExecuteEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

class MenuBarContext extends WidgetCreatorContext
{
	String caption;
	boolean isHtml;
	
	public void clearAttributes() 
	{
		caption = null;
		isHtml = false;
	}
}

/**
 * A Factory for MenuBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="menuBar", library="gwt", targetWidget=MenuBar.class)
@TagAttributes({
	@TagAttribute(value="autoOpen", type=Boolean.class), 
	@TagAttribute(value="focusOnHoverEnabled", type=Boolean.class) 
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="vertical", type=Boolean.class)
})
@TagChildren({
	@TagChild(MenuBarFactory.MenutItemsProcessor.class)
})
public class MenuBarFactory extends WidgetCreator<MenuBarContext> 
       implements HasAnimationFactory<MenuBarContext>, HasCloseHandlersFactory<MenuBarContext>
{
	protected GWTMessages messages = MessagesFactory.getMessages(GWTMessages.class);
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("menuBar");
		String className = MenuBar.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"("+isMenuVertical(metaElem)+");");
		return varName;
	}	

	/**
	 * @param element
	 * @return
	 */
	private boolean isMenuVertical(JSONObject element)
	{
		String verticalStr = element.optString("vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}
		return vertical;
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(MenutItemProcessor.class),
		@TagChild(MenutItemSeparatorProcessor.class)
	})
	public static class MenutItemsProcessor extends ChoiceChildProcessor<MenuBarContext> {}
	
	@TagChildAttributes(tagName="menuItem")
	@TagChildren({
		@TagChild(CaptionProcessor.class),
		@TagChild(MenuChildrenProcessor.class)
	})
	public static class MenutItemProcessor extends WidgetChildProcessor<MenuBarContext> {}
	
	@TagChildAttributes(tagName="separator")
	public static class MenutItemSeparatorProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			String widget = context.getWidget();
			out.println(widget+".addSeparator();");
		}
	}
	
	@TagChildren({
		@TagChild(TextCaptionProcessor.class),
		@TagChild(HtmlCaptionProcessor.class)
	})
	public static class CaptionProcessor extends ChoiceChildProcessor<MenuBarContext> {}

	@TagChildAttributes(tagName="textCaption")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true, supportsI18N=true)
	})
	public static class TextCaptionProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			context.caption = context.readChildProperty("text");
			context.isHtml = false;
		}
	}
	
	@TagChildAttributes(tagName="htmlCaption", type=HTMLTag.class)
	public static class HtmlCaptionProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			context.caption = ensureHtmlChild(context.getChildElement(), true);
			context.isHtml = true;
		}
	}

	@TagChildren({
		@TagChild(CommandProcessor.class),
		@TagChild(SubMenuProcessor.class)
	})
	public static class MenuChildrenProcessor extends ChoiceChildProcessor<MenuBarContext> {}
	
	@TagChildAttributes(tagName="command")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="onExecute", required=true)
	})
	public static class CommandProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out,  MenuBarContext context) throws CruxGeneratorException 
		{
			String executeEvt = context.readChildProperty("onExecute");
			if (executeEvt != null)
			{
				String event = ViewFactoryCreator.createVariableName("evt");
				String item = createMenuItem(out, context);
				
				out.println(item+".setCommand(new "+Command.class.getCanonicalName()+"(){);");
				out.println("public void execute(){");
				out.println("final Event "+event+" = Events.getEvent(\"onExecute\", "+ EscapeUtils.quote(executeEvt)+");");
				out.println("Events.callEvent("+event+", new "+ExecuteEvent.class.getCanonicalName()+"<"+MenuBar.class.getCanonicalName()+
						                             ">("+context.getWidget()+", "+context.getWidgetId()+"));");
				out.println("}");
				out.println("});");
			}
			context.clearAttributes();
		}
	}

	@TagChildAttributes(tagName="subMenu", type=MenuBarFactory.class)
	public static class SubMenuProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException 
		{
			String subMenu = getSubMenu(getWidgetCreator(), out, context);
			String item = createMenuItem(out, context);
			out.println(item+".setSubMenu("+subMenu+");");
		}
	}
	
	/**
	 * Creates a subMenu, based on inner span tags
	 * @param widgetCreator 
	 * @param element
	 * @return
	 * @throws CruxGeneratorException 
	 */
	protected static String getSubMenu(WidgetCreator<?> widgetCreator, SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
	{
		String widget = context.getWidget();
		String subMenu = widgetCreator.createChildWidget(out, context.getChildElement());	
		out.println(subMenu+".setAutoOpen("+widget+".getAutoOpen());");
		out.println(subMenu+".setAnimationEnabled("+widget+".isAnimationEnabled());");
		return subMenu;
	}
	
	/**
	 * @param context
	 * @return
	 */
	protected static String createMenuItem(SourcePrinter out, MenuBarContext context)
	{
		String widget = context.getWidget();
		String menuItemClass = MenuItem.class.getCanonicalName();
		String menuItem = ViewFactoryCreator.createVariableName("menuItem");
		out.println(menuItemClass +" "+ menuItem+"="+
				widget+".addItem(new "+menuItemClass+"("+EscapeUtils.quote(context.caption)+", "+context.isHtml+", (Command)null));");
		return menuItem;
	}

	@Override
    public MenuBarContext instantiateContext()
    {
	    return new MenuBarContext();
    }
}
