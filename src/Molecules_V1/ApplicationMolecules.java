package Molecules_V1;
//package Molecule;

import javax.swing.JFrame;

//Fenetre principale de l'application (et lancement)
public class ApplicationMolecules {
 public static void main(String[] args) {
     // Création de la fenêtre
     JFrame fenetre = new JFrame();
     fenetre.setTitle("Simulation molécules");
     fenetre.setSize(1200, 700);
     fenetre.setLocationRelativeTo(null);
     fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     fenetre.setResizable(false);
     // Création du contenu
     MoleculesJPanel panel = new MoleculesJPanel();
     fenetre.setContentPane(panel);
     // Affichage
     fenetre.setVisible(true);
     panel.Lancer();
 }
}
