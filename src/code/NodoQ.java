package code;

import org.opencv.core.*;

public class NodoQ {
	
	protected Mat llave;
	
	protected NodoQ hijoNE;
	protected NodoQ hijoNW;
	protected NodoQ hijoSW;
	protected NodoQ hijoSE;
	
	protected NodoQ padre;
	
	public NodoQ(Mat llave, NodoQ padre) {
		this.llave=llave;
		this.padre=padre;
		hijoNW=null;
		hijoNE=null;
		hijoSW=null;
		hijoSE=null;
	}
	
	public NodoQ() {
		this.padre = null;
		this.llave = null;
		hijoNW=null;
		hijoNE=null;
		hijoSW=null;
		hijoSE=null;
	}

	public NodoQ(Mat llave, NodoQ hijoNE, NodoQ hijoNW, NodoQ hijoSW, NodoQ hijoSE,
			NodoQ padre) {
		super();
		this.llave = llave;
		this.hijoNE = hijoNE;
		this.hijoNW = hijoNW;
		this.hijoSW = hijoSW;
		this.hijoSE = hijoSE;
		this.padre = padre;
	}
	
	
	public NodoQ getPadre() {
		return padre;
	}	
	
	public void setPadre(NodoQ padre) {
		this.padre = padre;
	}

	public NodoQ getHijoNE() {
		return hijoNE;
	}

	public void setHijoNE(NodoQ hijoNE) {
		if(this.llave != null) {
			this.llave = null;
		}
		this.hijoNE = hijoNE;
	}

	public NodoQ getHijoNW() {
		return hijoNW;
	}

	public void setHijoNW(NodoQ hijoNW) {
		if(this.llave != null) {
			this.llave = null;
		}
		this.hijoNW = hijoNW;
	}

	public NodoQ getHijoSW() {
		return hijoSW;
	}

	public void setHijoSW(NodoQ hijoSW) {
		if(this.llave != null) {
			this.llave = null;
		}
		this.hijoSW = hijoSW;
	}

	public NodoQ getHijoSE() {
		return hijoSE;
	}

	public void setHijoSE(NodoQ hijoSE) {
		if(this.llave != null) {
			this.llave = null;
		}
		this.hijoSE = hijoSE;
	}
	
	public Mat getLlave() {
		return llave;
	}

	public void setLlave(Mat llave) {
		this.llave = llave;
	}
	
	public boolean hasHijos() {
		return (this.hijoNE != null && this.hijoNW != null && this.hijoSE != null && this.hijoSW != null);
	}
	
	
}
