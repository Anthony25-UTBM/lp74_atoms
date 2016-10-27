package utbm.tx52.atoms_visualiser;


public interface IAGroup {

    void setTranslate(double x, double y, double z);

    void setTranslate(double x, double y);

    void setTx(double x);

    void setTy(double y);

    void setTz(double z);

    void setRotate(double x, double y, double z);

    void setRotateX(double x);

    void setRotateY(double y);

    void setRotateZ(double z);

    void setRx(double x);

    void setRy(double y);

    void setRz(double z);

    void setScale(double scaleFactor);

    void setScale(double x, double y, double z);

    void setSx(double x);

    void setSy(double y);

    void setSz(double z);

    void setPivot(double x, double y, double z);

    void reset();

    void resetTSP();

    String toString();
}
