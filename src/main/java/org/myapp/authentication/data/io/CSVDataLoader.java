package org.myapp.authentication.data.io;

import java.io.File;

public abstract class CSVDataLoader<T> extends SeparatorDataLoader<T> {

	private static final String DATA_SEPARATOR = ",";

	public CSVDataLoader(File dataFile, int columnSize) {
		super(dataFile, columnSize);
	}

	protected String getDataSeparator() {
		return DATA_SEPARATOR;
	}
}
