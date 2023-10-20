package code;
import org.opencv.core.*;


public class QuadTreeSegmented {

	protected int depth;
	protected NodoQ raiz;
	protected Size imageSize;
	protected int imageType;
	
	public QuadTreeSegmented(NodoQ r/*, int depth*/) {
		this.raiz = r;
		/*this.depth = depth;*/
	}

	public NodoQ getRaiz() {
		return raiz;
	}
	
	public boolean canDivideNE(Mat image) {
		if(image.cols() <= 1 || image.rows() <= 1) {return false;}
		boolean changeFound = false; 
		int tempRow = 0;
		double[] tempColor = image.get(0,0);
		while(++tempRow <= (image.rows()/2) && !changeFound) {
			int tempCol = image.cols()/2+1;
			while(++tempCol <= image.cols() && !changeFound  && image.get(tempRow, tempCol)!=null) {
				if(tempColor[0] != image.get(tempRow, tempCol)[0]) changeFound = true;
			}
		}
		return changeFound;
	}
	
	public boolean canDivideNW(Mat image) {
		if(image.cols() <= 1 || image.rows() <= 1) {return false;}
		boolean changeFound = false;
		int tempRow = 0;
		double[] tempColor = image.get(0,0);
		while(++tempRow <= (image.rows()/2) && !changeFound) {
			int tempCol=0;
			while(++tempCol <= (image.cols()/2) && !changeFound  && image.get(tempRow, tempCol)!=null) {
				if(tempColor[0] != image.get(tempRow, tempCol)[0]) changeFound = true;
			}
		}
		return changeFound;
	}
	
	public boolean canDivideSW(Mat image) {
		if(image.cols() <= 1 || image.rows() <= 1) {return false;}
		boolean changeFound = false;
		int tempRow = image.rows()/2+1;
		double[] tempColor = image.get(0,0);
		while(++tempRow <= image.rows() && !changeFound) {
			int tempCol=0;
			while(++tempCol <= (image.cols()/2) && !changeFound  && image.get(tempRow, tempCol)!=null) {
				if(tempColor[0] != image.get(tempRow, tempCol)[0]) changeFound = true;
			}
		}
		return changeFound;
	}
	
	public boolean canDivideSE(Mat image) {
		if(image.cols() <= 1 || image.rows() <= 1) {return false;}
		boolean changeFound = false;
		int tempRow = image.rows()/2+1;
		double[] tempColor = image.get(0,0);
		while(++tempRow <= image.rows() && !changeFound) {
			int tempCol=image.cols()/2+1;
			while(++tempCol <= image.cols() && !changeFound && image.get(tempRow, tempCol)!=null) {
				if(tempColor[0] != image.get(tempRow, tempCol)[0]) changeFound = true;
			}
		}
		return changeFound;
	}
	
	public void generarArbol(Mat image) {
		
		//Se comprueba si la imagen ya está en ancho o alto de 4^n
		
		
		
		
		this.imageType = image.type();
		this.imageSize = image.size();
		generarArbol(image, this.raiz/*, 0*/);
	}
	
	public void generarArbol(Mat image, NodoQ nodoActual/*, int depth*/) {

			boolean canDivideNE = canDivideNE(image);
			boolean canDivideNW = canDivideNW(image);
			boolean canDivideSW = canDivideSW(image);
			boolean canDivideSE = canDivideSE(image);
			if(!canDivideNE && !canDivideNW && !canDivideSE && !canDivideSW) {
				return;
			} else {
				Mat NW = new Mat(image, new Range(0, image.rows()/2), new Range(0, image.cols()/2));
				Mat NE = new Mat(image, new Range(0, image.rows()/2), new Range(image.cols()/2+1, image.cols()));
				Mat SW = new Mat(image, new Range(image.rows()/2+1, image.rows()), new Range(0, image.cols()/2));
				Mat SE = new Mat(image, new Range(image.rows()/2+1, image.rows()), new Range(image.cols()/2+1, image.cols()));
				NodoQ nodoNW = new NodoQ(NW, nodoActual);
				NodoQ nodoNE = new NodoQ(NE, nodoActual);
				NodoQ nodoSW = new NodoQ(SW, nodoActual);
				NodoQ nodoSE = new NodoQ(SE, nodoActual);
				nodoActual.setHijoNW(nodoNW);
				nodoActual.setHijoNE(nodoNE);
				nodoActual.setHijoSW(nodoSW);
				nodoActual.setHijoSE(nodoSE);
				
				if(canDivideNE /*&& depth<=this.depth*/) {generarArbol(NE, nodoNE/*, depth*/);}
				if(canDivideNW /*&& depth<=this.depth*/) {generarArbol(NW, nodoNW/*, depth*/);}
				if(canDivideSW /*&& depth<=this.depth*/) {generarArbol(SW, nodoSW/*, depth*/);}
				if(canDivideSE /*&& depth<=this.depth*/) {generarArbol(SE, nodoSE/*, depth*/);}
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
    	   Mat imagenReconstruida = new Mat(this.imageSize, this.imageType);
           reconstruirImagen(this.getRaiz(), imagenReconstruida, 0, 0, (int)this.imageSize.width, (int)this.imageSize.height);
           return imagenReconstruida;
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
	
}
