package Molecules_V4;
//package Molecules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;


// Panel principal gérant la simulation (sa création + son lancement + mises à jour)
public class MoleculesJPanel_2 extends JPanel implements Observer, MouseListener {
    protected Environnement_2 env;
    protected Timer timer;
    boolean enCours = false;
    TimerTask tache;
    
    public MoleculesJPanel_2() {
        this.setBackground(Color.WHITE);
        this.addMouseListener(this);
    }
    
    public void Lancer() {
        env = new Environnement_2(1000, this.getWidth(), getHeight(),0);
        env.addObserver(this);
        TimerTask tache = new TimerTask() {
            @Override
            public void run() {
                env.MiseAJourEnv();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(tache, 0, 500); 	//lent : 250
    }    
    
    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }    
     
    protected void DessinerMolecule(Molecule m, Graphics g) {
        //g.drawLine((int) m.posX, (int) m.posY, (int) (m.posX - 10 * m.vitesseX), (int) (m.posY - 10 * m.vitesseY));
        g.fillOval((int) (m.posX - m.rayon), (int) (m.posY - m.rayon), (int) m.rayon * 2, (int) m.rayon * 2);
        g.setColor(Color.GREEN);
    }
    
    protected void DessinerAtome(Atome a, Graphics g) {
		g.setColor(a.couleur);
    	if (a.etat == 2) g.fillOval((int) (a.posX - a.rayon), (int) (a.posY - a.rayon), (int) a.rayon * 2, (int) a.rayon * 2);
    	else g.drawOval((int) (a.posX - a.rayon), (int) (a.posY - a.rayon), (int) a.rayon * 2, (int) a.rayon * 2);
    }
     
 
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*for (Molecule m : env.molecules) {
            DessinerMolecule(m, g);
        }*/
        for (Atome a : env.atomes) {
            DessinerAtome(a, g);
        }
    }    

/*    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Clic gauche : ajout de cellules vivantes
            env.ChangerEtat(e.getX() / 3, e.getY() / 3);
            env.MiseAJour(false);
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            // Clic droit : pause du timer
            if (enCours) {
                timer.cancel();
                timer = null;
            }
            else {
                timer = new Timer();
                tache = new TimerTask() {
                    @Override
                    public void run() {
                        env.MiseAJour(true);
                    }
                };
                timer.scheduleAtFixedRate(tache, 0, 500);
            }
            enCours = !enCours;
        }
    }
*/
    @Override
    public void mouseClicked(MouseEvent e) {
        //env.AjouterMolecule(e.getX(), e.getY(), 30);
    }  
    
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}


