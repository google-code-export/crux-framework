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
package org.cruxframework.crux.core.rebind.ioc;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.ioc.Inject.Scope;
import org.cruxframework.crux.core.client.ioc.IocProvider;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.ioc.IoCException;
import org.cruxframework.crux.core.ioc.IocConfigImpl;
import org.cruxframework.crux.core.ioc.IocContainerManager;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainerRebind extends AbstractProxyCreator
{ 
	public IocContainerRebind(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context);
    }

	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private static IocLocalScope _localScope = new IocLocalScope();");
		srcWriter.println("private static IocDocumentScope _documentScope = new IocDocumentScope();");
		srcWriter.println("private static IocViewScope _viewScope = new IocViewScope();");
    }

	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		Iterator<String> classes = IocContainerManager.iterateClasses();
		while (classes.hasNext())
		{
			String className = classes.next();
			generateContainerInstatiationMethod(srcWriter, className);
		}
		generateGetScopeMethod(srcWriter);
    }

	/**
	 * 
	 * @param srcWriter
	 * @param className
	 */
	private void generateContainerInstatiationMethod(SourcePrinter srcWriter, String className)
	{
		srcWriter.println("public static "+className+" get"+className.replace('.', '_')+"("+Scope.class.getCanonicalName()+" scope, String subscope){");

		IocConfigImpl<?> iocConfig = IocContainerManager.getConfigurationForType(className);
		Class<?> providerClass = iocConfig.getProviderClass();
		if (providerClass != null)
		{
			srcWriter.println(className+" result = _getScope(scope).getValue(GWT.create("+providerClass.getCanonicalName()+".class), "+EscapeUtils.quote(className)+", subscope, ");
			generateFieldsPopulationCallback(srcWriter, className);
			srcWriter.println(");");
		}
		else if (iocConfig.getToClass() != null)
		{
			srcWriter.println(className+" result = _getScope(scope).getValue(new "+IocProvider.class.getCanonicalName()+"<"+className+">(){");
			srcWriter.println("public "+className+" get(){");
			srcWriter.println("return GWT.create("+iocConfig.getToClass().getCanonicalName()+".class);");
			srcWriter.println("}");
			srcWriter.println("}, "+EscapeUtils.quote(className)+", subscope, ");
			generateFieldsPopulationCallback(srcWriter, className);
			srcWriter.println(");");
		}
		else
		{
			srcWriter.println(className+" result = _getScope(scope).getValue(new "+IocProvider.class.getCanonicalName()+"<"+className+">(){");
			srcWriter.println("public "+className+" get(){");
			srcWriter.println("return GWT.create("+className+".class);");
			srcWriter.println("}");
			srcWriter.println("}, "+EscapeUtils.quote(className)+", subscope, ");
			generateFieldsPopulationCallback(srcWriter, className);
			srcWriter.println(");");
		}

		srcWriter.println("return result;");
		
		srcWriter.println("}");
    }

	/**
	 * 
	 * @param srcWriter
	 * @param className
	 */
	private void generateFieldsPopulationCallback(SourcePrinter srcWriter, String className) 
    {
		try
		{
			srcWriter.println("new IocScope.CreateCallback<"+className+">(){");
			srcWriter.println("public void onCreate("+className+" newObject){");
			injectFields(srcWriter, context.getTypeOracle().getType(className), "newObject", new HashSet<String>());
			srcWriter.println("}");
			srcWriter.println("}");
		}
		catch (NotFoundException e)
		{
			throw new IoCException("IoC Error Class ["+className+"] not found.");
		}
    }
	
	/**
	 * 
	 * @param srcWriter
	 * @param type
	 * @param parentVariable
	 * @param added
	 */
	private void injectFields(SourcePrinter srcWriter, JClassType type, String parentVariable, Set<String> added)
	{
        for (JField field : type.getFields()) 
        {
        	String fieldName = field.getName();
			if (!added.contains(fieldName))
        	{
				JType fieldType = field.getType();
				if ((fieldType.isPrimitive()== null))
				{
					String injectionExpression = getFieldInjectionExpression(field);
					if (injectionExpression != null)
					{
						if (JClassUtils.isPropertyVisibleToWrite(type, field))
						{
							if (JClassUtils.hasSetMethod(field, type))
							{
								srcWriter.println(fieldType.getQualifiedSourceName()+" field_"+fieldName+" = "+ injectionExpression+";");
								String setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
								srcWriter.println(parentVariable+"."+setterMethodName+"(field_"+ fieldName+");");
							}
							else
							{
								srcWriter.println(parentVariable+"."+fieldName+" = "+ injectionExpression+";");
							}
						}
						else
						{
							throw new IoCException("IoC Error Field ["+field.getName()+"] from class ["+type.getQualifiedSourceName()+"] is not a writeable property.");
						}
					}
				}
        	}
        }
        if (type.getSuperclass() != null)
        {
        	injectFields(srcWriter, type.getSuperclass(), parentVariable, added);
        }
	}
	
	/**
	 * 
	 * @param srcWriter
	 */
	private void generateGetScopeMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public static IocScope _getScope("+Scope.class.getCanonicalName()+" scope){");
		srcWriter.println("switch (scope){");
		srcWriter.println("case LOCAL: return _localScope;");
		srcWriter.println("case DOCUMENT: return _documentScope;");
		srcWriter.println("case VIEW: return _viewScope;");
		srcWriter.println("default: return _localScope;");
		srcWriter.println("}");
		srcWriter.println("}");
	}

	@Override
    public String getProxyQualifiedName()
    {
	    return IocProvider.class.getPackage().getName()+"."+getProxySimpleName();
    }

	@Override
    public String getProxySimpleName()
    {
	    return "IocContainerImpl";
    }

	@Override
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = IocProvider.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
    }
	
	/**
	 * @return
	 */
    protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName()
		};
	    return imports;
    }

	/**
	 * 
	 * @param srcWriter
	 * @param type
	 */
	public static void injectProxyFields(SourcePrinter srcWriter, JClassType type)
	{
		injectProxyFields(srcWriter, type, new HashSet<String>());
	}
	
	private static void injectProxyFields(SourcePrinter srcWriter, JClassType type, Set<String> added)
	{
        for (JField field : type.getFields()) 
        {
        	String fieldName = field.getName();
			if (!added.contains(fieldName))
        	{
				added.add(fieldName);
				String injectionExpression = getFieldInjectionExpression(field);
				if (injectionExpression != null)
				{
					srcWriter.println("this."+fieldName+" = "+ injectionExpression+";");
				}
        	}
        }
        if (type.getSuperclass() != null)
        {
        	injectProxyFields(srcWriter, type.getSuperclass(), added);
        }
	}

	private static String getFieldInjectionExpression(JField field)
    {
		Inject inject = field.getAnnotation(Inject.class);
		if (inject != null)
		{
			JType fieldType = field.getType();
			if (!field.isStatic())
			{
				if (fieldType.isClassOrInterface() != null)
				{
					String fieldTypeName = fieldType.getQualifiedSourceName();
					IocConfigImpl<?> iocConfig = IocContainerManager.getConfigurationForType(fieldTypeName);
					if (iocConfig != null)
					{
						String iocContainerClassName = IocProvider.class.getPackage().getName()+"."+"IocContainerImpl";
						return iocContainerClassName+".get"+fieldTypeName.replace('.', '_')+
						"("+Scope.class.getCanonicalName()+"."+inject.scope().name()+", "+EscapeUtils.quote(inject.subscope())+")";
					}
					else
					{
						return "GWT.create("+fieldTypeName+".class)";
					}
				}
				else
				{
					throw new IoCException("Error injecting field ["+field.getName()+"] from type ["+field.getEnclosingType().getQualifiedSourceName()+"]. Primitive fields can not be handled by ioc container.");
				}
			}
			else
			{
				throw new IoCException("Error injecting field ["+field.getName()+"] from type ["+field.getEnclosingType().getQualifiedSourceName()+"]. Static fields can not be handled by ioc container.");
			}
		}
	    return null;
    }	
}
