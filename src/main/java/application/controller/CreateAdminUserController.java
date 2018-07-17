package application.controller;

import java.io.IOException;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.UserManager;

import application.constant.Message;
import application.contoller.Controller;
import application.contoller.ControllerBase;
import application.exception.ApplicationException;
import application.model.ApplicationContext;
import application.util.ApplicationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAdminUserController extends ControllerBase {

	public static final void show() throws IOException {

		FXMLLoader loader = ApplicationUtil.getFXMLLoader("CreateAdminUser.fxml");
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

	/** 管理者登録画面 **/
	@FXML
	private Label toolNameLabel;
	@FXML
	private TextField adminId;
	@FXML
	private TextField adminName;
	@FXML
	private PasswordField adminPassword;
	@FXML
	private PasswordField adminPasswordAgain;

	@FXML
	public void initialize() {
		this.toolNameLabel.setText(ApplicationUtil.getApplicationName());
		this.adminId.setText(null);
		this.adminName.setText(null);
		this.adminPassword.setText(null);
		this.adminPasswordAgain.setText(null);
	}

	/**
	 * 管理者登録ボタン押下時の処理
	 */
	public void onClickAdminAddButton() {
		String userId = this.adminId.getText();
		String name = this.adminName.getText();
		String password = this.adminPassword.getText();
		String passwordAgain = this.adminPasswordAgain.getText();

		try {
			UserManager userMng = UserManager.getInstance();
			userMng.createAdminUser(userId, name, password, passwordAgain);
			userMng.commit();

			this.showInfoMessage(Message.getMessage(AuthenticationMessage.I0001));

			this.hide();

		} catch (ApplicationException e) {
			this.showWarnMessage(Message.getMessage(AuthenticationMessage.E0002), e);
		} catch (Exception e) {
			this.showSystemErrorMessage(Message.getMessage(AuthenticationMessage.E9999), e);
		}
	}
}
