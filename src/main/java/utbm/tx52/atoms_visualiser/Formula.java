package utbm.tx52.atoms_visualiser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adah on 16/11/16.
 */
public class Formula {
    private static final Logger logger = LogManager.getLogger("Environment");

    public ArrayList<Atome> parse(String formula, boolean isCHNO) {
        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d*)");
        Matcher matcher = pattern.matcher(formula);
        ArrayList<Atome> atoms = new ArrayList<>();

        while (matcher.find()) {
            String symbole = matcher.group(2);
            if (!verifyCHNO(isCHNO, matcher.group(1))) {
                continue;
            }
            logger.debug("adding atom " + matcher.group(1));
            if (!symbole.isEmpty()) {
                int count = Integer.parseInt(matcher.group(2));


                for (int i = 1; i < count; i++) {
                    atoms.add(new Atome(matcher.group(1), isCHNO));
                }
            }
            atoms.add(new Atome(matcher.group(1), isCHNO));
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
