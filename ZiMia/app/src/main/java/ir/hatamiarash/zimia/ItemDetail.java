package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import helper.TypefaceSpan;

public class ItemDetail extends Activity {
    private static final String TAG_PID = "id";
    EditText counter;
    Button inc;
    Button dec;
    String pid;
    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        counter = (EditText) findViewById(R.id.count);
        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);
        inc = (Button) findViewById(R.id.inc);
        dec = (Button) findViewById(R.id.dec);

        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                counter.setText(String.valueOf(count));
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 0)
                    count--;
                counter.setText(String.valueOf(count));
            }
        });
    }

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_LONG).show();
    }
}