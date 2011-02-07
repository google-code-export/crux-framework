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

import com.google.gwt.user.client.ui.HorizontalPanel;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasVerticalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="horizontalPanel", library="gwt", targetWidget=HorizontalPanel.class)
@TagChildren({
	@TagChild(HorizontalPanelFactory.HorizontalPanelProcessor.class)
})		
public class HorizontalPanelFactory extends CellPanelFactory<CellPanelContext>
	   implements HasHorizontalAlignmentFactory<CellPanelContext>, 
	   			  HasVerticalAlignmentFactory<CellPanelContext>
{
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(HorizontalProcessor.class),
		@TagChild(HorizontalWidgetProcessor.class)
	})		
	public static class  HorizontalPanelProcessor extends AbstractCellPanelProcessor<CellPanelContext> {}
	
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(value=HorizontalWidgetProcessor.class)
	})		
	public static class HorizontalProcessor extends AbstractCellProcessor<CellPanelContext> {}
		
	public static class HorizontalWidgetProcessor extends AbstractCellWidgetProcessor<CellPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement());
			String rootWidget = context.getWidget();
			out.println(rootWidget+".add("+child+");");
			context.child = child;
			super.processChildren(out, context);
			context.child = null;
		}
	}

	@Override
    public CellPanelContext instantiateContext()
    {
	    return new CellPanelContext();
    }	
}
