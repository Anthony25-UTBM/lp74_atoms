package utbm.tx52.atoms_visualiser.entities;

import javafx.geometry.Point3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adah on 16/11/16.
 */
public class Formula {
    private static final Logger logger = LogManager.getLogger("Environment");

    public ArrayList<Atom> parse(Environment environment, String formula, boolean isCHNO) {
        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d*)");
        Matcher matcher = pattern.matcher(formula);
        ArrayList<Atom> atoms = new ArrayList<>();

        while (matcher.find()) {
            String symbole = matcher.group(2);
            Point3D a_coord;
            if (!verifyCHNO(isCHNO, matcher.group(1))) {
                continue;
            }
            Random random_generator = new Random();

            logger.debug("adding atom " + matcher.group(1));
            if (!symbole.isEmpty()) {
                int count = Integer.parseInt(matcher.group(2));


                for (int i = 1; i < count; i++) {
                    a_coord = new Point3D(
                            random_generator.nextDouble() * (environment.getSize()/2 - 1),
                            random_generator.nextDouble() * (environment.getSize()/2 - 1),
                            random_generator.nextDouble() * (environment.getSize()/2 - 1)
                    );
                    atoms.add(new Atom(environment, matcher.group(1),a_coord,45, isCHNO));
                }
            }
            a_coord = new Point3D(
                    random_generator.nextDouble() * (environment.getSize()/2 - 1),
                    random_generator.nextDouble() * (environment.getSize()/2 - 1),
                    random_generator.nextDouble() * (environment.getSize()/2 - 1)
            );
            atoms.add(new Atom(environment, matcher.group(1),a_coord,45, isCHNO));
        }
        return atoms;
    }

    private boolean verifyCHNO(boolean isCHNO, String symb) {
        if (isCHNO) {
            CHNO t_chno = CHNO.getInstance();
            int index = t_chno.getSymbole().indexOf(symb);

            return (index >= 0);
        }
        return true;
    }
}
