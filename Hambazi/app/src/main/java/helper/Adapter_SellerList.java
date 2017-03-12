package helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ir.hatamiarash.hambazidemo.R;
import ir.hatamiarash.hambazidemo.SellerActivity;
import volley.Config_URL;


public class Adapter_SellerList extends BaseAdapter implements ListAdapter, LoadImageTask.Listener {
    private static final String TAG = Adapter_SellerList.class.getSimpleName();
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private Context context;
    private ImageView image;

    public Adapter_SellerList(ArrayList<HashMap<String, String>> list, Context context) {
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
            view = inflater.inflate(R.layout.item_seller, null);
        }

        final TextView uid = (TextView) view.findViewById(R.id.uid);
        final TextView name = (TextView) view.findViewById(R.id.name);
        image = (ImageView) view.findViewById(R.id.logo);

        uid.setText(list.get(position).get("uid"));
        name.setText(list.get(position).get("name"));
        String image_name = list.get(position).get("image");

        image.setImageDrawable(context.getResources().getDrawable(R.drawable.nnull));
        if (!image_name.equals("null"))
            new LoadImageTask(this).execute(Config_URL.image_URL + image_name);
        else
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.nnull));


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SellerActivity.class);
                i.putExtra("uid", uid.getText().toString());
                context.startActivity(i);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SellerActivity.class);
                i.putExtra("uid", uid.getText().toString());
                context.startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void onError() {
        Log.d(TAG, "مشکل در بارگزاری تصویر");
    }
}