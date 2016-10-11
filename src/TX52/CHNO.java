package TX52;

import javafx.scene.paint.Color;

/**
 * Created by adah on 10/10/16.
 */
public class CHNO {
    private static CHNO instance = null;
    private String[] limitedAtomsSymbole = {"H", "C", "N", "O"};
    private short[] limitedAtomsNumber = {1, 6, 7, 8};
    private short[] limitedAtomsLiaison = {1, 4, 0, 2};
    private short[] limitedAtomsRayon = {5, 10, 1, 8};//TODO VERIFY
    private Color[] limitedAtomsColor = {Color.BLACK, Color.WHITE, Color.BLUE, Color.RED};

    private CHNO() {
    }


    public static CHNO getInstance() {
        if (instance == null) new CHNO();
        return instance;
    }


    public String[] getLimitedAtomsSymbole() {
        return limitedAtomsSymbole;
    }

    public void setLimitedAtomsSymbole(String[] limitedAtomsSymbole) {
        this.limitedAtomsSymbole = limitedAtomsSymbole;
    }

    public short[] getLimitedAtomsLiaison() {
        return limitedAtomsLiaison;
    }

    public void setLimitedAtomsLiaison(short[] limitedAtomsLiaison) {
        this.limitedAtomsLiaison = limitedAtomsLiaison;
    }

    public short[] getLimitedAtomsRayon() {
        return limitedAtomsRayon;
    }

    public void setLimitedAtomsRayon(short[] limitedAtomsRayon) {
        this.limitedAtomsRayon = limitedAtomsRayon;
    }

    public Color[] getLimitedAtomsColor() {
        return limitedAtomsColor;
    }

    public void setLimitedAtomsColor(Color[] limitedAtomsColor) {
        this.limitedAtomsColor = limitedAtomsColor;
    }

    public int getNumber(int a_number) {
        switch (a_number) {
            case 1:
                return 0;//H
            case 6:
                return 1;//C
            case 7:
                return 2;//N
            default:
                return 3;//O
        }
    }

    public int getANumber(int number) {
        if (number > 3) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return limitedAtomsNumber[number];
    }
}