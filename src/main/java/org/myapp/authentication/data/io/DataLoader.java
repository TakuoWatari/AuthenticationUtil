package org.myapp.authentication.data.io;

import java.util.List;

import application.exception.ApplicationException;

public interface DataLoader<T> {

	public List<T> getDataList() throws ApplicationException;

}
