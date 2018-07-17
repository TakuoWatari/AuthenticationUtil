package application;

import java.io.IOException;
import java.net.URL;

import org.myapp.authentication.constant.AuthenticationConst;
import org.myapp.authentication.constant.AuthenticationMessage;
import org.myapp.authentication.data.UserManager;

import application.constant.Message;
import application.contoller.Controller;
import application.controller.CreateAdminUserController;
import application.controller.LoginController;
import application.model.ApplicationContext;
import application.util.ApplicationUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public abstract class AuthenticationApplication extends Application implements ApplicationBase {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			primaryStage.setTitle(ApplicationUtil.getApplicationName());
			Image icon = this.getIcon();
			if (icon != null) {
				primaryStage.getIcons().add(icon);
			}

			ApplicationContext.initialize(primaryStage, this.getParameters());

			URL appSylesheetURL = this.getStyleSheetUrl();
			if (appSylesheetURL != null) {
				ApplicationContext.set(AuthenticationConst.CONTEXT_KEY_STYLE_SHEET, appSylesheetURL.toExternalForm());
			}

			if (isNecessaryAuthentication()) {
				UserManager userMng = UserManager.getInstance();

				if (userMng.getUserCount() == 0) {
					CreateAdminUserController.show();
				}

				if (userMng.getUserCount() > 0) {
					LoginController.show(() -> {
						this.show();
					});
				}
			} else {
				show();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void show() {
		try {
			FXMLLoader loader = this.getLoader();
			loader.load();

			Parent root = loader.getRoot();
			Scene scene = new Scene(root);

			URL appSylesheetURL = this.getStyleSheetUrl();
			if (appSylesheetURL != null) {
				scene.getStylesheets().add(appSylesheetURL.toExternalForm());
			}

			Stage appStage = ApplicationContext.getPrimaryStage();

			Controller controller = loader.getController();
			controller.setStage(appStage);

			appStage.setScene(scene);
			appStage.setResizable(isResizable());
			appStage.show();

		} catch (IOException e) {
			throw new IllegalStateException(
					Message.getMessage(AuthenticationMessage.E9999), e);
		}
	}

	protected abstract boolean isNecessaryAuthentication();
}
