package org.myapp.authentication.data.io;

import java.io.File;

public abstract class TSVDataLoader<T> extends SeparatorDataLoader<T> {

	private static final String DATA_SEPARATOR = "\t";

	public TSVDataLoader(File dataFile, int columnSize) {
		super(dataFile, columnSize);
	}
	
	protected String getDataSeparator() {
		return DATA_SEPARATOR;
	}
}
