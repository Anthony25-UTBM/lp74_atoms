package utbm.tx52.atoms_visualiser;

import java.util.*;


// Grille repr�sentant l'environnement + les atomes
public class Environnement extends Observable {
    // protected Atome[] atomes;
    protected ArrayList<Atome> atomes;
    protected ArrayList<Molecule> molecules;
    protected Random generateur;
    protected double largeur;
    protected double hauteur;
    protected double profondeur;

    public Environnement(int _nbAtomes, double _largeur, double _profondeur, double _hauteur, boolean isCHNO) {
        largeur = _largeur;
        hauteur = _hauteur;
        profondeur = _profondeur;
        generateur = new Random();
        molecules = new ArrayList();
        atomes = new ArrayList();
        int nbSamples;

        if (isCHNO) {
            nbSamples = 4;
        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            nbSamples = t_periodic.getSymbole().size();
            System.out.println("Number of samples " + nbSamples);
            if (nbSamples == 0) return;
        }


        for (int i = 0; i < _nbAtomes; i++) {
            int number = 0;
            number = generateur.nextInt(nbSamples - 1);
            if (isCHNO) number = CHNO.getInstance().getANumber(number);

            atomes.add(new Atome(number, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * profondeur, generateur.nextDouble() * 2 * Math.PI, isCHNO));
        }
    }

    public void AjouterMolecule(double _posX, double _posY, double rayon) {
        molecules.add(new Molecule(_posX, _posY, rayon));
        if (!molecules.isEmpty()) {
            System.out.println("Molecule Yes");
        } else System.out.println("Molecule NO");
    }

    protected void MiseAJourMolecule() {
        for (Molecule m : molecules) {
            m.MiseAJour();
        }
        //TODO:
        //atomes.removeIf(a2 -> a2.estMort());
    }

    protected void MiseAJourAtomes(AGroup world) {
        for (Atome a : atomes) {
            a.MiseAJour(atomes, molecules, largeur, hauteur, profondeur);
            a.draw(world);
        }
    }

    public void addAtome(Atome a) {

        atomes.add(a);
    }

    public void MiseAJourEnv(AGroup world) {
        MiseAJourAtomes(world);
        MiseAJourMolecule();
        setChanged();
        notifyObservers();
    }

    public Map<String, Integer> nbOfEachAtoms() {
        Map<String, Integer> atom_groups = new Hashtable<String, Integer>();
        for (Atome a : atomes) {
            int current_nb = atom_groups.containsKey(a.getSymb()) ? atom_groups.get(a.getSymb()) : 0;
            atom_groups.put(a.getSymb(), current_nb + 1);
        }

        return atom_groups;
    }

    public int nbOfNotActiveAtoms() {
        int not_active_atoms = 0;
        for (Atome a : atomes) {
            if (a.isNotActive())
                not_active_atoms++;
        }

        return not_active_atoms;
    }


    public void setAtomesSpeed(int speed) {
        for (Atome a : atomes) {
            a.setSpeed(speed);
        }
    }

    public double getSpeed() {
        return atomes.get(0).getSpeed();
    }
}
