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
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.DecoratorPanel;


/**
 * Represents a DecoratorPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="decoratorPanel", library="gwt", targetWidget= DecoratorPanel.class)
public class DecoratorPanelFactory extends PanelFactory<WidgetCreatorContext>
{
    @Override
    @TagChildren({
            @TagChild(WidgetContentProcessor.class)
    })
    public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
    {
    }
    
    @Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
    
    @TagChildAttributes(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
}
