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

import com.google.gwt.user.client.ui.DeckPanel;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="deckPanel", library="gwt", targetWidget= DeckPanel.class)
@TagAttributes({
	@TagAttribute(value="visibleWidget", type=Integer.class, processor=DeckPanelFactory.VisibleWidgetAttributeParser.class)
})
@TagChildren({
	@TagChild(DeckPanelFactory.WidgetContentProcessor.class)
})
public class DeckPanelFactory extends ComplexPanelFactory<WidgetCreatorContext>
					implements HasAnimationFactory<WidgetCreatorContext>
{
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleWidgetAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String widget = context.getWidget();
			out.println(widget+".showWidget("+Integer.parseInt(attributeValue)+");");
        }
	}
	
	@Override
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}