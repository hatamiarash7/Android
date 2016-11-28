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

import ir.hatamiarash.ZiMia.model.FastFood_Food;

public class ZiMia_FastFood_Food extends ArrayAdapter<FastFood_Food> {
    private Context context;
    private List<FastFood_Food> fastfood_foodList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_FastFood_Food(Context context, int resource, List<FastFood_Food> objects) {
        super(context, resource, objects);
        this.context = context;
        this.fastfood_foodList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_fastfood_food, parent, false);
        //Display informations in the TextView widgets
        FastFood_Food fastfood_food = fastfood_foodList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.fastfood_food_textView1);
        TextView Price = (TextView) view.findViewById(R.id.fastfood_food_textView2);
        TextView Specification = (TextView) view.findViewById(R.id.fastfood_food_textView3);
        Name.setText(fastfood_food.getName());
        Price.setText(fastfood_food.getPrice());
        Specification.setText(fastfood_food.getSpecification());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(fastfood_food.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.fastfood_food_imageView1);
            image.setImageBitmap(fastfood_food.getBitmap());
        } else {
            ZiMia_FastFood_FoodAndView container = new ZiMia_FastFood_FoodAndView();
            container.fastfood_food = fastfood_food;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class ZiMia_FastFood_FoodAndView {
        public FastFood_Food fastfood_food;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<ZiMia_FastFood_FoodAndView, Void, ZiMia_FastFood_FoodAndView> {
        @Override
        protected ZiMia_FastFood_FoodAndView doInBackground(ZiMia_FastFood_FoodAndView... params) {
            ZiMia_FastFood_FoodAndView container = params[0];
            FastFood_Food fastfood_food = container.fastfood_food;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + fastfood_food.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                fastfood_food.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ZiMia_FastFood_FoodAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.fastfood_food_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.fastfood_food.getid(), result.bitmap);
        }
    }
}
