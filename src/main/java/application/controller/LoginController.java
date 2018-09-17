package application.controller;

import java.io.IOException;
import java.util.Map;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.User;
import org.myapp.authentication.data.UserManager;

import application.constant.Message;
import application.contoller.ControllerBase;
import application.exception.ApplicationException;
import application.model.ApplicationContext;
import application.util.ApplicationUtil;
import javafx.application.Application.Parameters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController extends ControllerBase {

	public static final void show(Runnable afterProcess) throws IOException {

		FXMLLoader loader = ApplicationUtil.getFXMLLoader("Login.fxml");
		loader.load();

		Parent root = loader.getRoot();
		Scene scene = new Scene(root);

		if (ApplicationContext.contains(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET)) {
			scene.getStylesheets().add((String) ApplicationContext.get(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET));
		}

		Stage appStage = ApplicationContext.getPrimaryStage();

		LoginController controller = (LoginController) loader.getController();
		controller.setStage(appStage);
		controller.setRunnable(afterProcess);

		appStage.setScene(scene);
		appStage.setResizable(false);

		appStage.show();
	}

	/** ログイン画面 **/
	@FXML
	private Label toolNameLabel;
	@FXML
	private TextField loginId;
	@FXML
	private PasswordField loginPassword;

	private Runnable afterProcess;

	private void setRunnable(Runnable afterProcess) {
		this.afterProcess = afterProcess;
	}

	@FXML
	public void initialize() {
		this.toolNameLabel.setText(ApplicationUtil.getApplicationName());
		this.loginId.setText(null);
		this.loginPassword.setText(null);

		this.loginId.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.loginPassword.requestFocus();
			}
		});

		this.loginPassword.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.onClickLoginButton();
			}
		});

		ApplicationContext.remove(AuthenticationConst.CONTEXT_KEY_LOGIN_USER);

		Parameters params = ApplicationContext.getParameters();
		if (params != null) {
			Map<String, String> namedParams = params.getNamed();
			if (namedParams.containsKey("id")) {
				this.loginId.setText(namedParams.get("id"));
			}
			if (namedParams.containsKey("password")) {
				this.loginPassword.setText(namedParams.get("password"));
			}
		}
	}

	/**
	 * ログインボタン押下時の処理
	 */
	public void onClickLoginButton() {
		String loginId = this.loginId.getText();
		String loginPassword = this.loginPassword.getText();

		try {
			UserManager userMng = UserManager.getInstance();
			User loginUser = userMng.login(loginId, loginPassword);
			ApplicationContext.set(AuthenticationConst.CONTEXT_KEY_LOGIN_USER, loginUser);

			if (loginUser.isAdmin()) {
				UserManagerController.show();
			} else {
				this.afterProcess.run();
			}

		} catch (ApplicationException e) {
			this.showErrorMessage(e.getMessage(), e.getCause());
		} catch (Exception e) {
			this.showSystemErrorMessage(Message.getMessage(AuthenticationMessage.E9999), e);
		}
	}
}
