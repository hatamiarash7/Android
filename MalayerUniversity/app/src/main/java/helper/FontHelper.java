/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.content.Context;
import android.graphics.Typeface;

public class FontHelper {
    public static final String FontPath = "fonts/shabnam.ttf";    // font path
    private static FontHelper instance;
    private static Typeface persianTypeface;                      // typeface

    private FontHelper(Context context) {
        persianTypeface = Typeface.createFromAsset(context.getAssets(), FontPath);
    }

    public static synchronized FontHelper getInstance(Context context) {
        if (instance == null)
            instance = new FontHelper(context);
        return instance;
    }

    Typeface getPersianTextTypeface() {
        return persianTypeface;
    }
}