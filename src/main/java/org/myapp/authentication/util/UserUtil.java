package org.myapp.authentication.util;

import java.security.SecureRandom;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;

import application.constant.Message;
import application.exception.ApplicationException;
import application.util.CommonUtil;

public class UserUtil {

	private static final SecureRandom PASSWORD_RANDOM;

	static {
		SecureRandom random = new SecureRandom();
		random.setSeed(System.nanoTime());
		PASSWORD_RANDOM = random;
	}

	/**
	 * コンストラクタ
	 * ※インスタンスの生成ができないようにprivate修飾子を付与
	 **/
	private UserUtil() {}

	/**
	 * ユーザーIDチェック部品
	 * @param userId ユーザーID
	 * @throws ApplicationException 入力値が不正な場合にスロー
	 */
	public static final void checkUserId(String userId)
	throws ApplicationException {
		if (CommonUtil.isEmpty(userId)) {
			// ユーザーIDが未入力
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0001));
		}

		int length = userId.length();
		for (int i = 0; i < length; i++) {
			if (AuthenticationConst.USER_ID_USE_DATA.indexOf(userId.charAt(i)) == -1) {
				// ユーザーIDに禁則文字が含まれている
				throw new ApplicationException(
						Message.getMessage(AuthenticationMessage.W0002));
			}
		}
	}

	/**
	 * ユーザー名チェック部品
	 * @param name ユーザー名
	 * @throws ApplicationException 入力値が不正な場合にスロー
	 */
	public static final void checkUserName(String name)
	throws ApplicationException {
		if (CommonUtil.isEmpty(name)) {
			// ユーザー名が未入力
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0003));
		}
	}

	/**
	 * パスワードチェック部品
	 * @param password パスワード
	 * @throws ApplicationException 入力値が不正な場合にスロー
	 */
	public static final void checkUserPassword(String password, String passwordAgain)
	throws ApplicationException {
		if (CommonUtil.isEmpty(password)
				|| password.length() < AuthenticationConst.PASSWORD_MIN_LENGTH) {
			// パスワード文字列が6文字未満
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0004,
							String.valueOf(AuthenticationConst.PASSWORD_MIN_LENGTH)));
		}
		if (CommonUtil.isEmpty(passwordAgain)
				|| !password.equals(passwordAgain)) {
			// 新パスワードと新パスワード（確認用）の値が異なる
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0021));
		}
	}

	/**
	 * 新パスワードチェック部品
	 * @param password 現パスワード
	 * @param newPassword 新パスワード
	 * @param newPasswordAgain 新パスワード(確認用)
	 * @throws ApplicationException 入力値が不正な場合にスロー
	 */
	public static final void checkUserNewPassword(String password, String newPassword, String newPasswordAgain)
	throws ApplicationException {
		if (CommonUtil.isEmpty(password)) {
			// 現パスワード文字列が未入力
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0016));
		}
		if (CommonUtil.isEmpty(newPassword)
				|| newPassword.length() < AuthenticationConst.PASSWORD_MIN_LENGTH) {
			// 新パスワード文字列が6文字未満
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0005,
							String.valueOf(AuthenticationConst.PASSWORD_MIN_LENGTH)));
		}
		if (CommonUtil.isEmpty(newPasswordAgain)
				|| !newPassword.equals(newPasswordAgain)) {
			// 新パスワードと新パスワード（確認用）の値が異なる
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0017));
		}
		if (password.equals(newPassword)) {
			// 現パスワードと新パスワードが同じ
			throw new ApplicationException(
					Message.getMessage(AuthenticationMessage.W0018));
		}
	}

	/**
	 * パスワード文字列自動生成
	 * @return パスワード文字列
	 */
	public static final String createRandomPassword() {

		int dataLength = AuthenticationConst.RANDOM_PASSWORD_USE_DATA.length();
		StringBuilder randomPassword = new StringBuilder();

		for (int i = 0; i < AuthenticationConst.PASSWORD_MIN_LENGTH; i++) {
			int index = PASSWORD_RANDOM.nextInt(dataLength);
			randomPassword.append(AuthenticationConst.RANDOM_PASSWORD_USE_DATA.charAt(index));
		}

		return randomPassword.toString();
	}
}
