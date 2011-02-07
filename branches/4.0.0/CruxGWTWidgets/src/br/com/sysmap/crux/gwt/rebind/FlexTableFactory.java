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

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * Factory for FlexTable widget
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="flexTable", library="gwt", targetWidget=FlexTable.class)
@TagChildren({
	@TagChild(FlexTableFactory.GridRowProcessor.class)
})
public class FlexTableFactory extends HTMLTableFactory<HTMLTableFactoryContext>
{
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(GridCellProcessor.class)
	})
	public static class GridRowProcessor extends TableRowProcessor<HTMLTableFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, HTMLTableFactoryContext context) throws CruxGeneratorException
		{
			String widget = context.getWidget();
			out.println(widget+".insertRow("+widget+".getRowCount());");
			super.processChildren(out, context);
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="colSpan", type=Integer.class),
		@TagAttributeDeclaration(value="rowSpan", type=Integer.class)
	})
	@TagChildren({
		@TagChild(GridChildrenProcessor.class)
	})
	public static class GridCellProcessor extends TableCellProcessor<HTMLTableFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, HTMLTableFactoryContext context) throws CruxGeneratorException
		{
			String widget = context.getWidget();
			out.println(widget+".addCell("+context.rowIndex+");");
			
			super.processChildren(out, context);

			String colspan = context.readChildProperty("colSpan");
			if(colspan != null && colspan.length() > 0)
			{
				out.println(widget+".getFlexCellFormatter().setColSpan("+context.rowIndex+", "+context.colIndex+", "+Integer.parseInt(colspan)+");");
			}
			
			String rowSpan = context.readChildProperty("rowSpan");
			if(rowSpan != null && rowSpan.length() > 0)
			{
				out.println(widget+".getFlexCellFormatter().setRowSpan("+context.rowIndex+", "+context.colIndex+", "+Integer.parseInt(rowSpan)+");");
			}
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	@TagChildren({
		@TagChild(FlexCellTextProcessor.class),
		@TagChild(FlexCellHTMLProcessor.class),
		@TagChild(FlexCellWidgetProcessor.class)
	})
	public static class GridChildrenProcessor extends ChoiceChildProcessor<HTMLTableFactoryContext> {}
	
	public static class FlexCellTextProcessor extends CellTextProcessor<HTMLTableFactoryContext>{}
	public static class FlexCellHTMLProcessor extends CellHTMLProcessor<HTMLTableFactoryContext>{}

	@TagChildren({
		@TagChild(FlexWidgetProcessor.class)
	})	
	public static class FlexCellWidgetProcessor extends CellWidgetProcessor<HTMLTableFactoryContext> {}
	public static class FlexWidgetProcessor extends WidgetProcessor<HTMLTableFactoryContext>{}
	
	@Override
    public HTMLTableFactoryContext instantiateContext()
    {
	    return new HTMLTableFactoryContext();
    } 
}
