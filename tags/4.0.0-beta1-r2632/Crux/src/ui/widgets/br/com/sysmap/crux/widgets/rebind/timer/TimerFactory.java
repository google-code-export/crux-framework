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
package br.com.sysmap.crux.widgets.rebind.timer;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.widgets.client.timer.Timer;
import br.com.sysmap.crux.widgets.rebind.event.TimeoutEvtBind;

/**
 * Factory for Timer widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="timer", library="widgets", targetWidget=Timer.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="start", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="initial", type=Integer.class, defaultValue="0"),
	@TagAttributeDeclaration(value="regressive", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="pattern", required=true)
})
@TagChildren({
	@TagChild(TimerFactory.TimerChildrenProcessor.class)
})
public class TimerFactory extends WidgetCreator<WidgetCreatorContext>
{
	/**
	 * @see br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();

		long initial = 0;
		boolean regressive = false;
		boolean start = false;
		
		String strInitial = context.readWidgetProperty("initial");  
		if(strInitial != null && strInitial.trim().length() > 0)
		{
			initial = Long.parseLong(strInitial);
		}
		
		String strRegressive = context.readWidgetProperty("regressive");  
		if(strRegressive != null && strRegressive.trim().length() > 0)
		{
			regressive = Boolean.parseBoolean(strRegressive);
		}
		
		String strStart = context.readWidgetProperty("start");  
		if(strStart != null && strStart.trim().length() > 0)
		{
			start = Boolean.parseBoolean(strStart);
		}

		String pattern = context.readWidgetProperty("pattern");  
		
		out.println(className + " " + context.getWidget()+" = new "+className+"("+initial+", "+regressive+", "+EscapeUtils.quote(pattern)+", "+start+");");
	}
	
	@TagConstraints(tagName="onTimeout", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="time", required=true, type=Integer.class),
		@TagAttributeDeclaration(value="execute", required=true)
	})
	public static class TimerChildrenProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			TimeoutEvtBind.processEvent(out, context.readChildProperty("execute"), context.readChildProperty("time"), 
								rootWidget, context.getWidgetId(), getWidgetCreator());
		}
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}