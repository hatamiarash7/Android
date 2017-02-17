/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ir.hatamiarash.zimia.MainScreenActivity;
import ir.hatamiarash.zimia.R;
import ir.hatamiarash.zimia.Seller_Detail;
import volley.Config_TAG;

import static java.lang.Integer.parseInt;

public class ProductAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = ProductAdapter.class.getSimpleName();
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private Context context;

    public ProductAdapter(ArrayList<HashMap<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_product, null);
        }

        final TextView pid = (TextView) view.findViewById(R.id.pid);
        final TextView title = (TextView) view.findViewById(R.id.name);
        final TextView specification = (TextView) view.findViewById(R.id.specification);
        final TextView price = (TextView) view.findViewById(R.id.price);
        final TextView price_off = (TextView) view.findViewById(R.id.price2);
        final ImageView image = (ImageView) view.findViewById(R.id.img);
        final Button add = (Button) view.findViewById(R.id.add);
        final TextView temp_count = (TextView) view.findViewById(R.id.counter);
        final TextView added = (TextView) view.findViewById(R.id.added);
        final TextView backup_price = (TextView) view.findViewById(R.id.backup_price);
        final TextView rating = (TextView) view.findViewById(R.id.rate);
        final TextView extra = (TextView) view.findViewById(R.id.extra);
        final LinearLayout web3d = (LinearLayout) view.findViewById(R.id.web3d);

        final ImageView inc = (ImageView) view.findViewById(R.id.count_inc);
        final ImageView dec = (ImageView) view.findViewById(R.id.count_dec);
        final TextView count = (TextView) view.findViewById(R.id.item_count);

        //price_off.addTextChangedListener(Helper.TextAutoResize(this.context, price_off, 12, 30));
        //price.addTextChangedListener(Helper.TextAutoResize(this.context, price, 12, 30));

        inc.setVisibility(View.INVISIBLE);
        dec.setVisibility(View.INVISIBLE);
        count.setVisibility(View.INVISIBLE);
        extra.setVisibility(View.INVISIBLE);
        web3d.setVisibility(View.INVISIBLE);
        price_off.setVisibility(View.INVISIBLE);

        add.setText("خرید");
        add.setBackgroundResource(R.drawable.add);
        add.setTextColor(context.getResources().getColor(R.color.white));
        // set item details
        temp_count.setText("1");
        count.setText("1");
        added.setText("0");
        pid.setText(list.get(position).get(Config_TAG.ID));
        title.setText(list.get(position).get(Config_TAG.NAME));
        specification.setText(list.get(position).get(Config_TAG.SPECIFICATION));
        // calculate off price
        int off = Integer.valueOf(list.get(position).get(Config_TAG.PRICE_OFF));
        if (off != 0) {
            price_off.setVisibility(View.VISIBLE);
            extra.setVisibility(View.VISIBLE);
            int old_price = Integer.valueOf(Convert(list.get(position).get(Config_TAG.PRICE)));
            int new_price = old_price - old_price * off / 100;
            price.setText(list.get(position).get(Config_TAG.PRICE));
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price.setTextColor(context.getResources().getColor(R.color.gray));
            String p = String.valueOf(new_price) + " تومان";
            price_off.setText(p);
            price_off.setTextColor(context.getResources().getColor(R.color.md_red_500));
            backup_price.setText(String.valueOf(new_price));
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) web3d.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, R.id.rate);
            params.addRule(RelativeLayout.END_OF, R.id.rate);
            params.setMargins(0, 0, 0, 0);
            web3d.setLayoutParams(params);
            backup_price.setText(Convert(list.get(position).get(Config_TAG.PRICE)));
            price.setText(list.get(position).get(Config_TAG.PRICE));
            price.setTextColor(context.getResources().getColor(R.color.textColorTertiary));
            price.setPaintFlags(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        // add image
        String pic = list.get(position).get(Config_TAG.PICTURE);
        image.setImageResource(Integer.valueOf(pic));

        int support_web_3d = Integer.valueOf(list.get(position).get(Config_TAG.WEB_3D));
        if (support_web_3d != 0) web3d.setVisibility(View.VISIBLE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parseInt(added.getText().toString()) == 1) {
                    added.setText("2");
                    Badge(FormatHelper.toEnglishNumber(count.getText().toString()));
                    int c = parseInt(count.getText().toString());
                    String final_price = String.valueOf(parseInt(backup_price.getText().toString()) * c);
                    AddToCard(title.getText().toString(), final_price, c);
                    inc.setVisibility(View.INVISIBLE);
                    dec.setVisibility(View.INVISIBLE);
                    count.setVisibility(View.INVISIBLE);
                    add.setText("خرید");
                    add.setBackgroundResource(R.drawable.add);
                    add.setTextColor(context.getResources().getColor(R.color.white));
                    count.setText("1");
                } else if (!MainScreenActivity.db.isExists(title.getText().toString())) {
                    added.setText("1");
                    add.setText("ثبت");
                    add.setBackgroundResource(R.drawable.confirm);
                    add.setTextColor(context.getResources().getColor(R.color.black));
                    inc.setVisibility(View.VISIBLE);
                    dec.setVisibility(View.VISIBLE);
                    count.setVisibility(View.VISIBLE);

                } else if (parseInt(added.getText().toString()) == 2 || MainScreenActivity.db.isExists(title.getText().toString()))
                    MakeToast("این محصول در سبد خرید موجود است");
            }
        });
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.valueOf(temp_count.getText().toString());
                c++;
                count.setText(String.valueOf(c));
                temp_count.setText(String.valueOf(c));
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.valueOf(temp_count.getText().toString());
                if (c > 1) {
                    c--;
                    count.setText(String.valueOf(c));
                    temp_count.setText(String.valueOf(c));
                } else {
                    add.setText("خرید");
                    added.setText("0");
                    add.setBackgroundResource(R.drawable.add);
                    add.setTextColor(context.getResources().getColor(R.color.white));
                    inc.setVisibility(View.INVISIBLE);
                    dec.setVisibility(View.INVISIBLE);
                    count.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }

    private void AddToCard(String name, String price, int count) { // add or update item
        if (!MainScreenActivity.db.isExists(name)) {
            MainScreenActivity.db.addItem(name, price, String.valueOf(count));
            MakeToast(name + " اضافه شد");
        } else MakeToast("این محصول در سبد خرید موجود است");
    }

    @NonNull
    private String Convert(String seller) {
        return seller.substring(0, seller.length() - 6);
    }

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(context, efr, Toast.LENGTH_SHORT).show();
    }

    private void Badge(String value) {
        Seller_Detail.pointer.badgeView.setBadgeBackgroundColor(context.getResources().getColor(R.color.Amber));
        Seller_Detail.pointer.badgeView.setTextColor(context.getResources().getColor(R.color.md_black_1000));
        TranslateAnimation anim = new TranslateAnimation(0, 0, -50, 0);
        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(1000);
        Seller_Detail.pointer.badgeView.toggle(anim, null);
        Seller_Detail.pointer.badgeView.setText(FormatHelper.toPersianNumber(Helper.SumString(value, Seller_Detail.pointer.badgeView.getText().toString())));
        Seller_Detail.pointer.badgeView.clearComposingText();
        Seller_Detail.pointer.badgeView.show();
    }
}