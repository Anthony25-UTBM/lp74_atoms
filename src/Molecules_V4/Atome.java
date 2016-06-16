package Molecules_V4;


import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.json.*;
import java.awt.*;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;


// Agent Atome
public class Atome extends Agent {
    // Constantes
    public static final double PAS = 3;
    public static final double DISTANCE_MIN = 10;
    public static final double DISTANCE_MIN_CARRE = 100;
    public static final double DISTANCE_MAX = 40;
    public static final double DISTANCE_MAX_CARRE = 1600;

    // nouvelle architecture proposé pr aymen

    protected static ArrayList<String> m_symbole = new ArrayList<String>();
    protected static ArrayList<Integer> m_liaisons = new ArrayList<Integer>();
    protected static ArrayList<Double> m_rayons = new ArrayList<Double>();
    protected static ArrayList<String> m_group = new ArrayList<String>();
    protected static ArrayList<String> m_uniqGroup = new ArrayList<String>();
    // Attributs communs aux atomes
    protected String symboles[] =
            {"", "H", "HE", "LI", "BE", "B", "C", "N", "O"};
    protected int    liaisons [] = {0,1,0,0,0,0,4,0,2};
    //protected double rayons   [] = {0,2.5,0,0,0,0,7,0,6}; // div par 10
    protected double rayons   [] = {0,5,0,0,0,0,10,0,8}; // div par 10
    protected Color  couleurs [] = {Color.WHITE, Color.BLUE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK, Color.WHITE, Color.RED};
    // Attributs de l'atome
    protected int a_number;		//1 -> H;  6 -> C;  8 -> 0
    protected String symb;
    protected int    etat;		//0 -> libre;    1 -> partiellement li�;	2 -> li� (stable)
    protected int    liaison;
    protected double rayon;
    protected Color couleur;
    //attributs simulation
    protected double vitesseX;
    protected double vitesseY;
    protected double vitesseZ;
    protected Random generateur;
    protected int tempsRestant = 500;
    private ASphere sphere;
    public Atome(int _n, double _x, double _y,double _z, double _dir) {
    	a_number = _n;
        symb = m_symbole.get(a_number);
        etat = 0;
        liaison = m_liaisons.get(a_number);
        rayon = m_rayons.get(a_number);
        couleur = couleurs[a_number % 9];
        posX = _x;
        posY = _y;
        posZ = _z;
        vitesseX = Math.cos(_dir);
        vitesseY = Math.sin(_dir);
        vitesseZ = 0;
        double [] pos = {posX,posY,posZ};
        double [] colors = {couleur.getRed()/255,couleur.getGreen()/255,couleur.getBlue()/255};
        sphere = new ASphere(rayon,pos,colors);


        System.out.println("Atome cr�� ("+symb+")");
    }

    public static void parseJson() {
        System.out.println("Reading json file");
        try {
            URL url = Atome.class.getClassLoader().getResource("periodicTable.json");
            InputStream is = new FileInputStream(url.getPath());
            JsonReader reader = Json.createReader(is);
            JsonObject object = reader.readObject();
            JsonArray tables = object.getJsonArray("table");


            int i = 0;

            for (Object table : tables) {
                JsonObject t = (JsonObject) table;
                JsonArray elementsArray = t.getJsonArray("elements");

                for (Object element : elementsArray) {

                    JsonObject e = (JsonObject) element;
                    JsonString symbol = e.getJsonString("small");
                    JsonString group  = e.getJsonString("group");
                    m_group.add(i,group.getString());
                    String uniq_g = group.getString();

                    if(!m_uniqGroup.contains(uniq_g) && !uniq_g.equals(""))
                    {
                        m_uniqGroup.add(m_uniqGroup.size(),uniq_g);
                    }
                    m_symbole.add(i, symbol.getString());
                    JsonArray electrons = e.getJsonArray("electrons");
                    int nbCouche = electrons.size();
                    int nbSaturation = (electrons.size() * electrons.size()) * 2;

                    int liaison = nbSaturation - Integer.parseInt(electrons.get(nbCouche - 1).toString());

                    m_liaisons.add(i, liaison);

                    JsonValue rayon = e.get("molar");
                    m_rayons.add(i, Double.parseDouble(rayon.toString()));
                    System.out.println(" element : " + m_symbole.get(i) + " "
                            + "electrons " + m_rayons.get(i) + " "
                            + " molar " + m_liaisons.get(i)
                    );
                    i++;
                }
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }
    
    public double getRayon() {
    	return rayon;
    }

    public String getSymb() {
        return symb;
    }

    //TODO: exploiter cette m�thode
    public int estlibre() {
    	return liaison;
    }

    //
    // mouvement des atomes
    //
    public double getVitesseX() {
        return vitesseX;
    }
    
    public double getVitesseY() {
        return vitesseY;
    }
    
    public double getVitesseZ()
    {
    	return vitesseZ;
    }

    
    protected void Normaliser() {
        double longueur = Math.sqrt(vitesseX * vitesseX + vitesseY * vitesseY + vitesseZ*vitesseZ);
        vitesseX /= longueur;
        vitesseY /= longueur;
        vitesseZ /= longueur;
    }
    
    protected void MiseAJourPosition() {
        posX += PAS * vitesseX;
        posY += PAS * vitesseY;
        posZ += PAS * vitesseZ;
    }

    protected double DistanceLimiteEnv(double envXMin, double envYMin,double envZMin, double envXMax, double envYMax,double envZMax) {
        double min = Math.min(posX - envXMin, Math.min(posY - envYMin, posZ - envZMin));
        min = Math.min(min, envXMax - posX);
        min = Math.min(min, envYMax - posY);
        min = Math.min(min, envZMax - posZ);
        return min;
    }
    
    //TODO: Interet de cette methode !!    
    protected boolean DansAlignement(Atome a) {
        double distanceCarre = DistanceCarre(a);
        return (distanceCarre < DISTANCE_MAX_CARRE && distanceCarre > DISTANCE_MIN_CARRE);
    }
    
    //TODO: URGENT
    protected boolean LierAtomes(Atome[] atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        double distanceCarre = DistanceCarre(a);
        for (Atome atome : atomes) {
            if (DistanceCarre(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = DistanceCarre(a);
            }
        }
        
        // Liaison possible ?
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * 4) && a_number != a.a_number) {
            /*double distance = Math.sqrt(distanceCarre);
            double diffX = (a.posX - posX) / distance;
            double diffY = (a.posY - posY) / distance;
            vitesseX = vitesseX - diffX / 2;
            vitesseY = vitesseY - diffY / 2;
            Normaliser();*/
        	if (a.liaison != 0) {
        		System.out.println("Une liaison avec Distance Carre = "+distanceCarre);
        	    liaison --; a.liaison--; 
        		etat = 1; a.etat = 1; 
        		return true;
        	}
        }
        return false;
    }    
    
    protected boolean EviterAtomes(Atome[] atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        generateur = new Random();
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        double distanceCarre = DistanceCarre(a);
        for (Atome atome : atomes) {
            if (DistanceCarre(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = DistanceCarre(a);
            }
        }
        
        // Evitement
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * a.rayon)) {
            double distance = Math.sqrt(distanceCarre);
            double diffX = (a.posX - posX) / distance;
            double diffY = (a.posY - posY) / distance;
            double diffZ = (a.posZ - posZ) / distance;
            double alea = generateur.nextDouble() * 4;
            vitesseX = vitesseX - diffX / alea;
            vitesseY = vitesseY - diffY / alea;
            vitesseZ = vitesseZ - diffZ / alea;
            //System.out.println("Atome �vit�!!");
            Normaliser();
            return true;
        }
        return false;
    }

    
    protected boolean EviterLimiteEnv(double envXMin, double envYMin, double envZMin, double envXMax, double envYMax, double envZMax) {
        // On s'arrete aux limites de l'environnement
        if (posX < envXMin) {
            posX = envXMin;
        }
        else if (posY < envYMin) {
            posY = envYMin;
        }
        else if (posX > envXMax) {
            posX = envXMax;
        }
        else if (posY > envYMax) {
            posY = envYMax;
        }
        else if (posZ < envZMin)
        {
        	posZ = envZMin;
        }
        else if (posZ > envZMax)
        {
        	posZ = envZMax;
        }
        
        // Changer de direction
        double distance = DistanceLimiteEnv(envXMin, envYMin,envZMin, envXMax, envYMax,envZMax);
        if (distance < DISTANCE_MIN) {
            if (distance == (posX - envXMin)) {
                vitesseX += 0.3;
            }
            else if (distance == (posY - envYMin)) { 
                vitesseY += 0.3; 
            } 
            else if (distance == (envXMax - posX)) {
                vitesseX -= 0.3;
            } 
            else if (distance == (envYMax - posY)) {
                vitesseY -= 0.3;
            }  
            else if (distance == (envZMax - posZ)) 
            {
            	vitesseZ -= 0.3;
            }
            else if (distance == (posZ - envZMin))
            {
            	vitesseZ += 0.3;
            }
            Normaliser();
            return true;
        }
        return false;
    }

    protected boolean EviterMolecule(ArrayList<Molecule> molecules) {
        if (!molecules.isEmpty()) {
            // Recherche de la mol�cule la plus proche
        	Molecule m = molecules.get(0);
            double distanceCarre = DistanceCarre(m);
            for (Molecule m_2 : molecules) {
                if (DistanceCarre(m) < distanceCarre) {
                    m = m_2;
                    distanceCarre = DistanceCarre(m_2);
                }
            }
            
            if (distanceCarre < (m.rayon * m.rayon * 4)) {
                // Si collision, calcul du vecteur diff
                double distance = Math.sqrt(distanceCarre);
                double diffX = (m.posX - posX) / distance;
                double diffY = (m.posY - posY) / distance;
                double diffZ = (m.posZ - posZ) / distance;
                vitesseX = vitesseX - diffX / 2;
                vitesseY = vitesseY - diffY / 2;
                vitesseZ = vitesseZ - diffZ / 2;
                Normaliser();
                return true;
            }
        }
        return false;        
    }
    
    
    
    protected void CalculerDirectionMoyenne(Atome[] atomes) {
        double vitesseXTotal = 0;
        double vitesseYTotal = 0;
        double vitesseZTotal = 0;
        int nbTotal = 0;
        for (Atome a : atomes) {
            if (DansAlignement(a)) {
                vitesseXTotal += a.vitesseX;
                vitesseYTotal += a.vitesseY;
                vitesseZTotal += a.vitesseZ;
                nbTotal++;
            }
        }
        if (nbTotal >= 1) {
            vitesseX = (vitesseXTotal / nbTotal + vitesseX) / 2;
            vitesseY = (vitesseYTotal / nbTotal + vitesseY) / 2;
            vitesseZ = (vitesseZTotal / nbTotal + vitesseZ) / 2;
            Normaliser();
        }
    }

    
    public void MiseAJour(Atome[] atomes, ArrayList<Molecule> molecules, double largeur, double hauteur,double profondeur) {
    	tempsRestant--;
    	if (etat == 0) {
    		if (!EviterLimiteEnv(0,0,0,largeur,hauteur,profondeur)) {
    			if (!LierAtomes(atomes)) {
    				if (!EviterMolecule(molecules)) {
    					if (!EviterAtomes(atomes)) {
    						CalculerDirectionMoyenne(atomes);
    					}
    				}
    			}
    		}
    		if (liaison == 0) etat = 2;
    		MiseAJourPosition();
        }
		if (liaison == 0) etat = 2;
    }
    
    protected void updatePosition()
    {
    	double [] pos = {posX,posY,posZ};
    	sphere.setT(pos);
    }

    protected Rectangle m_rect;
    protected Tooltip   m_tooltip;
    protected void draw(AGroup root)
    {
    	updatePosition();
    	if(!root.getChildren().contains(sphere))
    	{

            sphere.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("clicked !");
                    m_tooltip = new Tooltip("Atoms !");

                    Tooltip.install(sphere,m_tooltip);

                }
            });
    		root.getChildren().add(sphere);
    	}
    }

}


    
