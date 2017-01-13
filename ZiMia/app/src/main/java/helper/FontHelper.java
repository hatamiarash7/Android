/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.content.Context;
import android.graphics.Typeface;

public class FontHelper {
    public static final String FontPath = "fonts/shabnam.ttf";    // font path
    public static final String FontPath2 = "fonts/yekan.ttf";     // font path
    public static final String FontPath3 = "fonts/calibril.ttf";  // font path
    public static final String FontPath4 = "fonts/iran_sans.ttf"; // font path
    private static FontHelper instance;
    private static Typeface persianTypeface;                   // typeface
    private static Typeface persianTypeface2;                  // typeface
    private static Typeface persianTypeface3;                  // typeface
    private static Typeface persianTypeface4;                  // typeface

    private FontHelper(Context context) {
        // set font for typeface
        persianTypeface = Typeface.createFromAsset(context.getAssets(), FontPath);
        persianTypeface2 = Typeface.createFromAsset(context.getAssets(), FontPath2);
        persianTypeface3 = Typeface.createFromAsset(context.getAssets(), FontPath3);
        persianTypeface4 = Typeface.createFromAsset(context.getAssets(), FontPath4);
    }

    public static synchronized FontHelper getInstance(Context context) {
        if (instance == null)
            instance = new FontHelper(context);
        return instance;
    }

    Typeface getPersianTextTypeface_Yekan() {
        return persianTypeface2;
    }

    Typeface getPersianTextTypeface_Shabnam() {
        return persianTypeface;
    }

    Typeface getPersianTextTypeface_Calibri() {
        return persianTypeface3;
    }

    Typeface getPersianTextTypeface_IranSans() {
        return persianTypeface4;
    }
}