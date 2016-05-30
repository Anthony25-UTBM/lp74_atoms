//package Molecules;

import javax.swing.JFrame;

//Fenetre principale de l'application (et lancement)
public class ApplicationMolecules_2 {
 public static void main(String[] args) {
     // Création de la fenêtre
     JFrame fenetre = new JFrame();
     fenetre.setTitle("Simulation de formation de molécules");
     fenetre.setSize(1200, 700);
     fenetre.setLocationRelativeTo(null);
     fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     fenetre.setResizable(false);
     // Création du contenu
     MoleculesJPanel_2 panel = new MoleculesJPanel_2();
     fenetre.setContentPane(panel);
     // Affichage
     fenetre.setVisible(true);
     panel.Lancer();
 }
}
