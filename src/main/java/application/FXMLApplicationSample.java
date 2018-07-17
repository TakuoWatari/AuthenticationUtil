package application;

import java.net.URL;

import application.util.ApplicationUtil;
import javafx.fxml.FXMLLoader;

public class FXMLApplicationSample extends AuthenticationApplication {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public FXMLLoader getLoader() {
		return ApplicationUtil.getFXMLLoader("Sample.fxml");
	}

	@Override
	public URL getStyleSheetUrl() {
		return ApplicationUtil.getStyleSheetURL("application.css");
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	protected boolean isNecessaryAuthentication() {
		return true;
	}
}
