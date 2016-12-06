package utbm.tx52.atoms_visualiser.utils;

import utbm.tx52.atoms_visualiser.entities.CHNO;

public class IPeriodicTableFactory {
    private boolean isCHNO;

    private IPeriodicTableFactory() {
    }

    public IPeriodicTableFactory(boolean isCHNO) {
        this.isCHNO = isCHNO;
    }

    public IPeriodicTable getInstance() {
        if (isCHNO) return CHNO.getInstance();
        return PeriodicTable.getInstance();
    }

    public void setIsCHNO(boolean isCHNO) {
        this.isCHNO = isCHNO;
    }

}
