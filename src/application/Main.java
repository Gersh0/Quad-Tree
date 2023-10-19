package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {

		// carga el archivo intro.FXML el cual es la base de la aplicacion
		Parent root = FXMLLoader.load(getClass().getResource("Intro.fxml"));
		// se establece una ventana que va a ser formada apartir del archivo
		Scene scene = new Scene(root);
		// setea el ciono
		Image icono = new Image(getClass().getResourceAsStream("cuadriculaBN32x32.jpg"));
		// obtiene los estilos css
		String css = getClass().getResource("Intro.css").toExternalForm();
		scene.getStylesheets().add(css);
		// titulo e icono
		primaryStage.setTitle("QuadTree");
		primaryStage.getIcons().add(icono);

		// propiedades e inicializar
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
