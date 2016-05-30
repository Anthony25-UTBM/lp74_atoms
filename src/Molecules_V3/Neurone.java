package Molecules_V3;

import java.util.*;

class VectorNeurone {
  private Vector<Neurone> unVecteur;

  public VectorNeurone() {
    unVecteur = new Vector<Neurone>();
  }
  public void ajouteUnNeuroneEnQueue(Neurone unNeurone) {
    unVecteur.addElement(unNeurone);
  }
  public void afficherLeNeuroneI(int i) {
    ((Neurone)unVecteur.elementAt(i)).donneActivation(); /* Casting malheureusement indispensable */
  }
  public void montreToutLeVecteur() {
    for (int i=0; i<unVecteur.size(); i++)
      ((Neurone)unVecteur.elementAt(i)).donneActivation();
  }
}


public class Neurone {
 private int activation;
 private int numero;

 public Neurone(int numero) {
  activation = 0;
  this.numero = numero;
 }

 public void donneActivation() {
  System.out.println("l’activation du neurone " + numero + " est " + activation);
}
 public void changeActivation(int activation)  {
  this.activation = activation;
 }
}

class TestGenericite {
	
         public static void main(String[] args) {
           Vector<Neurone> mesNeurones = new Vector<Neurone>(); /* attention, nouveau chez Java
              depuis la version 1.5 */
           mesNeurones.addElement(new Neurone(1)); 
           mesNeurones.elementAt(0).changeActivation(2); // plus de casting !
           mesNeurones.elementAt(0).donneActivation();
	      }
}

