package code;

import java.io.FileWriter;
import java.io.IOException;

import org.opencv.core.*;

public class QuadTree {

	protected int depth;
	protected NodoQ raiz;
	protected Size imageSize;
	protected int imageType;
	protected boolean wasExtended;
	int filasNuevas;
	int columnasNuevas;

	public QuadTree(NodoQ r/* , int depth */) {// FALTAAAAAAAA
		this.raiz = r;
		/* this.depth = depth; */
	}

	public NodoQ getRaiz() {
		return raiz;
	}

	public boolean canDivideNE(Mat image) {
		if (image.cols() <= 1 || image.rows() <= 1) {
			return false;
		}
		boolean changeFound = false;
		int tempRow = 0;
		double[] tempColor = image.get(0, 0);
		while (++tempRow <= (image.rows() / 2) && !changeFound) {
			int tempCol = image.cols() / 2;
			while (++tempCol <= image.cols() && !changeFound && image.get(tempRow, tempCol) != null) {
				if (tempColor[0] != image.get(tempRow, tempCol)[0])
					changeFound = true;
			}
		}
		return changeFound;
	}

	public boolean canDivideNW(Mat image) {
		if (image.cols() <= 1 || image.rows() <= 1) {
			return false;
		}
		boolean changeFound = false;
		int tempRow = 0;
		double[] tempColor = image.get(0, 0);
		while (++tempRow <= (image.rows() / 2) && !changeFound) {
			int tempCol = 0;
			while (++tempCol <= (image.cols() / 2) && !changeFound && image.get(tempRow, tempCol) != null) {
				if (tempColor[0] != image.get(tempRow, tempCol)[0])
					changeFound = true;
			}
		}
		return changeFound;
	}

	public boolean canDivideSW(Mat image) {
		if (image.cols() <= 1 || image.rows() <= 1) {
			return false;
		}
		boolean changeFound = false;
		int tempRow = image.rows() / 2;
		double[] tempColor = image.get(0, 0);
		while (++tempRow <= image.rows() && !changeFound) {
			int tempCol = 0;
			while (++tempCol <= (image.cols() / 2) && !changeFound && image.get(tempRow, tempCol) != null) {
				if (tempColor[0] != image.get(tempRow, tempCol)[0])
					changeFound = true;
			}
		}
		return changeFound;
	}

	public boolean canDivideSE(Mat image) {
		if (image.cols() <= 1 || image.rows() <= 1) {
			return false;
		}
		boolean changeFound = false;
		int tempRow = image.rows() / 2;
		double[] tempColor = image.get(0, 0);
		while (++tempRow <= image.rows() && !changeFound) {
			int tempCol = image.cols() / 2;
			while (++tempCol <= image.cols() && !changeFound && image.get(tempRow, tempCol) != null) {
				if (tempColor[0] != image.get(tempRow, tempCol)[0])
					changeFound = true;
			}
		}
		return changeFound;
	}

	public void generarArbol(Mat image) {
		this.imageType = image.type();
		this.imageSize = image.size();
		// Se comprueba si la imagen ya está en ancho o alto de 2^n
		// para eso, si la cantidad total de pixeles es 4^n, el ancho y alto seran
		// tambien 2^n
		double log2Columnas = (Math.log10(image.width()) / Math.log10(2));
		double log2Filas = (Math.log10(image.height()) / Math.log10(2));

		if (log2Columnas % 1 != 0) {
			columnasNuevas = (int) Math.pow(2, (int) log2Columnas + 1);
		} else {
			columnasNuevas = image.cols();
		}
		if (log2Filas % 1 != 0) {
			filasNuevas = (int) Math.pow(2, (int) log2Filas + 1);
		} else {
			filasNuevas = image.rows();
		}

		if (columnasNuevas != 0 || filasNuevas != 0) {
			this.wasExtended = true;
			Mat temp = new Mat(filasNuevas, columnasNuevas, image.type());
			Mat subMat = new Mat(temp, new Rect(0, 0, image.cols(), image.rows()));
			image.copyTo(subMat);
			generarArbol(temp, this.raiz);
		} else {
			generarArbol(image, this.raiz);
		}
	}

	public void generarArbol(Mat image, NodoQ nodoActual) {

		boolean canDivideNE = canDivideNE(image);
		boolean canDivideNW = canDivideNW(image);
		boolean canDivideSW = canDivideSW(image);
		boolean canDivideSE = canDivideSE(image);
		if (!canDivideNE && !canDivideNW && !canDivideSE && !canDivideSW) {
			return;
		} else {
			Mat NW = new Mat(image, new Range(0, image.rows() / 2), new Range(0, image.cols() / 2));
			Mat NE = new Mat(image, new Range(0, image.rows() / 2), new Range(image.cols() / 2, image.cols()));
			Mat SW = new Mat(image, new Range(image.rows() / 2, image.rows()), new Range(0, image.cols() / 2));
			Mat SE = new Mat(image, new Range(image.rows() / 2, image.rows()),
					new Range(image.cols() / 2, image.cols()));
			NodoQ nodoNW = new NodoQ(NW, nodoActual);
			NodoQ nodoNE = new NodoQ(NE, nodoActual);
			NodoQ nodoSW = new NodoQ(SW, nodoActual);
			NodoQ nodoSE = new NodoQ(SE, nodoActual);
			nodoActual.setHijoNW(nodoNW);
			nodoActual.setHijoNE(nodoNE);
			nodoActual.setHijoSW(nodoSW);
			nodoActual.setHijoSE(nodoSE);

			if (canDivideNE) {
				generarArbol(NE, nodoNE);
			}
			if (canDivideNW) {
				generarArbol(NW, nodoNW);
			}
			if (canDivideSW) {
				generarArbol(SW, nodoSW);
			}
			if (canDivideSE) {
				generarArbol(SE, nodoSE);
			}
		}

/*
		
		if(columnas o filas igual a 1) PARAR
		else{
			Se guardan booleanos temporales de cada cuadrante.
			if(no se puede dividir en ningun cuadrante){
				Parada secundaria
			} else{
				
				Se generan las divisiones (Mat), y se ponen como hijas de la raiz actual.
				
				if(se puede dividir en el NW){	generarArbol(NW)}
				if(se puede dividir en el NE){	generarArbol(NE)}
				if(se puede dividir en el SE){	generarArbol(SE)}
				if(se puede dividir en el SW){	generarArbol(SW)}
				
			}
		}
		
		 */

	}

	public Mat reconstruirImagen() {
	       if(this.raiz.hasHijos()) {
	    	   if(this.wasExtended) {
	    		   Mat imagenExtendidaReconstruida = new Mat(new Size(columnasNuevas,filasNuevas), this.imageType);
	               reconstruirImagen(this.getRaiz(), imagenExtendidaReconstruida, 0, 0, columnasNuevas, filasNuevas);
	               Mat imagenReconstruida = new Mat(imagenExtendidaReconstruida, new Rect(0,0,(int)this.imageSize.width, (int)this.imageSize.height));
	               return imagenReconstruida;
	    	   } else {
	    		   Mat imagenReconstruida = new Mat(this.imageSize, this.imageType);
	               reconstruirImagen(this.getRaiz(), imagenReconstruida, 0, 0, (int)this.imageSize.width, (int)this.imageSize.height);
	               return imagenReconstruida;
	    	   }
	    	   
	           /*
	           Mat imagenReconstruida2 = arbol2.reconstruirImagen();
	           Mat imagenReconstruidaOriginal2 = new Mat(imagenReconstruida2, new Rect(0,0,image2.cols(),image2.rows()));
	           Imgcodecs.imwrite("C:\\Users\\juliana salazar\\eclipse-workspace\\QuadTree\\Pruebas\\testPattern.png", imagenReconstruidaOriginal2);
	           */
	       } else {
	    	   return null;
	    	   // en este caso, sale una excepcion.
	       }
	    }

	private void reconstruirImagen(NodoQ nodo, Mat imagenReconstruida, int inicioX, int inicioY, int ancho, int alto) {
        if (nodo == null) {
            return;
        }

        int x = inicioX + ancho/2;
        int y = inicioY + alto/2;

        
        reconstruirImagen(nodo.getHijoNE(), imagenReconstruida, x, inicioY, ancho/2, alto/2);
        reconstruirImagen(nodo.getHijoNW(), imagenReconstruida, inicioX, inicioY, ancho/2, alto/2);
        reconstruirImagen(nodo.getHijoSW(), imagenReconstruida, inicioX, y, ancho/2, alto/2);
        reconstruirImagen(nodo.getHijoSE(), imagenReconstruida, x, y, ancho/2, alto/2);

        
        if (nodo.getLlave() != null) {
            int anchoSubimagen = nodo.getLlave().cols();
            int altoSubimagen = nodo.getLlave().rows();
            int xSubimagen = x - anchoSubimagen / 2;
            int ySubimagen = y - altoSubimagen / 2;

            if (xSubimagen >= 0 && ySubimagen >= 0 && (xSubimagen + anchoSubimagen) <= imagenReconstruida.cols()
                    && (ySubimagen + altoSubimagen) <= imagenReconstruida.rows()) {
                Mat subimagen = new Mat(imagenReconstruida, new Rect(xSubimagen, ySubimagen, anchoSubimagen, altoSubimagen));
                nodo.getLlave().copyTo(subimagen);
            }
        }
    }

	// Método para guardar una matriz en un archivo de texto
	public static void saveMatrixToTextFile(String filePath, Mat matrix) {
		try (FileWriter writer = new FileWriter(filePath)) {
			int rows = matrix.rows();
			int cols = matrix.cols();

			// Iterar sobre cada píxel de la matriz
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					double[] pixel = matrix.get(i, j);
					// Escribir los valores de los píxeles en el archivo
					for (double value : pixel) {
						writer.write(value + "\t");
					}
					writer.write("\n"); // Nueva línea para cada fila de la matriz
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
