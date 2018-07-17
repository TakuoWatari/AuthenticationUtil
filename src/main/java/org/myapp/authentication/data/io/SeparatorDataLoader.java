package org.myapp.authentication.data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.constant.CommonConst;

public abstract class SeparatorDataLoader<T> extends DataLoaderBase<T> {

	private static final String DAOUBLE_QUAT = "\"";

	private int columnSize;

	public SeparatorDataLoader(File dataFile, int columnSize) {
		super(dataFile);
		this.columnSize = columnSize;
	}

	protected List<T> load(BufferedReader reader) throws IOException {
		List<T> dataList = new ArrayList<>();

		String[] dataValues = new String[this.columnSize];
		int index = 0;
		boolean contiueFlg = false;

		StringBuilder dataBuf = null;

		for (String lineData = reader.readLine();
				lineData != null;
				lineData = reader.readLine()) {
			String[] separatedValues = lineData.split(
					this.getDataSeparator());

			for (int i = 0; i < separatedValues.length; i++) {
				if (index == this.columnSize) {
					throw new IOException("読み込みファイルの構成が不正です。");
				}
				String value = separatedValues[i];
				if (!contiueFlg) {
					dataBuf = new StringBuilder();
					dataBuf.append(value);
				} else {
					dataBuf.append(value);
				}

				if (!value.contains(DAOUBLE_QUAT)) {
					dataValues[index] = dataBuf.toString();
					index++;
					dataBuf = null;
					contiueFlg = false;
				} else {
					if (value.startsWith(DAOUBLE_QUAT)
							&& value.endsWith(DAOUBLE_QUAT)) {
						dataValues[index] = (dataBuf.substring(1, value.length() - 1)).replaceAll("\"\"", "\"");
						index++;
						dataBuf = null;
						contiueFlg = false;
					} else if (value.startsWith(DAOUBLE_QUAT)) {
						if (i + 1 == separatedValues.length) {
							dataBuf.append(CommonConst.LINE_SEPARATOR);
						} else {
							dataBuf.append(this.getDataSeparator());
						}
						contiueFlg = true;
					} else {
						dataValues[index] = dataBuf.toString();
						index++;
						dataBuf = null;
						contiueFlg = false;
					}
				}
			}

			if (!contiueFlg && index == this.columnSize) {
				T data = parseData(dataValues);
				dataList.add(data);
				index = 0;
			}
		}

		return dataList;
	}

	abstract protected String getDataSeparator();

	abstract protected T parseData(String[] separatedData);
}
