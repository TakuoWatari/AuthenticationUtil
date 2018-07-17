package org.myapp.authentication.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.io.DataLoader;

import application.constant.Message;
import application.exception.ApplicationException;
import application.util.ApplicationUtil;
import application.util.CommonUtil;

public abstract class DataBase<T extends Data> {

	/** データ保存用ファイル **/
	private File saveFile;

	private Map<String, T> dataMap = new TreeMap<String, T>();

	/**
	 * コンストラクタ(他パッケージでインスタンス生成不可)
	 * @param saveFile データ保存用ファイル
	 */
	protected DataBase(File saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * データ保存ファイル名を取得する
	 * @return データ保存ファイル名
	 */
	protected final String getSaveFileName() {
		return this.saveFile.getName();
	}

	/**
	 * データインポート
	 * @param loader データ読み込みクラス
	 */
	public final void importData(DataLoader<T> loader) throws ApplicationException {
		List<T> dataList = loader.getDataList();
		for (T data : dataList) {
			dataMap.put(data.getId(), data);
		}
		this.commit();
	}

	/**
	 * データ保存ファイルを取得する
	 * @return データ保存ファイル
	 */
	protected final File getSaveFile() {
		return this.saveFile;
	}

	/**
	 * データ保存ファイルの内容を読み込みます
	 * @param
	 */
	protected void load() {

		// データファイルの読み込み処理を実装
		if (saveFile != null && saveFile.exists()) {
			if (!saveFile.isFile()) {
				// データファイルがファイルでない
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]が存在しません。");
			}
			if (!saveFile.canRead()) {
				// データファイルに読み込み権限がない
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]を読み込めません。");
			}

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(saveFile), ApplicationUtil.getDataCharCode()))) {

				for (String data = reader.readLine();
						data != null;
						data = reader.readLine()) {

					T dataObj = this.parse(data);
					this.dataMap.put(dataObj.getId(), dataObj);
				}

			} catch (IOException e) {
				// 読み込みに失敗
				throw new IllegalStateException(
						Message.getMessage(AuthenticationMessage.E9999,
								"・データファイル[" + saveFile.getName() + "]の読み込み中にエラーが発生しました。"),
								e);
			} catch (ApplicationException e) {
				// 読み込みに失敗
				throw new IllegalStateException(
						Message.getMessage(AuthenticationMessage.E9999,
								"・データファイル[" + saveFile.getName() + "]の読み込み中にエラーが発生しました。"),
								e);
			}
		}
	}

	/**
	 * データ保存ファイルから読み込んだデータ情報をデータオブジェクトに変換する。
	 * ※子クラスで実装が必要
	 * @param data データ情報文字列
	 * @return データオブジェクト
	 */
	protected abstract T parse(String data) throws ApplicationException;

	/**
	 * データリストをデータ保存ファイルに書き込む。
	 * @param data データ情報
	 * @return データオブジェクト
	 */
	protected final void save() {
		if (saveFile.exists()) {
			if (!saveFile.isFile()) {
				// データファイルがファイルでない
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]が存在しません。");
			}
			if (!saveFile.canWrite()) {
				// データファイルに対して書込み権限が無い
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]に書き込めません。");
			}
		} else {
			File parentDir = saveFile.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
		}

		if (this.getDataCount() > 0) {
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(saveFile, false), ApplicationUtil.getDataCharCode()))) {
				Collection<T> dataList = this.dataMap.values();
				for (T data : dataList) {
					// 情報を保存
					String formatedData = format(data);
					if (!CommonUtil.isEmpty(formatedData)) {
						writer.write(format(data));
					}
				}
			} catch (IOException e) {
				// 書込みに失敗
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]へ書込み中にエラーが発生しました。");
			} catch (ApplicationException e) {
				// データ変換にに失敗
				throw new IllegalStateException("データファイル[" + saveFile.getName() + "]へ書込み中にエラーが発生しました。", e);
			}
		} else {
			saveFile.delete();
		}
	}

	/**
	 * データオブジェクトを保存用ファイルに書き込む文字列に変換する。
	 * ※子クラスで実装が必要
	 * @param data データオブジェクト
	 * @return データ情報文字列
	 */
	protected abstract String format(T data) throws ApplicationException;

	protected void insert(T data) {
		this.dataMap.put(data.getId(), data);
	}

	protected boolean contains(T data) {
		return this.dataMap.containsKey(data.getId());
	}

	protected boolean contains(String id) {
		return this.dataMap.containsKey(id);
	}

	protected T getData(String id) {
		return this.dataMap.get(id);
	}

	protected void delete(String id) {
		if (this.contains(id)) {
			T data = this.dataMap.get(id);
			data.disable();
			this.dataMap.remove(id);
		}
	}

	protected void delete(T data) {
		if (this.contains(data)) {
			data.disable();
			this.dataMap.remove(data.getId());
		}
	}

	public void deleteAll() {
		Collection<T> dataList = this.dataMap.values();
		for (T data : dataList) {
			data.disable();
		}
		this.dataMap.clear();
	}

	protected int getDataCount() {
		return this.dataMap.size();
	}

	protected Collection<T> getDataValues() {
		return this.dataMap.values();
	}

	/**
	 * コミットされた時の挙動を定義
	 * ※子クラスで実装が必要
	 */
	public abstract void commit() throws ApplicationException;
}
