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
package br.com.sysmap.crux.tools.htmltags.template;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.tools.htmltags.HTMLTagsMessages;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Templates 
{
	private static Map<String, Document> templates = null;
	private static Map<String, Set<String>> registeredLibraries = null;
	private static HTMLTagsMessages messages = MessagesFactory.getMessages(HTMLTagsMessages.class);
	private static final Log logger = LogFactory.getLog(Templates.class);
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	public static void initialize()
	{
		if (templates != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (templates != null)
			{
				return;
			}
			
			initializeTemplates();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	protected static void initializeTemplates()
	{
		templates = new HashMap<String, Document>();
		registeredLibraries = new HashMap<String, Set<String>>();
		logger.info(messages.templatesScannerSearchingTemplateFiles());
		TemplatesScanner.getInstance().scanArchives();
	}
	
	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static Document getTemplate(String library, String id)
	{
		return getTemplate(library, id, false);
	}

	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static Document getTemplate(String library, String id, boolean clone)
	{
		if (templates == null)
		{
			initialize();
		}
		Document document = templates.get(library+"_"+id);
		if (document != null)
		{
			document = (Document) document.cloneNode(true);
		}
		return document;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Set<String> getRegisteredLibraries()
	{
		if (registeredLibraries == null)
		{
			initialize();
		}
		
		return registeredLibraries.keySet();
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public static Set<String> getRegisteredLibraryTemplates(String library)
	{
		if (registeredLibraries == null)
		{
			initialize();
		}
		
		return registeredLibraries.get(library);
	}

	/**
	 * 
	 * @param templateId
	 * @param template
	 */
	static void registerTemplate(String templateId, Document template)
	{
		Element templateElement = template.getDocumentElement();
		String library = templateElement.getAttribute("library");
		if (!registeredLibraries.containsKey(library))
		{
			registeredLibraries.put(library, new HashSet<String>());
		}
		registeredLibraries.get(library).add(templateId);
		
		templates.put(library+"_"+templateId, template);
	}
}
