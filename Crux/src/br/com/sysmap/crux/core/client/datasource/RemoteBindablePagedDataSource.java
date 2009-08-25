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
package br.com.sysmap.crux.core.client.datasource;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class RemoteBindablePagedDataSource<T> extends AbstractRemotePagedDataSource<DataSourceRecord, T>
													   implements BindableDataSource<DataSourceRecord, T>
{
	@Override
	protected DataSourceRecord[] createDataObject(int count)
	{
		return new DataSourceRecord[count];
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.BindableDataSource#getBindedObject()
	 */
	public T getBindedObject()
	{
		return getBindedObject(getRecord());
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.BindableDataSource#getBindedObject(br.com.sysmap.crux.core.client.datasource.DataSourceRecord)
	 */
	public T getBindedObject(DataSourceRecord record)
	{
		return null;
	}		
}
