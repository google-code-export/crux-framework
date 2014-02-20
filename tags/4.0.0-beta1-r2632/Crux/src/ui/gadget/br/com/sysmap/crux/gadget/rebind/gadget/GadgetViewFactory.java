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
package br.com.sysmap.crux.gadget.rebind.gadget;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.gadget.client.widget.CruxGadgetView;
import br.com.sysmap.crux.gadget.client.widget.GadgetView;
import br.com.sysmap.crux.gadget.client.widget.GadgetView.View;
import br.com.sysmap.crux.gwt.rebind.AbstractHTMLPanelFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="gadgetView", library="gadget", targetWidget=GadgetView.class, htmlContainer=true)
@TagAttributes({
	@TagAttribute(value="view", type=View.class, required=true)
})
@TagChildren({
	@TagChild(value=GadgetViewFactory.ContentProcessor.class, autoProcess=false)
})
public class GadgetViewFactory extends AbstractHTMLPanelFactory
{
	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = CruxGadgetView.class.getCanonicalName();
		String id = context.readWidgetProperty("id");
        if(StringUtils.isEmpty(id))
        {
        	throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
        }
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(id)+");");
		createChildren(out, context);
//		out.println(CruxGadgetView.class.getCanonicalName()+".getGadget();");//initializes the gadget
	}
	
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	    super.processAttributes(out, context);
	}

	@Override
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=AnyTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
}