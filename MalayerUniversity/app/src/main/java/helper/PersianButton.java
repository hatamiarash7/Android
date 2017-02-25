/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class PersianButton extends Button {
    public PersianButton(Context context) {
        super(context);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());
    }

    public PersianButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());
    }

    public PersianButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());
    }
}

