package utbm.tx52.atoms_visualiser.utils;

import java.util.UUID;

/**
 * Created by adah on 11/12/16.
 */
public class RandomHelper {
    public static String getRandomID() {
        return UUID.randomUUID().toString();
    }
}
