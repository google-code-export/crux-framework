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
package br.com.sysmap.crux.core.client.datasource.local;

import br.com.sysmap.crux.core.client.datasource.EditableDataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.Metadata;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class EditableLocalScrollableDataSource extends AbstractLocalScrollableDataSource<EditableDataSourceRecord> implements EditableDataSource
{
	public EditableLocalScrollableDataSource(Metadata metadata)
	{
		super(metadata);
	}
}
