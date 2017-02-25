/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package helper;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;

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

    public static synchronized SpannableString getSpannedString(Context context, String TEXT) {
        persianTypeface = Typeface.createFromAsset(context.getAssets(), FontPath);
        SpannableString result = new SpannableString(TEXT);
        result.setSpan(new TypefaceSpan(persianTypeface), 0, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }

    Typeface getPersianTextTypeface() {
        return persianTypeface;
    }
}