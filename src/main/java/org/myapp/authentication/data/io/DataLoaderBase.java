package org.myapp.authentication.data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import application.exception.ApplicationException;

abstract class DataLoaderBase<T> implements DataLoader<T> {

	private File dataFile;

	public DataLoaderBase(File dataFile) {
		this.dataFile = dataFile;
	}

	public List<T> getDataList() throws ApplicationException {
		List<T> dataList;

		try (BufferedReader reader = new BufferedReader(
				new FileReader(this.dataFile))) {

			dataList = load(reader);

		} catch (IOException e) {
			throw new ApplicationException("");
		}

		return dataList;
	}

	abstract protected List<T> load(BufferedReader reader) throws IOException;
}
