package org.myapp.authentication.constant;

public class AuthenticationConst {

	/** コンストラクタ(インスタンス生成不可 **/
	private AuthenticationConst() {}

	public static final String USER_SAVE_FILE_NAME = "user.dat";

	public static final int USER_DATA_COLUMNS = 4;

	public static final int PASSWORD_MIN_LENGTH = 6;

	public static final String USER_ID_USE_DATA = "abcdefghijklmnopqrstuvwxuzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!#$%&'()=-~^{}[]`@+;*,._";

	public static final String RANDOM_PASSWORD_USE_DATA = "abcdefghijklmnopqrstuvwxuzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!#$%&'()=-~^{}[]`@+;*,._/<>|:";

	public static final String CONTEXT_KEY_LOGIN_USER = "loginUser";
	public static final String CONTEXT_KEY_STYLE_SHEET = "styleSheet";
}
