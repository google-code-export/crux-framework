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
package br.com.sysmap.crux.core.server.dispatch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public class ControllerFactoryInitializer 
{
	private static final Log logger = LogFactory.getLog(ControllerFactoryInitializer.class);
	private static ControllerFactory controllerFactory;
	private static final Lock lock = new ReentrantLock();
	private static final Lock initializeLock = new ReentrantLock();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static boolean factoryInitialized = false;
	
	/**
	 * 
	 * @return
	 */
	public static ControllerFactory getControllerFactory()
	{
		if (controllerFactory != null) return controllerFactory;
		
		try
		{
			lock.lock();
			if (controllerFactory != null) return controllerFactory;
			controllerFactory = (ControllerFactory) Class.forName(ConfigurationFactory.getConfigurations().controllerFactory()).newInstance(); 
		}
		catch (Throwable e)
		{
			logger.error(messages.controllerFactoryInitializerError(e.getMessage()), e);
		}
		finally
		{
			lock.unlock();
		}
		return controllerFactory;
	}

	/**
	 * 
	 * @param controllerFactory
	 */
	public static void registerControllerFactory(ControllerFactory controllerFactory)
	{
		ControllerFactoryInitializer.controllerFactory = controllerFactory;
		factoryInitialized = false;
	}
	
	/**
	 * 
	 * @param context
	 */
	public static void initialize(ServletContext context)
	{
		if (!factoryInitialized)
		{
			initializeLock.lock();
			try
			{
				if (!factoryInitialized)
				{
					getControllerFactory().initialize(context);
					if (logger.isInfoEnabled())
					{
						logger.info(messages.controllerFactoryInitializerControllersRegistered());
					}
					factoryInitialized = true;
				}
			}
			finally
			{
				initializeLock.unlock();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isFactoryInitialized()
	{
		return factoryInitialized;
	}
}
