package org.myapp.authentication.data;

import java.io.File;

import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.constant.Authority;
import org.myapp.authentication.util.UserUtil;

import application.exception.ApplicationException;
import application.model.Data;
import application.util.ApplicationUtil;
import application.util.FileUtil;

/**
 * <p>ユーザークラス</p>
 * @author T.Watari
 */
public class User implements Data {

	/** ユーザーID **/
	private String id;
	/** ユーザー名 **/
	private String name;
	/** パスワード **/
	private String password;
	/** 権限 **/
	private Authority authority;

	/**
	 * コンストラクタ
	 * <pre>他のパッケージでインスタンスを生成できない、インスタンスの生成は、UserManagerを用いて生成すること</pre>
	 * @param id ユーザーID
	 * @param name 名前
	 * @param password パスワード
	 * @param authority 権限
	 */
	User(String id, String name, String password, Authority authority){
		this.id = id;
		this.name = name;
		this.password = password;
		this.authority = authority;
	}

	/**
	 * ユーザー専用データ保存ディレクトリを取得する
	 * @return ファイルオブジェクト
	 */
	public File getDataDir() {
		File dataDir = new File(ApplicationUtil.getDataDir(), this.id);
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		return dataDir;
	}

	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * 名前を取得する
	 * @return 名前
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 名前を設定する
	 * @param name 名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 権限を取得する
	 * @return 権限
	 */
	public Authority getAuthority() {
		return this.authority;
	}

	/**
	 * 管理者であるか判定する
	 * @return 判定結果(true：管理者, false：管理者でない)
	 */
	public boolean isAdmin() {
		boolean result = false;
		if (this.authority.equals(Authority.ADMIN)) {
			result = true;
		}
		return result;
	}

	/**
	 * パスワードを取得する
	 * @return 権限
	 */
	String getPassword() {
		return this.password;
	}

	/**
	 * パスワードをリセットする
	 */
	void resetPassword(String password) {
		this.password = password;
	}

	/**
	 * 認証の可否を行う
	 * @param password パスワード
	 * @return 認証結果(true：認証成功, false：認証失敗)
	 */
	public boolean canAuthenticate(String password) {
		boolean result = false;
		if (password != null
				&& password.equals(this.password)) {
			result = true;
		}
		return result;
	}

	/**
	 * <p>ユーザー情報更新(名前)</p>
	 * @param name 名前
	 * @throws ApplicationException パスワード変更に失敗した場合にスロー
	 */
	public void changeName(String name)
	throws ApplicationException {
		UserUtil.checkUserName(name);
		this.name = name;
	}

	/**
	 * <p>ユーザー情報更新(パスワード)</p>
	 * @param password 現パスワード
	 * @param newPassword 新パスワード
	 * @param newPasswordAgain 新パスワード(確認用)
	 * @throws ApplicationException パスワード変更に失敗した場合にスロー
	 */
	public void changePassword(String password, String newPassword, String newPasswordAgain)
	throws ApplicationException {
		if (!canAuthenticate(password)) {
			throw new ApplicationException(AuthenticationMessage.W0016);
		}
		UserUtil.checkUserNewPassword(password, newPassword, newPasswordAgain);
		this.password = newPassword;
	}

	@Override
	public String toString() {
		return this.id + ":" + this.name;
	}

	@Override
	public void disable() {
		File userDir = this.getDataDir();
		FileUtil.deleteFile(userDir);
	}
}
