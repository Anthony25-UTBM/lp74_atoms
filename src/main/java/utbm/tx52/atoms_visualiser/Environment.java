package utbm.tx52.atoms_visualiser;

import java.util.*;


// Grille repr�sentant l'environnement + les atoms
public class Environment extends Observable {
    // protected Atome[] atoms;
    protected ArrayList<Atome> atoms;
    protected ArrayList<Molecule> molecules;
    protected Random random_generator;
    protected double width;
    protected double height;
    protected double depth;

    public Environment(int nbAtoms, double width, double depth, double height, boolean isCHNO) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        random_generator = new Random();
        molecules = new ArrayList();
        atoms = new ArrayList();
        int nbSamples;

        PeriodicTable t_periodic = PeriodicTable.getInstance();
        if (isCHNO) {
            nbSamples = 4;
        } else {
            nbSamples = t_periodic.getSymbole().size();
            System.out.println("Number of samples " + nbSamples);
            if (nbSamples == 0) return;
        }

        for (int i = 0; i < nbAtoms; i++) {
            int number = random_generator.nextInt(nbSamples - 1);
            if (isCHNO)
                number = CHNO.getInstance().getANumber(number);
            else
                number = t_periodic.getNumber().get(number);

            double a_x = random_generator.nextDouble() * this.width;
            double a_y = random_generator.nextDouble() * this.height;
            double a_z = random_generator.nextDouble() * this.depth;
            double a_dir = random_generator.nextDouble() * 2 * Math.PI;
            atoms.add(new Atome(number, a_x, a_y, a_z, a_dir, isCHNO));
        }
    }

    public void addMolecule(double _posX, double _posY, double rayon) {
        molecules.add(new Molecule(_posX, _posY, rayon));
        if (!molecules.isEmpty()) {
            System.out.println("Molecule Yes");
        } else System.out.println("Molecule NO");
    }

    public ArrayList<Atome> getAtoms() {
        return new ArrayList<Atome>(atoms);
    }

    public void addAtom(Atome a) {
        atoms.add(a);
    }

    protected void updateMolecules() {
        molecules.forEach(Molecule::update);
        //TODO:
        //atoms.removeIf(a2 -> a2.estMort());
    }

    protected void updateAtoms(AGroup world) {
        for (Atome a : atoms) {
            a.MiseAJour(atoms, molecules, width, height, depth);
            a.draw(world);
        }
    }

    public void updateEnv(AGroup world) {
        updateAtoms(world);
        updateMolecules();
        setChanged();
        notifyObservers();
    }

    public Map<String, Integer> nbOfEachAtoms() {
        Map<String, Integer> atom_groups = new Hashtable<String, Integer>();
        for (Atome a : atoms) {
            int current_nb = atom_groups.containsKey(a.getSymb()) ? atom_groups.get(a.getSymb()) : 0;
            atom_groups.put(a.getSymb(), current_nb + 1);
        }

        return atom_groups;
    }

    public int nbOfNotActiveAtoms() {
        int not_active_atoms = 0;
        for (Atome a : atoms) {
            if (a.isNotActive())
                not_active_atoms++;
        }

        return not_active_atoms;
    }

    public double getSpeed() {
        return atoms.get(0).getSpeed();
    }

    public void setAtomsSpeed(int speed) throws NegativeSpeedException {
        if (speed < 0) {
            throw new NegativeSpeedException("Speed should be positive or null");
        }
        atoms.forEach(a->{
            a.setSpeed(speed);
        });
    }
}
