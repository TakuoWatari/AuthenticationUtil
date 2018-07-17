package org.myapp.authentication.constant;

public enum Authority {

	ADMIN(9, "管理者"),
	USER(1, "利用者");

	private int code;
	private String name;

	private Authority(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public int getCode() {
		return this.code;
	}

	public static Authority getAuthority(int code) {
		Authority authority;
		switch (code) {
			case 9 : 
				authority = ADMIN;
				break;
			case 1 : 
				authority = USER;
				break;
			default :
				authority = null;
				break;
		}
		return authority;
	}
}
