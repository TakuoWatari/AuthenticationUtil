package application.controller;

import java.io.IOException;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.User;
import org.myapp.authentication.data.UserManager;

import application.constant.Message;
import application.content.PopupWindow;
import application.contoller.ControllerBase;
import application.exception.ApplicationException;
import application.model.ApplicationContext;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class UserInfoSettingController extends ControllerBase {

	private static PopupWindow dialog;

	public static final synchronized void show(Window owner) throws IOException {
		if (dialog == null) {
			dialog = new PopupWindow(
					owner,
					StageStyle.DECORATED,
					"ユーザー情報設定画面",
					"UserInfoSetting.fxml",
					Modality.WINDOW_MODAL,
					false,
					false,
					(String) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET));
		}

		dialog.show();
	}

	/** ユーザー設定 **/
	@FXML
	private TextField loginUserId;
	@FXML
	private TextField loginUserName;
	@FXML
	private PasswordField loginUserPassword;
	@FXML
	private PasswordField loginUserNewPassword;
	@FXML
	private PasswordField loginUserNewPasswordAgain;

	@FXML
	public void initialize() {
		User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
		this.loginUserId.setText(loginUser.getId());
		this.loginUserName.setText(loginUser.getName());
		this.loginUserPassword.setText(null);
		this.loginUserNewPassword.setText(null);
		this.loginUserNewPasswordAgain.setText(null);
	}

	/**
	 * 名前変更ボタン押下時の処理
	 */
	public void onClickNameChangeButton() {
		String name = this.loginUserName.getText();
		try {
			UserManager userMng = UserManager.getInstance();
			User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
			loginUser.changeName(name);
			userMng.commit();

			this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0004));

		} catch (ApplicationException e) {
			this.showErrorMessage(Message.getMessage(AuthenticationMessage.E0003), e);
		} catch (Exception e) {
			this.showSystemErrorMessage(Message.getMessage(AuthenticationMessage.E9999), e);
		}
	}

	/**
	 * パスワード変更ボタン押下時の処理
	 */
	public void onClickPasswordChangeButton() {
		String password = this.loginUserPassword.getText();
		String newPassword = this.loginUserNewPassword.getText();
		String newPasswordAgain = this.loginUserNewPasswordAgain.getText();
		try {
			UserManager userMng = UserManager.getInstance();
			User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
			loginUser.changePassword(password, newPassword, newPasswordAgain);
			userMng.commit();

			this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0007));

		} catch (ApplicationException e) {
			this.showErrorMessage(Message.getMessage(AuthenticationMessage.E0003), e);
		} catch (Exception e) {
			this.showSystemErrorMessage(Message.getMessage(AuthenticationMessage.E9999), e);
		}
	}

	/**
	 * 閉じるボタン押下時の処理
	 */
	public void onClickCloseButtton() {
		this.close();
	}
}
