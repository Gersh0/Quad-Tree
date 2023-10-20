package application;

import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javafx.scene.shape.Line;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class ControlIntro {

	@FXML
	Button iniciarButton;
	@FXML
	AnchorPane ventanaAnchorPane;
	@FXML
	Line line1;
	@FXML
	Line line2;
	@FXML
	Pane panel1;
	@FXML
	Pane panel2;
	@FXML
	Pane panel3;
	@FXML
	Pane panel4;

	// seccion para cambiar pagina a otra

	// variables necesarias para hacer la animacion

	private List<Pane> paneList = new ArrayList<>();
	private ActionEvent storedEvent; // Variable para almacenar el evento
	private boolean botonClickeado = false;
	
	public void IntroToMain(ActionEvent event) throws IOException {
		if(!botonClickeado) {
			storedEvent = event; // Almacena el evento para su uso posterior

			// Llena la lista 'paneList' con los paneles en orden
			paneList.add(panel1);
			paneList.add(panel2);
			paneList.add(panel4);
			paneList.add(panel3);

			// Comienza la secuencia de desvanecimiento
			fadeOutNextPane(0);
			botonClickeado = true;
		}
	}

	// este metodo es asi ya que cuando lo intente con ciclos no funcionaba

	private void fadeOutNextPane(int index) {
		// metodo recursivo donde se recorre la lista para ejecutar el desvanecimiento
		if (index >= 0 && index < paneList.size()) {
			Pane pane = paneList.get(index);

			// se declaran los atributos de la FadeTransition(duracion,objeto)
			FadeTransition fadeOut = new FadeTransition(Duration.millis(500), pane);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.setOnFinished(e -> {
				// aqui se ejecuta la recursividad
				fadeOutNextPane(index + 1);
			});

			fadeOut.play();
		} else {
			// Cuando todos los paneles se han desvanecido, abre la otra página
			openMainPage();
		}
	}

	// esto simplemente abre la pagina
	private void openMainPage() {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Stage stage = (Stage) ((Node) storedEvent.getSource()).getScene().getWindow(); // Usa storedEvent
		String css = getClass().getResource("application.css").toExternalForm();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();

	}

}
