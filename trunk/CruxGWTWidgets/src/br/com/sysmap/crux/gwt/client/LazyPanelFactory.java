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
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.HasWidgetsHandler;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.LazyFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which content is only rendered when it becomes visible for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="lazyPanel", library="gwt", lazy=true)
public class LazyPanelFactory extends PanelFactory<LazyPanel> implements LazyFactory<LazyPanel>, HasWidgetsFactory<LazyPanel>
{
	protected com.google.gwt.user.client.ui.LazyPanel lazyPanelWidget;
	
	/**
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	@Override
	public LazyPanel instantiateWidget(final Element element, String widgetId) 
	{
		class LazyPanelImpl extends LazyPanel implements br.com.sysmap.crux.core.client.screen.LazyPanel{
			private String innerHTML = element.getInnerHTML();
			
			@Override
			protected Widget createWidget() 
			{
				if (isScreenParsing())
				{
					createWidgetAsync();
					return null;
				}
				else
				{
					doCreateWidget();
					return getWidget();
				}
			}

			private void createWidgetAsync()
			{
				Scheduler.get().scheduleDeferred(new ScheduledCommand()
				{
					public void execute()
					{
						doCreateWidget();
					}
				});
			}			

			private void doCreateWidget()
			{
				getElement().setInnerHTML(innerHTML);
				innerHTML = null;
				parseDocument();
			}
		}
		LazyPanel result =  new LazyPanelImpl();
		element.setInnerHTML("");
		HasWidgetsHandler.handleWidgetElement(result, widgetId, "gwt_lazyPanel");
		return result;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(LazyPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		parent.add(child);
	}
}
