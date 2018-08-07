package application.controller;

import java.io.IOException;
import java.util.List;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.User;
import org.myapp.authentication.data.UserManager;

import application.constant.Message;
import application.contoller.Controller;
import application.contoller.ControllerBase;
import application.exception.ApplicationException;
import application.model.ApplicationContext;
import application.util.ApplicationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserManagerController extends ControllerBase {

	public static final void show() throws IOException {
		FXMLLoader loader = ApplicationUtil.getFXMLLoader("UserManager.fxml");
		loader.load();

		Parent root = loader.getRoot();
		Scene scene = new Scene(root);

		if (ApplicationContext.contains(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET)) {
			scene.getStylesheets().add((String) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET));
		}

		Stage appStage = ApplicationContext.getPrimaryStage();

		Controller controller = loader.getController();
		controller.setStage(appStage);

		appStage.setScene(scene);
		appStage.setResizable(false);

		appStage.show();
	}

	/** ユーザー管理 **/
	@FXML
	private Tab userMngTab;
	@FXML
	private TextField newUserId;
	@FXML
	private TextField newUserName;
	@FXML
	private ChoiceBox<User> passwordResetUserChoiceBox;
	@FXML
	private ChoiceBox<User> deleteUserChoiceBox;

	private ObservableList<User> userList;

	@FXML
	public void initialize() {
		this.userList = FXCollections.observableArrayList();

		this.passwordResetUserChoiceBox.setItems(this.userList);
		this.deleteUserChoiceBox.setItems(this.userList);

		try {
			this.refreshUser();
		} catch (ApplicationException e) {
			this.showErrorMessage(AuthenticationMessage.E0002, e);
		}
	}

	private void refreshUser() throws ApplicationException {
		try {
			UserManager userMng = UserManager.getInstance();

			User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
			List<User> users = userMng.getUserList(loginUser);

			users.remove(loginUser);

			this.userList.clear();
			this.userList.addAll(users);

			if (!users.isEmpty()) {
				this.passwordResetUserChoiceBox.setValue(users.get(0));
				this.deleteUserChoiceBox.setValue(users.get(0));
			} else {
				this.passwordResetUserChoiceBox.setValue(null);
				this.deleteUserChoiceBox.setValue(null);
			}
		} catch (IOException e) {
			throw new ApplicationException(Message.getMessage(AuthenticationMessage.E9999, "ユーザ情報の読み込みに失敗しました。"), e);
		}
	}

	/**
	 * ユーザー登録ボタン押下時の処理
	 */
	public void onClickAddUserButton() {
		String userId = this.newUserId.getText();
		String userName = this.newUserName.getText();

		try {
			UserManager userMng = UserManager.getInstance();

			User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
			String password = userMng.createUser(loginUser, userId, userName);
			userMng.commit();

			this.newUserId.setText(null);
			this.newUserName.setText(null);

			refreshUser();

			this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0002, userId, password));

		} catch (ApplicationException e) {
			this.showErrorMessage(AuthenticationMessage.E0002, e);
		} catch (Exception e) {
			this.showSystemErrorMessage(AuthenticationMessage.E9999, e);
		}
	}

	/**
	 * パスワードリセットボタン押下時の処理
	 */
	public void onClickPasswordResetButton() {
		User targetUser = this.passwordResetUserChoiceBox.getValue();
		if (targetUser != null) {
			try {
				UserManager userMng = UserManager.getInstance();

				User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
				String password = userMng.resetPassword(loginUser, targetUser);
				userMng.commit();

				refreshUser();

				this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0003, targetUser.getId(), password));

			} catch (ApplicationException e) {
				this.showErrorMessage(AuthenticationMessage.E0003, e);
			} catch (Exception e) {
				this.showSystemErrorMessage(AuthenticationMessage.E9999, e);
			}
		}
	}

	/**
	 * ユーザー削除ボタン押下時の処理
	 */
	public void onClickDeleteUserButton() {
		User targetUser = this.deleteUserChoiceBox.getValue();
		if (targetUser != null) {
			try {
				UserManager userMng = UserManager.getInstance();

				User loginUser = (User) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);
				userMng.deleteUser(loginUser, targetUser);
				userMng.commit();

				refreshUser();

				this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0005, targetUser.getId()));

			} catch (ApplicationException e) {
				this.showErrorMessage(AuthenticationMessage.E0004, e);
			} catch (Exception e) {
				this.showSystemErrorMessage(AuthenticationMessage.E9999, e);
			}
		}
	}

	/**
	 * 閉じるボタン押下時の処理
	 */
	public void onClickCloseButton() {
		this.close();
	}
}
