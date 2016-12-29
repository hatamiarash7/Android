package helper;

import android.content.Context;
import android.graphics.Typeface;

public class FontHelper {
    public static final String FontPath = "fonts/yekan.ttf";
    private static FontHelper instance;
    private static Typeface persianTypeface;

    private FontHelper(Context context) {
        persianTypeface = Typeface.createFromAsset(context.getAssets(), FontPath);
    }

    public static synchronized FontHelper getInstance(Context context) {
        if (instance == null)
            instance = new FontHelper(context);
        return instance;
    }

    public Typeface getPersianTextTypeface() {
        return persianTypeface;
    }
}