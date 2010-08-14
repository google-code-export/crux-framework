package br.com.sysmap.crux.showcase.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalPagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.SimpleGridServiceAsync;

@DataSource("simpleGridDataSource")
@DataSourceRecordIdentifier("name")
public class SimpleGridDataSource extends LocalPagedDataSource<Contact> {
	
	@Create
	protected SimpleGridServiceAsync service;
	
	public void load()
	{
		service.getContactList(new DataSourceAsyncCallbackAdapter<Contact>(this));
	}		
}

