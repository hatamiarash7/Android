package helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ir.hatamiarash.hambazidemo.R;
import volley.Config_TAG;
import volley.Config_URL;

public class Adapter_GameList extends BaseAdapter implements ListAdapter, LoadImageTask.Listener {
    private static final String TAG = Adapter_GameList.class.getSimpleName();
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private Context context;
    private ImageView image;

    public Adapter_GameList(ArrayList<HashMap<String, String>> list, Context context) {
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
            view = inflater.inflate(R.layout.item_game, null);
        }

        final TextView pid = (TextView) view.findViewById(R.id.uid);
        final TextView name = (TextView) view.findViewById(R.id.name);
        final TextView play_count = (TextView) view.findViewById(R.id.play_count);
        final TextView point = (TextView) view.findViewById(R.id.point);
        image = (ImageView) view.findViewById(R.id.img);

        final Button show = (Button) view.findViewById(R.id.show);

        pid.setText(list.get(position).get(Config_TAG.ID));
        name.setText(list.get(position).get(Config_TAG.NAME));
        play_count.setText("تعداد بازیکن : " + list.get(position).get("player_count"));
        point.setText("امتیاز : " + list.get(position).get("point"));
        String image_name = list.get(position).get("image");

        new LoadImageTask(this).execute(Config_URL.image_URL + "game_" + image_name);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Helper.MakeToast(context, "مشکل در بارگزاری تصویر", Config_TAG.ERROR);
    }
}