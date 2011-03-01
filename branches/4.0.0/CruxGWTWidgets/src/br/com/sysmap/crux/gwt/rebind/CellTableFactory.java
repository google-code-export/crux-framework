/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

class CellTableContext extends WidgetCreatorContext
{
	String rowDataObject;
	public String header;
	public String footer;
	public String colDataObject;
	public String column;
	public JClassType rowDataObjectType;
	public JType colDataObjectType;
	public String columnExpression;
	public String columnWidth;
	public String columnsortable;
	public String columnHorizontalAlignment;
	public String columnVerticalAlignment;
	public String columnFieldUpdaterFactoryMethod;
	
	void clearColumnInformation()
	{
		header = null;
		footer = null;
		colDataObject = null;
		colDataObjectType = null;
		columnExpression = null;
		column = null;
		columnWidth = null;
		columnsortable = null;
		columnHorizontalAlignment = null;
		columnVerticalAlignment = null;
		columnFieldUpdaterFactoryMethod = null;
	}
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="cellTable", library="gwt", targetWidget=CellTable.class)
@TagAttributes({
	@TagAttribute(value="tableLayoutFixed", type=Boolean.class) //TODO RowStyles??
})
@TagChildren({
	@TagChild(value=CellTableFactory.ColumnsProcessor.class)
})
public class CellTableFactory extends AbstractHasDataFactory<CellTableContext>
{
	@Override
	public void processAttributes(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
	{
	    super.processAttributes(out, context);
	    context.rowDataObject = getDataObject(context.getChildElement());
	    context.rowDataObjectType = getContext().getTypeOracle().findType(context.rowDataObject);
	}
	
	@TagConstraints(tagName="column", minOccurs="1", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="sortable", type=Boolean.class),
		@TagAttributeDeclaration(value="property", required=true),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class),
		@TagAttributeDeclaration("fieldUpdaterFactoryMethod"),
		@TagAttributeDeclaration("width")
	})
	@TagChildren({
		@TagChild(ColumnHeaderProcessor.class),
		@TagChild(ColumnCellProcessor.class),
		@TagChild(ColumnFooterProcessor.class)
	})
	public static class ColumnsProcessor extends WidgetChildProcessor<CellTableContext> implements HasPostProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String property = context.readChildProperty("property");
			StringBuilder getValueExpression = new StringBuilder();			
			try
            {
				context.colDataObjectType = ClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, 
						context.rowDataObject, property, "object", true);
				context.colDataObject = context.colDataObjectType.getParameterizedQualifiedSourceName();
				context.columnExpression = getValueExpression.toString();
				context.columnWidth = context.readChildProperty("width");
				context.columnsortable = context.readChildProperty("sortable");
				context.columnHorizontalAlignment = context.readChildProperty("horizontalAlignment");
				context.columnVerticalAlignment = context.readChildProperty("verticalAlignment");
				context.columnFieldUpdaterFactoryMethod = context.readChildProperty("fieldUpdaterFactoryMethod");
            }
            catch (NoSuchFieldException e)
            {
            	throw new CruxGeneratorException();//TODO message
            }
		}
		
		public void postProcessChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
        {
		    if (!StringUtils.isEmpty(context.columnWidth))
		    {
		    	out.println(context.getWidget()+".setColumnWidth("+context.column+", "+EscapeUtils.quote(context.columnWidth)+");");
		    }
		    if (!StringUtils.isEmpty(context.columnsortable))
		    {
		    	out.println(context.column+".setSortable("+Boolean.parseBoolean(context.columnsortable)+");");
		    }
			if (!StringUtils.isEmpty(context.columnHorizontalAlignment))
			{
				out.println(context.column+".setHorizontalAlignment("+
					  AlignmentAttributeParser.getHorizontalAlignment(context.columnHorizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			if (!StringUtils.isEmpty(context.columnVerticalAlignment))
			{
				out.println(context.column+".setVerticalAlignment("+AlignmentAttributeParser.getVerticalAlignment(context.columnVerticalAlignment)+");");
			}
			if (!StringUtils.isEmpty(context.columnFieldUpdaterFactoryMethod))
			{
				String updater = getWidgetCreator().createVariableName("updater");
				
				out.print(FieldUpdater.class.getCanonicalName()+"<"+context.rowDataObject+","+
						context.colDataObject+"> "+ updater + " = ("+FieldUpdater.class.getCanonicalName()+"<"+context.rowDataObject+","+
						context.colDataObject+">)");
				
				EvtProcessor.printEvtCall(out, context.columnFieldUpdaterFactoryMethod, "loadFieldUpdater", (String)null, null, getWidgetCreator());
				out.println(context.column+".setFieldUpdater("+updater+");");
			}
			
			
		    if (context.header != null)
			{
				if (context.footer != null)
				{
					out.println(context.getWidget()+".addColumn("+context.column+","+context.header+","+context.footer+");");
				}
				else 
				{
					out.println(context.getWidget()+".addColumn("+context.column+","+context.header+",("+Header.class.getCanonicalName()+")null);");
				}
			}
			else
			{
				if (context.footer != null)
				{
					out.println(context.getWidget()+".addColumn("+context.column+",("+Header.class.getCanonicalName()+")null,"+context.footer+");");
				}
				else
				{
					out.println(context.getWidget()+".addColumn("+context.column+");");
				}
			}
			
			context.clearColumnInformation();
        }
	}
	
	@TagConstraints(tagName="header", minOccurs="0")
	@TagChildren({
		@TagChild(ColumnHeaderChoiceProcessor.class)
	})
	public static class ColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext> {}

	@TagChildren({
		@TagChild(TextColumnHeaderProcessor.class),
		@TagChild(HTMLColumnHeaderProcessor.class),
		@TagChild(CustomColumnHeaderProcessor.class)
	})
	public static class ColumnHeaderChoiceProcessor extends ChoiceChildProcessor<CellTableContext> {}
	
	@TagConstraints(tagName="text", type=String.class)
	public static class TextColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = ensureTextChild(context.getChildElement(),true);
			context.header = "new "+TextHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}

	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = ensureTextChild(context.getChildElement(),true);
			context.header = "new "+SafeHtmlHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}
	
	@TagConstraints(tagName="custom")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="factoryMethod", required=true)
	})
	public static class CustomColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String factoryMethod = context.getChildElement().optString("factoryMethod");
		    assert (!StringUtils.isEmpty(factoryMethod));
			String header = getWidgetCreator().createVariableName("header");
			out.print(Header.class.getCanonicalName()+"<"+context.colDataObject+">"+" "+header+
					"=("+Header.class.getCanonicalName()+"<"+context.colDataObject+">)");
		    EvtProcessor.printEvtCall(out, factoryMethod, "loadHeader", (String)null, null, getWidgetCreator());
		    context.header = header;
		}
	}

	@TagConstraints(tagName="footer", minOccurs="0", maxOccurs="1")
	@TagChildren({
		@TagChild(ColumnFooterChoiceProcessor.class)
	})
	public static class ColumnFooterProcessor extends WidgetChildProcessor<CellTableContext> {}	

	@TagChildren({
		@TagChild(TextColumnFooterProcessor.class),
		@TagChild(HTMLColumnFooterProcessor.class),
		@TagChild(CustomColumnFooterProcessor.class)
	})
	public static class ColumnFooterChoiceProcessor extends ChoiceChildProcessor<CellTableContext> {}
	
	@TagConstraints(tagName="text", type=String.class)
	public static class TextColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = ensureTextChild(context.getChildElement(),true);
			context.footer = "new "+TextHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}

	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = ensureTextChild(context.getChildElement(),true);
			context.footer = "new "+SafeHtmlHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}		
	}

	@TagConstraints(tagName="custom")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="factoryMethod", required=true)
	})
	public static class CustomColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String factoryMethod = context.getChildElement().optString("factoryMethod");
		    assert (!StringUtils.isEmpty(factoryMethod));
			String footer = getWidgetCreator().createVariableName("footer");
			out.print(Header.class.getCanonicalName()+"<"+context.colDataObject+">"+" "+footer+
					"=("+Header.class.getCanonicalName()+"<"+context.colDataObject+">)");
		    EvtProcessor.printEvtCall(out, factoryMethod, "loadFooter", (String)null, null, getWidgetCreator());
		    context.footer = footer;
		}
	}
	
	@TagConstraints(tagName="cell")
	@TagChildren({
		@TagChild(value=CellListChildProcessor.class, autoProcess=false)
	})
	public static class ColumnCellProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String cell = ((CellTableFactory)getWidgetCreator()).getCell(out, context.getChildElement());
			String column = getWidgetCreator().createVariableName("column");
			out.println(Column.class.getCanonicalName()+"<"+context.rowDataObject+","+context.colDataObject+">"+" "+column+
					"=new "+Column.class.getCanonicalName()+"<"+context.rowDataObject+","+context.colDataObject+">("+cell+"){");
			out.println("public "+context.colDataObject+" getValue("+context.rowDataObject+" object){");
			out.println("return "+context.columnExpression);
			out.println("}");
			out.println("};");
			context.column = column;
		}
	}

	@Override
    public CellTableContext instantiateContext()
    {
	    return new CellTableContext();
    }
}

