package org.myapp.authentication.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.constant.Authority;
import org.myapp.authentication.util.UserUtil;

import application.constant.CommonConst;
import application.constant.Message;
import application.exception.ApplicationException;
import application.util.ApplicationUtil;
import application.util.CipherManager;

public class UserManager extends DataBase<User> {

	/** インスタンス **/
	private static UserManager instance;

	/** 暗号化・復号化部品 **/
	private static CipherManager cipherMng;

	/**
	 * インスタンス取得メソッド
	 * @return UserManager
	 */
	public static final synchronized UserManager getInstance() {
		if (cipherMng == null) {
			cipherMng = new CipherManager("UserData");
		}
		if (instance == null) {
			File saveFile = new File(ApplicationUtil.getDataDir(),
					AuthenticationConst.USER_SAVE_FILE_NAME);
			instance = new UserManager(saveFile);
		}
		return instance;
	}

	/**
	 * <p>コンストラクタ</p>
	 * 他のクラスでインスタンスを生成できないため、
	 * getInstanceメソッドを呼び出してインスタンスを取得する必要がある。
	 */
	private UserManager(File saveFile) {
		super(saveFile);
		load();
	}

	@Override
	protected User parse(String data) throws ApplicationException {
		String[] datas = data.split(CommonConst.TAB);

		if (datas.length != AuthenticationConst.USER_DATA_COLUMNS) {
			throw new ApplicationException (Message.getMessage(
						AuthenticationMessage.E9999,
						"データファイル[" + this.getSaveFileName() + "]の内容が壊れています。"));
		}

		String id = datas[0];
		String name = datas[1];
		Authority authority;
		try {
			authority = Authority.getAuthority(Integer.parseInt(datas[2]));
		} catch (NumberFormatException e) {
			throw new ApplicationException (Message.getMessage(
					AuthenticationMessage.E9999,
					"データファイル[" + this.getSaveFileName() + "]の内容が壊れています。"));
		}
		if (authority == null) {
			throw new ApplicationException (Message.getMessage(
					AuthenticationMessage.E9999,
					"データファイル[" + this.getSaveFileName() + "]の内容が壊れています。"));
		}
		String password = cipherMng.decrypt(datas[3]);
		return new User(id, name, password, authority);
	}

	@Override
	protected String format(User user) {
		StringBuilder buf = new StringBuilder();

		buf.append(user.getId());
		buf.append(CommonConst.TAB);
		buf.append(user.getName());
		buf.append(CommonConst.TAB);
		buf.append(user.getAuthority().getCode());
		buf.append(CommonConst.TAB);
		buf.append(cipherMng.encrypt(user.getPassword()));
		buf.append(CommonConst.LINE_SEPARATOR);

		return buf.toString();
	}

	/**
	 * <p>ログイン</p>
	 * ID/パスワードで認証を行います。<br>
	 * ID/パスワードが正しくない場合、エラーとなります。
	 * @param userId ユーザーID
	 * @param password パスワード
	 * @return ユーザ情報
	 */
	public User login(String userId, String password)
	throws ApplicationException {
		User loginedUser = null;
		if (userId != null && password != null) {
			Collection<User> users = this.getDataValues();
			for (User user : users) {
				if (user.getId().equals(userId)
						&& user.canAuthenticate(password)) {
					loginedUser = user;
					break;
				}
			}
		}
		if (loginedUser == null) {
			throw new ApplicationException(AuthenticationMessage.W0013);
		}
		return loginedUser;
	}

	/**
	 * <p>管理者作成</p>
	 * @param userId ユーザーID
	 * @param name 名前
	 * @param password パスワード
	 * @throws ApplicationException ユーザの作成に失敗した場合にスロー
	 */
	public final void createAdminUser(String userId, String name, String password, String passwordAgain)
	throws ApplicationException {
		UserUtil.checkUserId(userId);
		UserUtil.checkUserName(name);
		UserUtil.checkUserPassword(password, passwordAgain);

		if (containsUserId(userId)) {
			// 既にユーザーIDが同一のユーザが存在する
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0015));
		}
		User user = new User(userId, name, password, Authority.ADMIN);
		this.insert(user);
	}

	/**
	 * 登録ユーザー件数取得
	 * @return ユーザー件数
	 */
	public final int getUserCount() {
		return this.getDataCount();
	}

	/**
	 * <p>ユーザー作成</p>
	 * 管理者のみが実行可能
	 * @param loginUser 実行ユーザー
	 * @param userId ユーザーID
	 * @param name ユーザー名
	 * @return パスワード
	 * @throws ApplicationException ユーザの作成に失敗した場合にスロー
	 */
	public final String createUser(User loginUser, String userId, String name)
	throws ApplicationException {
		if (!isAdminUser(loginUser)) {
			// 権限がない
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0019));
		}
		UserUtil.checkUserId(userId);
		UserUtil.checkUserName(name);

		if (containsUserId(userId)) {
			// 既にユーザーIDが同一のユーザが存在する
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0015));
		}

		String password = UserUtil.createRandomPassword();
		User user = new User(userId, name, password, Authority.USER);
		this.insert(user);

		return password;
	}

	/**
	 * <p>ユーザーリスト取得</p>
	 * 管理者のみが実行可能
	 * @param loginUser 実行ユーザー
	 * @return ユーザーリスト
	 * @throws ApplicationException ユーザーリストの取得に失敗した場合にスロー
	 */
	public List<User> getUserList(User loginUser)
	throws ApplicationException {
		if (!isAdminUser(loginUser)) {
			// 権限がない
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0019));
		}
		List<User> userList = new ArrayList<>();
		userList.addAll(this.getDataValues());
		return userList;
	}

	/**
	 * <p>パスワードリセット</p>
	 * 管理者のみが実行可能
	 * @param loginUser 実行ユーザー
	 * @param targetUser パスワードリセット対象ユーザー
	 * @return リセット後パスワード文字列
	 * @throws ApplicationException パスワードリセットに失敗した場合にスロー
	 */
	public String resetPassword(User loginUser, User targetUser)
	throws ApplicationException {
		if (!isAdminUser(loginUser)) {
			// 権限がない
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0019));
		}

		String password;
		if (this.contains(targetUser)) {
			password = UserUtil.createRandomPassword();
			targetUser.resetPassword(password);
		} else {
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0020));
		}

		return password;
	}

	/**
	 * <p>ユーザー削除</p>
	 * 管理者のみが実行可能
	 * @param loginUser 実行ユーザー
	 * @param targetUser 対象ユーザー
	 * @throws ApplicationException ユーザー削除に失敗した場合にスロー
	 */
	public void deleteUser(User loginUser, User targetUser)
	throws ApplicationException {
		if (!isAdminUser(loginUser)) {
			// 権限がない
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0019));
		}

		if (this.contains(targetUser)) {
			this.delete(targetUser);
		}
	}

	/**
	 * ユーザーIDが存在確認
	 * @param userId ユーザーID
	 * @return　確認結果(true:ユーザーIDが同一のユーザが存在する, false：ユーザーIDが同一のユーザは存在しない)
	 */
	private boolean containsUserId(String userId) {
		return this.contains(userId);
	}

	/**
	 * 管理者権限確認
	 * @param loginUser 対象ユーザ
	 * @return 確認結果(true：管理者, false：管理者でない)
	 */
	private boolean isAdminUser(User loginUser) {
		boolean result = false;
		if (loginUser != null
				&& loginUser.getAuthority().equals(Authority.ADMIN)) {
			// 権限がない
			result = true;
		}
		return result;
	}

	/**
	 * コミット処理（ユーザー情報を保存する）
	 */
	public void commit() {
		this.save();
	}
}
