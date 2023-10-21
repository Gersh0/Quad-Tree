package code;

import java.io.FileWriter;
import java.io.IOException;

import org.opencv.core.*;

public class QuadTree {

	protected NodoQ raiz;
	protected Size imageSize;
	protected int imageType;
	protected boolean wasExtended;
	int filasNuevas;
	int columnasNuevas;

	public QuadTree(NodoQ r) {
		this.raiz = r;
	}

	public NodoQ getRaiz() {
		return raiz;
	}
	
	/* Se puede hacer así y también con Case.
	 public boolean canDivide(Mat image, String quadrant) {
	    if (image.cols() <= 1 || image.rows() <= 1) {
	        return false;
	    }
	    
	    boolean changeFound = false;
	    int tempRowStart = 0;
	    int tempRowEnd = (image.rows() / 2);
	    int tempColStart = 0;
	    int tempColEnd = (image.cols() / 2);

	    if (quadrant.equals("NE")) {
	        tempRowStart = 0;
	        tempRowEnd = (image.rows() / 2);
	        tempColStart = image.cols() / 2;
	        tempColEnd = image.cols();
	    } else if (quadrant.equals("NW")) {
	        tempRowStart = 0;
	        tempRowEnd = (image.rows() / 2);
	        tempColStart = 0;
	        tempColEnd = (image.cols() / 2);
	    } else if (quadrant.equals("SW")) {
	        tempRowStart = image.rows() / 2;
	        tempRowEnd = image.rows();
	        tempColStart = 0;
	        tempColEnd = (image.cols() / 2);
	    } else if (quadrant.equals("SE")) {
	        tempRowStart = image.rows() / 2;
	        tempRowEnd = image.rows();
	        tempColStart = image.cols() / 2;
	        tempColEnd = image.cols();
	    }

	    double[] tempColor = image.get(tempRowStart, tempColStart);
	    
	    for (int tempRow = tempRowStart; tempRow < tempRowEnd && !changeFound; tempRow++) {
	        for (int tempCol = tempColStart; tempCol < tempColEnd && !changeFound; tempCol++) {
	            double[] pixel = image.get(tempRow, tempCol);
	            if (pixel != null && tempColor[0] != pixel[0]) {
	                changeFound = true;
	            }
	        }
	    }
	    
	    return changeFound;
	}*/

	
	//se itera por la matriz, si se encuentra un pixel distinto al que esta en el punto 0,0 , devuelve true.
	// Itera en el sector Noreste
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
	//se itera por la matriz, si se encuentra un pixel distinto al que esta en el punto 0,0 , devuelve true.
		// Itera en el sector Noroeste
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
	//se itera por la matriz, si se encuentra un pixel distinto al que esta en el punto 0,0 , devuelve true.
		// Itera en el sector Suroeste
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
	//se itera por la matriz, si se encuentra un pixel distinto al que esta en el punto 0,0 , devuelve true.
		// Itera en el sector Sureste
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
		double log2Columnas = (Math.log10(image.width()) / Math.log10(2));
		double log2Filas = (Math.log10(image.height()) / Math.log10(2));

		// si el log es un numero entero, sera 2^n, si no, se aproxima al numero 2^n mas cercano hacia arriba
		// se hace con ambas las filas y las columnas
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

		// si son iguales a 0, significara que ya estaban en orden 2^n, por lo que no hay que extender la imagen
		if (columnasNuevas != 0 || filasNuevas != 0) {
			//se pega la matriz original a una con tamaño orden 2^n
			this.wasExtended = true;
			Mat temp = new Mat(filasNuevas, columnasNuevas, image.type());
			//temp es la matriz expandida, vacia
			Mat subMat = new Mat(temp, new Rect(0, 0, image.cols(), image.rows()));
			// se crea una submatriz de temp, para poder pegar la original, de manera que los pixeles 0,0 de 
			// ambas imagenes esten en la misma posicion
			image.copyTo(subMat);
			// se copia una en la otra
			generarArbol(temp, this.raiz);
			//se usa el metodo del arbol en la imagen expandida, que ahora tiene la original
		} else {
			generarArbol(image, this.raiz);
		}
	}

	public void generarArbol(Mat image, NodoQ nodoActual) {
		boolean canDivideNE = canDivideNE(image);
		boolean canDivideNW = canDivideNW(image);
		boolean canDivideSW = canDivideSW(image);
		boolean canDivideSE = canDivideSE(image);
		// se comprueban las cuatro regiones por separado, antes de dividir. Esto ayuda a optimizar el arbol, ya que
		// si se encuentra un pixel diferente en algun cuadrante, igual se dividira, pero si el cuadrante no tiene cambios,
		// y habia otro cuadrante por el que hay que dividir, el cuadrante que esta perfecto se guarda directamente, sin tener
		// que volver a hacer la comprobación.
		
		
		if (!canDivideNE && !canDivideNW && !canDivideSE && !canDivideSW) {//&(Llegué al fondo || #GUI)

			if( (image.get(0,0) != image.get(image.width()/2+1,0)) || (image.get(image.width()/2+1,0) != image.get(image.width()/2+1,image.height()/2+1)) ||
			(image.get(image.width()/2+1,image.height()/2+1) != image.get(0, image.height()/2+1)) || (image.get(0, image.height()/2+1) != image.get(0,0))){

				// como ya se comprobo que todos los cuadrantes son de un color cada uno, hay que confirmar si tienen o no el mismo color entre ellos.
				// si hay alguna con color distinto a las otras, se dividira.
				Mat NW = new Mat(image, new Range(0, image.rows() / 2), new Range(0, image.cols() / 2));
				Mat NE = new Mat(image, new Range(0, image.rows() / 2), new Range(image.cols() / 2, image.cols()));
				Mat SW = new Mat(image, new Range(image.rows() / 2, image.rows()), new Range(0, image.cols() / 2));
				Mat SE = new Mat(image, new Range(image.rows() / 2, image.rows()), new Range(image.cols() / 2, image.cols()));
				NodoQ nodoNW = new NodoQ(NW, nodoActual);
				NodoQ nodoNE = new NodoQ(NE, nodoActual);
				NodoQ nodoSW = new NodoQ(SW, nodoActual);
				NodoQ nodoSE = new NodoQ(SE, nodoActual);
				nodoActual.setHijoNW(nodoNW);
				nodoActual.setHijoNE(nodoNE);
				nodoActual.setHijoSW(nodoSW);
				nodoActual.setHijoSE(nodoSE);
			}
			
			return;
		} else {
			//Se crean submatrices de la original, dividiendola por cuadrantes.
			Mat NW = new Mat(image, new Range(0, image.rows() / 2), new Range(0, image.cols() / 2));
			Mat NE = new Mat(image, new Range(0, image.rows() / 2), new Range(image.cols() / 2, image.cols()));
			Mat SW = new Mat(image, new Range(image.rows() / 2, image.rows()), new Range(0, image.cols() / 2));
			Mat SE = new Mat(image, new Range(image.rows() / 2, image.rows()), new Range(image.cols() / 2, image.cols()));
			
			// se crean nodos nuevos, y se les guardan las submatrices, ademas de guardar el nodo actual como padre.
			// luego se le ponen los nodos como hijos al actual.
			NodoQ nodoNW = new NodoQ(NW, nodoActual);
			NodoQ nodoNE = new NodoQ(NE, nodoActual);
			NodoQ nodoSW = new NodoQ(SW, nodoActual);
			NodoQ nodoSE = new NodoQ(SE, nodoActual);
			nodoActual.setHijoNW(nodoNW);
			nodoActual.setHijoNE(nodoNE);
			nodoActual.setHijoSW(nodoSW);
			nodoActual.setHijoSE(nodoSE);
			// IMPORTANTE: los 4 metodos de sethijo, borraran la llave del padre, si la tiene. Esto es porque solo se quiere
			// guardar informacion en las hojas. Como tiene hijos sera nodo interno, asi que hay que eliminar la matriz
			// que tiene guardada para ahorrar memoria, ya que nunca se va a utilizar.

			// esto es para que si el cuadrante en especifico no tiene ningun cambio, no se sigue por ahi, guardandolo.
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
	    	   
	    	   // en ambos de los casos abajo, se crea una imagen temporal del tamaño de la imagen a reconstruir, 
	    	   // si es una imagen expandida, se cuenta el tamaño extra.
	    	   
	    	   // Luego, se reconstruye. Si la imagen es expandida, antes de devolver la imagen, saca una submatriz del tamaño de la
	    	   // imagen original, desde el pizel 0,0 , a el final de la imagen
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
	       }
	    }

	private void reconstruirImagen(NodoQ nodo, Mat imagenReconstruida, int inicioX, int inicioY, int ancho, int alto) {
        if (nodo == null) {
            return;
        }

        int x = inicioX + ancho/2;
        int y = inicioY + alto/2;

        // se reconstruye por cuadrantes.
        
        // los puntos de inicio, y las coordenadas cambian segun el cuadrante.
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
	/*
	// intento de Método para guardar una matriz en un archivo de texto
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
	*/

}
