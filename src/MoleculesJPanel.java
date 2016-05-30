//package jeuDeLaVie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

// Panel principal gérant le jeu de la vie (sa création + son lancement + mises à jour)
public class MoleculesJPanel extends JPanel implements Observer, MouseListener {
    Timer timer;
    boolean enCours = false;
    Environnement env;
    TimerTask tache;
    
    public MoleculesJPanel() {
        this.setBackground(Color.WHITE);
        this.addMouseListener(this);
    }
    
    public void Lancer() {
        env = new Environnement(this.getWidth() / 3, getHeight() / 3, 1000);
        env.addObserver(this);
        timer = new Timer();
        tache = new TimerTask() {
            @Override
            public void run() {
                env.MiseAJour(true);
            }
        };
        timer.scheduleAtFixedRate(tache, 0, 500);
        enCours = true;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

    public void DessinerCellule(Graphics g, int i, int j, int k) {
        //g.fillRect(3*i-1, 3*j-1, 3, 3);
        g.fillRect(10*i-1, 10*j-1, 10, 10);
        if (k == 1) g.setColor(Color.BLUE);
        else { if (k == 2) g.setColor(Color.RED);
               else g.setColor(Color.BLACK);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < env.largeur; i++) {
            for (int j = 0; j < env.hauteur; j++) {
                if (env.contenu[i][j][0] > 0)
                    DessinerCellule(g, i, j, env.contenu[i][j][0]);
            }
        }
    }
    
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

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}

