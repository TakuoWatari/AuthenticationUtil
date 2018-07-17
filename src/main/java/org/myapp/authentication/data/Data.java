package org.myapp.authentication.data;

/**
 * <p>データ</p>
 * @author T.Watari
 */
public interface Data {

	/**
	 * データを一意に識別できるキー値
	 */
	public String getId();

	/**
	 * 無効化処理
	 * データを削除するまでに必要な処理があれば定義する
	 */
	public void disable();
}
