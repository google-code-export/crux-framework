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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.user.client.ui.HTML;


/**
 * Represents a HTMLFactory DeclarativeFactory
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="HTML", library="gwt")
public class HTMLFactory extends AbstractLabelFactory<HTML> implements HasHTMLFactory<HTML, WidgetCreatorContext>
{
	@Override
	public HTML instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new HTML();
	}
	
	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<HTML, WidgetCreatorContext> {}
}
