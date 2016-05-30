package Molecules_V3;

//import java.io.*;
//import java.lang.*;
//import java.util.*;

// Classe Liste
class Liste {

  int contenu;
  Liste suivant;

  Liste (int x, Liste a) {
      contenu = x;
      suivant = a;
  }

  static Liste ajouter (int x, Liste a) {

    return new Liste (x, a);
  }

  static boolean recherche(int x, Liste a) {
  // \esc{Recherche, voir page \pageref{prog:recherche-liste}} 

    while (a != null) {
        if (a.contenu == x)
            return true;
        a = a.suivant;
    }
    return false;
  }

  static boolean rechercheR (int x, Liste a) {
  // \esc{recherche re'cursive, voir page \pageref{prog:recherche-liste-rec}}

    if (a == null) 
        return false;
    else if (a.contenu == x) 
        return true;       
    else 
        return rechercheR (x, a.suivant);
  }

  static boolean rechercheRbis (int x, Liste a) {
  // \esc{recherche re'cursive, voir page \pageref{prog:recherche-liste-rec}}

    return (a != null)
        && ((a.contenu == x)
            || rechercheRbis (x, a.suivant));
  }

   static int longueur(Liste a) {
   // \esc{Longueur d'une liste, voir page \pageref{prog:longueur-liste-rec}}

    if (a == null)
        return 0;
    else
        return 1 + longueur (a.suivant);
  }

  static int longueurI(Liste a) {
   // \esc{Longueur d'une liste, voir page \pageref{prog:longueur-liste}}

    int   longueur = 0;
    while (a != null) {
        ++longueur;
        a = a.suivant;
    }
    return longueur;
  } 

  static Liste supprimer (int x, Liste a) {
  // \esc{Supprimer, voir page \pageref{prog:supprimer-liste-recursif}} 

    if (a != null)
        if (a.contenu == x)
            a = a.suivant;
        else 
         a.suivant  = supprimer (x, a.suivant);
    return a;      
  }

  static Liste supprimerI (int x, Liste a) { 
    Liste   b;

    if (a != null) {
        if (a.contenu == x){
            a = a.suivant;
        } else {
            b = a ;
            while (b.suivant != null && b.suivant.contenu != x) 
                b = b.suivant;
            if (b.suivant != null) { 
                b.suivant = b.suivant.suivant;
            }
        } 
    }
    return a;       
  }

  static Liste listePremier (int n) {
   //\esc{Liste des nombres premiers, voir page \pageref{prog:liste-premiers}}

    Liste  a = null;
    int    k;

    for (int i = n; i >= 2; --i) {
         a = ajouter (i, a);
    } 
    k = a.contenu; 
    for (Liste b = a; k * k <= n ; b = b.suivant){ 
        k = b.contenu;   
        for (int j = k; j <= n/k; ++j) 
            a = supprimer (j * k, a);
        }
    return(a); 
  }

  static void imprimer (Liste a) {

    for (Liste b = a; b != null; b = b.suivant) 
        System.out.print (b.contenu + " ");
    System.out.println ();
  }
}

// Classe Pile
class ExceptionPile extends Exception {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
String nom;

  public ExceptionPile (String x) {
      nom = x;
  }
}

class Pile {

  final static int maxP = 10;

  int          hauteur ;
  int          contenu[];

  Pile ()  {
     hauteur = 0;
     contenu = new int[maxP];
  }

  static void faireVide (Pile p) {
    p.hauteur = 0;
  }

  static boolean estVide(Pile p) {
    return p.hauteur == 0;
  }

  static boolean estPleine(Pile p) {
    return p.hauteur == maxP;
  }

  static void ajouter (int x, Pile p) 
  {
    p.contenu[p.hauteur] = x;
    ++ p.hauteur;
  }

  static int  valeur(Pile p) 
  {
    return p.contenu [p.hauteur - 1];
   }

  static void supprimer(Pile p) 
  {
    -- p.hauteur;
  }
}


// Graphes  représentés par une matrice
class GrapheMat {

  int m[][];
  int nb;

  static void imprimer (GrapheMat g) { 

      System.out.println ("nombre de sommets " + g.nb);
      for (int i = 0; i < g.nb ; ++i) {
          for (int j = 0; j < g.nb; ++j) 
              System.out.print (g.m[i][j] + " ");
         System.out.println ();
      } 
      System.out.println (); 
  }

  GrapheMat (int n1) {
  // Initialisation d'un graphe de n1 sommets sous forme de matrice vide

      nb = n1;
      m = new int[n1][n1]; 
  }
 
  GrapheMat (GrapheSucc h) {
  // Transformation en matrice d'adjacence
  //  d'un graphe de n1 sommets donne comme une matrice de successeurs

      nb = h.nb;
      m= new int[nb][nb]; 
      for (int i = 0; i < nb ; ++i)
         for (int j = 0; j < nb ; ++j) m[i][j] = 0; 
      for (int i = 0; i < nb ; ++i)
          for (int k = 0; h.succ[i][k] != GrapheSucc.Omega; ++k)
              m[i][h.succ[i][k]] = 1; 
  }

  GrapheMat (int n1, int p) { 
  // \esc{Initialisation d'un graphe de n1 sommets sous forme de matrice}
  // \esc{On tire au sort des nombres} 
  // \esc{entre 0 et 1 pour savoir s'il y a un arc entre i et j:  proba 1/p d'exister}

      nb = n1;
      m = new int[n1][n1];                           
      for (int i = 0; i < nb; ++i)
        for (int j = 0; j < nb; ++j) {
          int a  = (int) (Math.random() * p);
          if (a == p - 1 ) m[i][j] = 1;
            else m[i][j] = 0;
        }
  }

  static void multiplier (int c[][], int a[][], int b[][]) {
  // \esc{Produit de  matrices} 
                               
      int n = c.length;
      for (int i = 0; i < n; ++i)
          for (int j = 0; j < n; ++j) {
              c[i][j] = 0;
              for (int k = 0; k < n; ++k)
                  c[i][j] = c[i][j] + a[i][k] * b[k][j];
          }
  }

  static void additionner (int c[][], int a[][], int b[][]) {
  // \esc{Somme de  matrices} 
                               
      int n = c.length;
      for (int i = 0; i < n; ++i)
          for (int j = 0; j < n; ++j) 
              c[i][j] = a[i][j] * b[i][j];
  }
      
  static boolean existeChemin (int i, int j, GrapheMat g) {

      int n = g.nb;
      int m[][] = g.m;
      int u[][] = (int[][]) m.clone();
      int v[][] = new int[n][n];

      for (int k = 1; u[i][j] == 0 && k <= n; ++k) {
	  multiplier (v, u, m);
	  additionner (u, u, v);
      }
      return u[i][j] != 0;
  }

  static void phi (GrapheMat g, int a) { 
  // \esc{calcul de la Fermeture transitive , voir page 
  //   \pageref{prog:fermtrans} }

        for (int i = 0; i < g.nb; ++i)
        if (g.m[i][a] == 1)
            for (int j = 0; j < g.nb; ++j)
               if (g.m[a][j] == 1) 
                   g.m[i][j] = 1;
  }

  static public void fermetureTransitive (GrapheMat gr) {
   
      for (int k = 0; k < gr.nb; ++k) 
        phi(gr, k);
   }
}

class GrapheSucc{

  int succ[][];
  int nb;
  final static int Omega = -1;

  GrapheSucc (int n) {
      nb = n;
      succ = new int[n][n]; 
  } 

  GrapheSucc (GrapheMat g) {

  // Transformation en matrice de successeurs
  // d'un graphe donne comme une matrice d'adjacence

    nb = g.nb;
    int nbMaxSucc = 0;
    for (int i = 0; i < nb ; ++i) {
        nbMaxSucc = 0;
        for (int j = 0; j < nb  ; ++j) 
            if (g.m[i][j] != 0)
                nbMaxSucc = Math.max (nbMaxSucc, j);
    }
    succ = new int[nb][nbMaxSucc + 1]; 
    for (int i = 0; i < nb ; ++i) {
        int k = 0;
        for (int j = 0; j < nb  ; ++j) 
            if (g.m[i][j] != 0)
                succ[i][k++] = j;
        succ[i][k] = Omega;
    }          
  }

  static void imprimer (GrapheSucc g) {

     System.out.println ("nombre de sommets " + g.nb + " ");
     for (int i = 0; i < g.nb; ++i)
       { System.out.println ("sommet " + i + ", : successeurs: ");
         for (int j = 0; g.succ[i][j] != Omega; ++j)
         System.out.print (g.succ[i][j] +" ");
         System.out.println ();
       }
     System.out.println();
  }
}

class GrapheListe{

  Liste listeSucc[];
  int nb;

  GrapheListe (GrapheSucc g) {
  // \esc {Transformation en tableau de listes de successeurs}
  //  d'un graphe de n1 sommets donne comme une matrice de successeurs

    nb = g.nb;
    listeSucc = new Liste[nb];
    for (int i = 0; i < nb ; ++i) {
        listeSucc[i] = null;
        for (int k = 0; g.succ[i][k] != GrapheSucc.Omega; ++k)
            listeSucc[i] = Liste.ajouter(g.succ[i][k], listeSucc[i]);
    }
  }

  static void imprimer (GrapheListe g) {
  // Impression d'un graphe sous forme de  en tableau de listes de successeurs

      System.out.println ("nombre de sommets " + g.nb);
      for (int i = 0; i < g.nb; ++i) {
          System.out.println ("sommet " + i + ", : successeurs: ");
          for (Liste u = g.listeSucc[i]; u != null; u = u.suivant)
              System.out.print (" " + u.contenu);
         System.out.println ();
      } 
      System.out.println ();
      
  }

  GrapheListe (int n1) {
  // Creation d'un graphe sous forme d'un tableau de listes

      nb = n1;
      listeSucc = new Liste[n1];
  }

}

class Arbo{

  int pere[];
  final static int Omega = 1;


  Arbo (int n, int r) {
    pere = new int[n];
    for (int i = 0; i < n ; ++i) 
         pere[i] = Omega;
    pere[r] = r;
  }

  Arbo (GrapheSucc g, int r) {

    pere = new int[g.nb];
    pere[r] = r;
    for (int i = 0; i < g.nb ; ++i)
        for (int k = 0; g.succ[i][k] != GrapheSucc.Omega; ++k)
            pere[g.succ[i][k]] = i;
  }

  static  void imprimer (Arbo a) {

     System.out.println ("nombre de sommets " + a.pere.length + " ");
     for (int i = 0; i < a.pere.length; ++i)
         System.out.println ("sommet " + i + ", : pere: " + a.pere[i]);
  }
  
  static int[] numPrefixe (int r, GrapheSucc g) {

    int numero[] = new int[g.nb];
    numPrefixe1 (r, g, numero, 0);
    return numero;
  }

  static void numPrefixe1 (int x, GrapheSucc g, int numero[], int num) {
    numero [x] = num++;
    for (int i = 0; g.succ[x][i] != GrapheSucc.Omega; ++i)
        numPrefixe1 (g.succ[x][i], g, numero, num);
  }

  /*
   static Arbo arbPlusCourt (GrapheSucc g, int x0) {

      Arbo a = new Arbo(g.nb, x0);
  
      File f = new File.vide();
      
      File.ajouter(x0, f);
      while (!File.estVide(f)) {
          int x = File.valeur(f);
          File.supprimer(f);
          for (int i = 0; g.succ[x][i] != GrapheSucc.Omega; ++i) {
              int y = g.succ[x][i];
              if (a.pere[y] == Omega) {
                  a.pere[y] = x;
                  File.ajouter(y, f);
              }
          }
      }
     return a;
  }
*/
}


class Parcours{

  final static int Omega = -1;

  static void tremauxRec (int x, GrapheSucc g, Arbo a) {

    for (int k = 0; g.succ[x][k] != Omega; ++k) {
        int b = g.succ[x][k];
        if (a.pere[b] == Omega) { 
            a.pere[b] = x;
            tremauxRec(b, g, a);
        }
    }
  }

  static void tremaux (int x, GrapheSucc g) {

    tremauxRec(x, g, new Arbo (g.nb, x));
  }


  @SuppressWarnings("unused")
static void tremauxPile (int x, GrapheSucc g, Arbo a) {
  //  \esc{Proce'dure de Tre'maux Ite'rative voir page \pageref{prog:tremauxpil}}

    int y, z;
    Pile p = new Pile();
    Pile.ajouter (x, p);
    a.pere[x] = x;
  boucle:
    while ( !Pile.estVide(p) ) {
	y = Pile.valeur (p);
	for (int k = 0; g.succ[y][k] != GrapheSucc.Omega; ++k) {
	    z = g.succ[y][k]; 
	    if (a.pere[z] == Omega)
		a.pere[z] = y;
		Pile.ajouter (z, p);
		continue boucle;
	}
	Pile.supprimer (p);
    }
  }

}

class Composantes{

  final static int Omega = -1;

  static int pointAttache (int x, GrapheSucc g, int at[]) {
 
      int y, z;
      int m = x;
      at[x] = x;
      for (int k = 0; g.succ[x][k] != Omega; ++k) {
          y  = g.succ[x][k];
          if (at[y] == Omega)
              z = pointAttache(y, g, at);
          else
              z = y;
          m = Math.min(m, z);
      }
      at[x] = m;
      return m;
  }

  static void supprimerComp (int x, int num, int[] numComp,
                           GrapheSucc g) {
      numComp[x] = num;
      for (int k = 0; g.succ[x][k] != Omega; ++k) {
//int y = g.succ[a][k];
          int y = g.succ[x][k];
          if (y > x && numComp[y] == 0)
              supprimerComp (y, num, numComp, g);
      }
  }

  static int pointAttache1 (int x, GrapheSucc g, 
                           int at[], int numComp[], int num) {
      int res = num;
      at[x] = x;
      for (int k = 0; g.succ[x][k] != Omega; k++) {
          int y = g.succ[x][k];
          if (at[y] == Omega) {
              res = pointAttache1 (y, g, at, numComp, res);
              if (at[x] > at[y]) 
                  at[x]= at[y];
          } else
//if (numComp[b] == 0)
                 at[x] = Math.min (at[x], y) ;
      }
      if (x == at[x]) {
          ++res;
          supprimerComp(x, res, numComp, g);
      }
      return res;
  }

  static void compCon (GrapheSucc g) {

      int num = 0;
      int numComp[] = new int[g.nb];
      int at[] = new int[g.nb];
      for (int x = 0; x < g.nb; ++x) {
          numComp[x] = 0;
          at[x] = Omega;
      }
      for (int x = 0; x < g.nb ; ++x) 
          if (numComp[x] == 0 && at[x] == Omega)            
              num = pointAttache1 (x, g, at, numComp, num);
  }

}

class Demo{

  public static void main (String[] args) { 

    // Demo sur les graphes- matrices
 
      GrapheMat g1 = new GrapheMat(10, 5); 
      System.out.println("Un graphe tire au hasard");
      System.out.println();
      GrapheMat.imprimer(g1);
/*
      GrapheSucc g2 = new GrapheSucc(g1); 
      System.out.println("Transformation sous forme de matrice de successeurs:");
      System.out.println(); 
      GrapheSucc.imprimer(g2);
*/
      System.out.println("Existence d'un chemin entre 1 et 5: " 
           + GrapheMat.existeChemin(1, 5, g1));
      System.out.println();
      GrapheMat.fermetureTransitive(g1); 
      System.out.println("La fermeture transitive du precedent:");
      System.out.println(); 
      GrapheMat.imprimer(g1); 
//g2 = new GrapheSucc(g1); 
      System.out.println("Transformation sous forme de matrice de successeurs:");
      System.out.println(); 
//GrapheSucc.imprimer(g2);
      
/*
    // Demo sur les graphes- matrices de successeurs
  
      GrapheMat g3 = new GrapheMat(g2);
      System.out.println("Transformation sous forme de matrice de 0 et de 1 pour verifier:");
      System.out.println(); 
      GrapheMat.imprimer(g3);

    // Demo sur les graphes- listes  de successeurs
  
      GrapheListe g4 = new GrapheListe(g2);
      System.out.println("Transformation sous forme de listes ");
      System.out.println(); 
      GrapheListe.imprimer(g4);

    // Demo sur les arborescences

      System.out.println("Un graphe tire au hasard");
      System.out.println(); 
      g1 = new GrapheMat(10, 3); 
      g2 = new GrapheSucc(g1);
      GrapheSucc.imprimer(g2);

      Arbo a = new Arbo(g2, 0);
      System.out.println("Transformation sous forme d'arborescence du graphe precedent le pere est le dernier trouve");
      System.out.println(); 
      Arbo.imprimer(a);
      a = Arbo.arbPlusCourt(g2, 0);
      System.out.println("Arborescence des plus courts chemins du graphe precedent ");
      System.out.println(); 
      Arbo.imprimer(a);

  // Demo sur les parcours    
     a = new Arbo(g2.nb, 0);
     Parcours.tremauxRec(0, g2, a);
    
     System.out.println("Arborescence de TremauxRec du graphe precedent ");
     System.out.println(); 
     Arbo.imprimer(a);
     a = new Arbo(g2.nb, 0);
     Parcours.tremauxPil(0, g2, a);
     System.out.println("Arborescence de TremauxPil du graphe precedent ");
     System.out.println(); 
     Arbo.imprimer(a);
*/
  }
}
