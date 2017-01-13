/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class PersianRadioButton extends RadioButton {
    public PersianRadioButton(Context context) {
        super(context);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }

    public PersianRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }

    public PersianRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }
}

