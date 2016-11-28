package ir.hatamiarash.ZiMia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import ir.hatamiarash.ZiMia.model.FastFood;

public class ZiMia_FastFood extends ArrayAdapter<FastFood> {
    private Context context;
    private List<FastFood> fastfoodList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_FastFood(Context context, int resource, List<FastFood> objects) {
        super(context, resource, objects);
        this.context = context;
        this.fastfoodList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_fastfood, parent, false);
        //Display informations in the TextView widgets
        FastFood fastfood = fastfoodList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.fastfood_textView1);
        Name.setText(fastfood.getName());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(fastfood.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.fastfood_imageView1);
            image.setImageBitmap(fastfood.getBitmap());
        } else {
            FastFoodAndView container = new FastFoodAndView();
            container.ateriad = fastfood;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class FastFoodAndView {
        public FastFood ateriad;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<FastFoodAndView, Void, FastFoodAndView> {
        @Override
        protected FastFoodAndView doInBackground(FastFoodAndView... params) {
            FastFoodAndView container = params[0];
            FastFood fastfood = container.ateriad;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + fastfood.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                fastfood.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FastFoodAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.fastfood_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.ateriad.getid(), result.bitmap);
        }
    }
}