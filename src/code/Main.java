package code;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {

	public static void main(String[] args) {

		QuadTree arbol = new QuadTree(new NodoQ());
		QuadTree arbol2 = new QuadTree(new NodoQ());

		// Load the image file
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread("128x128.png");
		// Convert the image to grayscale
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

		arbol.generarArbol(grayImage);

		Mat imagenReconstruida = arbol.reconstruirImagen();

		Mat image2 = Imgcodecs.imread("Monday2.png");
		// Convert the image to grayscale
		Mat grayImage2 = new Mat();
		Imgproc.cvtColor(image2, grayImage2, Imgproc.COLOR_BGR2GRAY);

		arbol2.generarArbol(grayImage2);

		Mat imagenReconstruida2 = arbol2.reconstruirImagen();

		// Imgcodecs.imwrite("C:\\Users\\juliana
		// salazar\\eclipse-workspace\\QuadTree\\Pruebas\\Monday.png",
		// imagenReconstruida);
		Imgcodecs.imwrite("C:\\Users\\juliana salazar\\eclipse-workspace\\QuadTree\\Pruebas\\128x128.png",
				imagenReconstruida);
		Imgcodecs.imwrite("C:\\Users\\juliana salazar\\eclipse-workspace\\QuadTree\\Pruebas\\Monday2.png",
				imagenReconstruida2);
		// Imgcodecs.imwrite("C:\\Users\\juliana
		// salazar\\eclipse-workspace\\QuadTree\\Pruebas\\testPattern.png",
		// imagenReconstruida);

	}
}
