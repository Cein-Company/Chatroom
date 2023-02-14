package utils;

public class ArraysHelper {
    public static String toString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        b.append("\n");

        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax) {
                b.append("\n");
                return b.toString();
            }
            b.append("\n");
        }
    }
}
