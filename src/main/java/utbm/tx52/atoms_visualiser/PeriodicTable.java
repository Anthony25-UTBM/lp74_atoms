package utbm.tx52.atoms_visualiser;


import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by adah on 10/10/16.
 */
public class PeriodicTable implements IPeriodicTable {
    private static final Logger logger = LogManager.getLogger("PeriodicTable");
    private static PeriodicTable instance = null;
    private ArrayList<String> symbole;
    private ArrayList<Integer> liaisons;
    private ArrayList<Double> rayons;
    private ArrayList<String> group;
    private ArrayList<String> uniqGroup;
    private ArrayList<Integer> number;
    private Color[] couleurs = {Color.WHITE, Color.BLUE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK, Color.WHITE, Color.RED};

    private PeriodicTable() {
        symbole = new ArrayList<>();
        liaisons = new ArrayList<>();
        rayons = new ArrayList<>();
        group = new ArrayList<>();
        uniqGroup = new ArrayList<>();
        number = new ArrayList<>();
        parseJson();


    }

    public static PeriodicTable getInstance() {
        if (instance == null) instance = new PeriodicTable();
        return instance;
    }

    public static void setInstance(PeriodicTable instance) {
        PeriodicTable.instance = instance;
    }

    public ArrayList<Integer> getNumber() {
        return number;
    }

    public void setNumber(ArrayList<Integer> number) {
        this.number = number;
    }

    private void parseJson() {
        logger.debug("Reading json file");
        try {
            URL url = Atome.class.getClassLoader().getResource("periodicTable.json");
            logger.debug("Link : " + url.getPath());
            InputStream is = new FileInputStream(url.getPath());
            JsonReader reader = Json.createReader(is);
            JsonObject object = reader.readObject();
            JsonArray tables = object.getJsonArray("table");
            logger.debug("load data");

            int i = 0;

            for (Object table : tables) {
                JsonObject t = (JsonObject) table;
                JsonArray elementsArray = t.getJsonArray("elements");

                for (Object element : elementsArray) {

                    JsonObject e = (JsonObject) element;

                    if (e.getJsonString("name").getString().isEmpty()) continue;


                    JsonString symbol = e.getJsonString("small");
                    JsonString _group = e.getJsonString("group");
                    JsonNumber _number = e.getJsonNumber("number");
                    this.number.add(_number.intValue());
                    group.add(i, _group.getString());
                    String uniq_g = _group.getString();

                    if (!uniqGroup.contains(uniq_g) && !uniq_g.equals("")) {
                        uniqGroup.add(uniqGroup.size(), uniq_g);
                    }
                    symbole.add(i, symbol.getString());
                    JsonArray electrons = e.getJsonArray("electrons");
                    int nbCouche = electrons.size();
                    int nbSaturation = (electrons.size() * electrons.size()) * 2;

                    int liaison = nbSaturation - Integer.parseInt(electrons.get(nbCouche - 1).toString());

                    liaisons.add(i, liaison);

                    JsonValue rayon = e.get("molar");
                    rayons.add(i, Double.parseDouble(rayon.toString()));
                    logger.debug(" element : " + symbole.get(i) + " "
                            + "number " + number.get(i) + " "
                            + "electrons " + rayons.get(i) + " "
                            + " groupe  " + uniq_g
                            + " molar " + liaisons.get(i)
                    );
                    i++;
                }
            }

        } catch (Exception e) {
            logger.debug("Error reading JSON FILE " + e.getCause());

        }
    }

    public ArrayList<String> getSymbole() {
        return symbole;
    }

    public void setSymbole(ArrayList<String> symbole) {
        this.symbole = symbole;
    }

    public ArrayList<Integer> getLiaisons() {
        return liaisons;
    }

    public void setLiaisons(ArrayList<Integer> liaisons) {
        this.liaisons = liaisons;
    }

    public ArrayList<Double> getRayons() {
        return rayons;
    }

    public void setRayons(ArrayList<Double> rayons) {
        this.rayons = rayons;
    }

    public ArrayList<String> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<String> group) {
        this.group = group;
    }

    public ArrayList<String> getUniqGroup() {
        return uniqGroup;
    }

    public void setUniqGroup(ArrayList<String> uniqGroup) {
        this.uniqGroup = uniqGroup;
    }

    public Color[] getCouleurs() {
        return couleurs;
    }

    public void setCouleurs(Color[] couleurs) {
        this.couleurs = couleurs;
    }
}
