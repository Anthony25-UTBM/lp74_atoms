package utbm.tx52.atoms_visualiser;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by adah on 18/10/16.
 */

public interface IPeriodicTable {
    ArrayList<String> getSymbole();

    ArrayList<Integer> getLiaisons();

    ArrayList<Double> getRayons();

    ArrayList<String> getGroup();


    ArrayList<String> getUniqGroup();


    Color[] getCouleurs();

}
