/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PersianTextView extends TextView {
    public PersianTextView(Context context) {
        super(context);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }

    public PersianTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }

    public PersianTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface_Shabnam());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null)
            text = FormatHelper.toPersianNumber(text.toString());
        super.setText(text, type);
    }
}

