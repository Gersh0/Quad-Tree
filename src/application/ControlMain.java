package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import code.NodoQ;
import code.QuadTree;
import code.QuadTreeSegmented;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ControlMain implements Initializable {

	@FXML
	Button ingresarArchivoButton;
	@FXML
	ImageView imgV1;
	@FXML
	ImageView imgV2;
	@FXML
	Button borrarButton;
	@FXML
	Button generarButton;
	@FXML
	Button helpButton;
	@FXML
	ToolBar toolBar;
	@FXML
	Label archivoLabelIMG1;
	@FXML
	Label archivoLabelIMG2;
	@FXML
	ComboBox<String> profundidadComboBox;

	String[] valueProfundidad = { "Original", "Seccionada" };
	String valorCB;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		profundidadComboBox.getItems().addAll(valueProfundidad);
		profundidadComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				valorCB = newValue;
			}
		});
	}

	String rutaImagenInsertada; // esta ruta se va a usar en el metodo generar QuadTree
	String nombreImagenInsertada;

	// Método para manejar la selección de archivos
	public void seleccionarImagen(ActionEvent event) {

		if (imgV1.getImage() != null) {
			// Muestra una ventana emergente de advertencia
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Advertencia");
			alert.setHeaderText(null);
			alert.setContentText("Imagen ya insertada, Borre la imagen primero");

			alert.showAndWait();
		} else {
			// establecer filtro de extension
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters()
					.addAll(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

			// Abre el cuadro de diálogo para seleccionar un archivo
			File selectedFile = fileChooser.showOpenDialog(new Stage());

			if (selectedFile != null) {
				Image image = new Image(selectedFile.toURI().toString()); // se instancia la imagen seleccionada
				imgV1.setImage(image); // esa imagen se setea en el imageView
				animacionImagenFadeIn(event, imgV1);// acciona la animacion
				rutaImagenInsertada = selectedFile.toString();// aqui se guarda la ruta para usar posteriormente
				archivoLabelIMG1.setText(selectedFile.getName());// actualiza el label con el nombre del archivo
				nombreImagenInsertada = selectedFile.getName();
			}
		}
	}

	// seccion de animaciones experimentales para las imgV1 y imgV2

	public void animacionImagenFadeIn(ActionEvent event, ImageView imgView) {
		if (imgView.getImage() != null) {

			// las fade transition requieren de la duracion y su objetivo , posteriormente
			// el valor inicial y el valor final de la opacidad
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), imgView);
			fadeTransition.setFromValue(0.0);
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
		}
	}

	public void animacionImagenFadeOut(ImageView imgView) {
		if (imgView.getImage() != null) {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), imgView);
			fadeTransition.setFromValue(1.0);
			fadeTransition.setToValue(0);

			// Establece un evento para limpiar la imagen después de la animación
			fadeTransition.setOnFinished(event -> {
				imgView.setImage(null);
			});

			fadeTransition.play();
		}
	}

	public void limpiarImagen(ActionEvent event) {
		animacionImagenFadeOut(imgV1);
		animacionImagenFadeOut(imgV2);
		archivoLabelIMG1.setText(null);
		archivoLabelIMG2.setText(null);
	}

	public void generarQuadTree(ActionEvent event) {
		// Verifica si la ImageView está vacía
		if (imgV1.getImage() == null) {
			// Muestra una ventana emergente de advertencia si la imagen de entrada es nula.
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Advertencia");
			alert.setHeaderText(null);
			alert.setContentText("Primero ingrese un archivo de imagen: " + "\n" + " .png, .jpg, .jpeg, .gif, .bmp.");
			alert.showAndWait();
		} else if (imgV2.getImage() != null) {
			// Muestra una ventana emergente de advertencia si la imagen ya ha sido
			// generada.
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Advertencia");
			alert.setHeaderText(null);
			alert.setContentText("Imagen ya generada");
			alert.showAndWait();
		} else {
			if (valorCB == "Seccionada") {
				quadTreeSeccionado();
				animacionImagenFadeIn(event, imgV2);
			} else {

				quadTreeOriginal();
				animacionImagenFadeIn(event, imgV2);
			}

		}
	}

	public void quadTreeOriginal() {
		// Crear un objeto QuadTree
		QuadTree arbol = new QuadTree(new NodoQ());

		// Cargar la imagen desde la ruta proporcionada en rutaImagenInsertada
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread(rutaImagenInsertada);

		// Convertir la imagen a escala de grises
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

		double tiempoInicial = System.currentTimeMillis();

		// Generar el QuadTree a partir de la imagen en escala de grises
		arbol.generarArbol(grayImage);

		// Reconstruir la imagen a partir del QuadTree
		Mat imagenReconstruida = arbol.reconstruirImagen();

		System.out.println(System.currentTimeMillis() - tiempoInicial);

		// Definir la ruta donde se guardará la imagen reconstruida
		String directorioUsuario = System.getProperty("user.dir");
		String rutaImagen = directorioUsuario + "/Pruebas/" + "QuadtreeOf" + nombreImagenInsertada;

		// Guardar la imagen reconstruida en la ruta especificada
		Imgcodecs.imwrite(rutaImagen, imagenReconstruida);

		// Crear un objeto Image a partir de la imagen guardada
		Image imagenFinal = new Image("file:" + rutaImagen);

		// Mostrar la imagen reconstruida en la segunda ImageView
		imgV2.setImage(imagenFinal);

		// generar texto del quadTree
		// String rutaTexto = directorioUsuario + "/Textos/" + "QuadtreeOf" +
		// nombreImagenInsertada + ".txt";
		// QuadTree.saveMatrixToTextFile(rutaTexto, imagenReconstruida);

		// aqui iba una animacion

		System.out.println(nombreImagenInsertada + " Generado exitoso");

		// Actualizar el texto del label en la interfaz
		archivoLabelIMG2.setText("QuadtreeOf" + nombreImagenInsertada);

		System.out.println(valorCB);
		// Liberar recursos
		imagenFinal = null;
	}

	public void quadTreeSeccionado() {
		// Crear un objeto QuadTree
		QuadTreeSegmented arbol = new QuadTreeSegmented(new NodoQ());

		// Cargar la imagen desde la ruta proporcionada en rutaImagenInsertada
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread(rutaImagenInsertada);

		// Convertir la imagen a escala de grises
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

		double tiempoInicial = System.currentTimeMillis();

		// Generar el QuadTree a partir de la imagen en escala de grises
		arbol.generarArbol(grayImage);

		// Reconstruir la imagen a partir del QuadTree
		Mat imagenReconstruida = arbol.reconstruirImagen();

		System.out.println(System.currentTimeMillis() - tiempoInicial);

		// Definir la ruta donde se guardará la imagen reconstruida
		String directorioUsuario = System.getProperty("user.dir");
		String rutaImagen = directorioUsuario + "/Pruebas/" + "Segmented" + "QuadtreeOf" + nombreImagenInsertada;

		// Guardar la imagen reconstruida en la ruta especificada
		Imgcodecs.imwrite(rutaImagen, imagenReconstruida);

		// Crear un objeto Image a partir de la imagen guardada
		Image imagenFinal = new Image("file:" + rutaImagen);

		// Mostrar la imagen reconstruida en la segunda ImageView
		imgV2.setImage(imagenFinal);

		// generar texto del quadTree
		// String rutaTexto = directorioUsuario + "/Textos/" + "QuadtreeOf" +
		// nombreImagenInsertada + ".txt";
		// QuadTree.saveMatrixToTextFile(rutaTexto, imagenReconstruida);

		// aqui iba una animacion

		System.out.println(nombreImagenInsertada + " Generado exitoso");

		// Actualizar el texto del label en la interfaz
		archivoLabelIMG2.setText("SegmentedQuadtreeOf" + nombreImagenInsertada);

		System.out.println(valorCB);
		// Liberar recursos
		imagenFinal = null;
	}

	public void mostrarAyuda(ActionEvent event) {
		Alert info = new Alert(AlertType.INFORMATION);
		info.setTitle("Instrucciones");
		info.setHeaderText(null);
		info.setContentText("1. Ingrese una imagen" + "\n" + "2. Pulse generar");
		// Abre el cuadro de diálogo
		info.showAndWait();
	}

	// importar Libreria OpenCV
	// seccion de conversores de imagen

	public static Mat convertirImagenJavaFXAOpenCVMat(Image fxImage) {

		// Convertir la imagen de JavaFX a BufferedImage
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

		// Crear una matriz OpenCV (Mat) a partir de la BufferedImage
		Mat openCvMat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
		byte[] data = ((java.awt.image.DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
		openCvMat.put(0, 0, data);

		return openCvMat;
	}

	public static BufferedImage convertirImagenJavaFXABufferedImage(Image fxImage) {
		// Convertir la imagen de JavaFX a BufferedImage
		BufferedImage BufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

		return BufferedImage;
	}

}
