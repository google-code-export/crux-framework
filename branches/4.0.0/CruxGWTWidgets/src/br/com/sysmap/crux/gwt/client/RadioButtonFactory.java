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
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.user.client.ui.RadioButton;


/**
 * Represents a RadioButtonFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="radioButton", library="gwt")
public class RadioButtonFactory extends AbstractCheckBoxFactory<RadioButton>
{
	@Override
	public RadioButton instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new RadioButton(element.getProperty("name"));
	}

	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetFactoryContext<RadioButton> context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<RadioButton> {}
	
}
