/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.module.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.module.CruxModuleBridge;


/**
 * Used to locate the resources inside the modules structure. Used only for development. When this filter is used, you need 
 * to remove CruxFilter and DeclarativeUIFilter from your web.xml
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ModulesDeclarativeUIFilter extends ModulesFilter
{
	private static final Log log = LogFactory.getLog(ModulesDeclarativeUIFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException, ServletException 
	{
		if (production)
		{
			chain.doFilter(req, resp);
		}
		else
		{
			String requestedScreen = getRequestedScreen(req);
			if (requestedScreen != null)
			{
				int index = requestedScreen.indexOf("/");
				String currentModule = requestedScreen.substring(0, index);
				requestedScreen = requestedScreen.substring(index+1);
				CruxModuleBridge.getInstance().registerCurrentModule(currentModule);
				CruxBridge.getInstance().registerLastPageRequested(requestedScreen);
				try
				{
					String screenId = requestedScreen.replace(".html", ".crux.xml");

					InputStream screenResource = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(screenId);
					if (screenResource != null)
					{
						StreamUtils.write(screenResource, resp.getOutputStream(), false);
						return;
					}
					else
					{
						log.info("The page ["+requestedScreen+"] is not transformed... Accessing directly.");
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw new ServletException(e.getMessage(),e);
				}
			}
			chain.doFilter(req, resp);
		}
	}
}
