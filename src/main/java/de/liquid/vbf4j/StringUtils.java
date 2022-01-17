package de.liquid.vbf4j;

import java.util.Collections;

public class StringUtils {

    public static String repeat(String s, int n) {
        return String.join("", Collections.nCopies(n, s));
    }

}
