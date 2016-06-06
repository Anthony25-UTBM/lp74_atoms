package Molecules_V4;


public interface IAGroup {
	
	public void setTranslate(double x, double y, double z); 
	public void setTranslate(double x, double y); 
	public void setTx(double x); 
	public void setTy(double y); 
	public void setTz(double z); 
	
	public void setRotate(double x, double y, double z); 
	
	public void setRotateX(double x);
	public void setRotateY(double y); 
	public void setRotateZ(double z);
	public void setRx(double x); 
	public void setRy(double y); 
	public void setRz(double z);
	
	public void setScale(double scaleFactor); 
	
	public void setScale(double x, double y, double z);
	
	public void setSx(double x); 
	public void setSy(double y);
	public void setSz(double z);
	
	public void setPivot(double x, double y, double z); 
	
	public void reset();
	
	public void resetTSP(); 
	public String toString();
}
