package shakeit.com.shakefilter.utils;

import android.os.Build;

/**
 * Created by camposbrunocampos on 11/03/15.
 */
public class AndroidAPIUtils {

    public static boolean isPos(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    public static boolean isPre(int version) {
        return Build.VERSION.SDK_INT < version;
    }

    /**
     * Check if it is pos API 11 (Android 3.0)
     */
    public static boolean isPosHoneycomb() {
        return isPos(Build.VERSION_CODES.HONEYCOMB);
    }

    /**
     * Check if it is pos API 14 (Android 4.0)
     */
    public static boolean isPosIceCreamSandwich() {
        return isPos(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    /**
     * Check if it is pos API 16 (Android 4.1)
     */
    public static boolean isPosJellyBean() {
        return isPos(Build.VERSION_CODES.JELLY_BEAN);
    }

    /**
     * Check if it is pos API 18 (Android 4.3)
     */
    public static boolean isPosJellyBeanMR2() {
        return isPos(Build.VERSION_CODES.JELLY_BEAN_MR2);
    }

    /**
     * Check if it is pos API 19 (Android 4.4)
     */
    public static boolean isPosKitKat() {
        return isPos(Build.VERSION_CODES.KITKAT);
    }

    /**
     * Check if it is pos API 21 (Android 5.0)
     */
    public static boolean isPreJellyBean() {
        return isPre(Build.VERSION_CODES.JELLY_BEAN);
    }

    public static boolean isPosLollipop() {
        return isPos(Build.VERSION_CODES.LOLLIPOP);
    }

    public static boolean isPreHoneycomb() {
        return isPre(Build.VERSION_CODES.HONEYCOMB);
    }

}
