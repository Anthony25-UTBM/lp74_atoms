package utbm.tx52.atoms_visualiser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adah on 16/11/16.
 */
public class Formula {
    public ArrayList<Atome> parse(String formula, boolean isCHNO) {
        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d*)");
        Matcher matcher = pattern.matcher(formula);
        ArrayList<Atome> atoms = new ArrayList<>();

        while (matcher.find()) {
            String symbole = matcher.group(2);
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
}
