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

import ir.hatamiarash.ZiMia.model.Resturan_Food;

public class ZiMia_Resturan_Food extends ArrayAdapter<Resturan_Food> {
    private Context context;
    private List<Resturan_Food> resturan_foodList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_Resturan_Food(Context context, int resource, List<Resturan_Food> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resturan_foodList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_resturan_food, parent, false);
        //Display informations in the TextView widgets
        Resturan_Food resturan_food = resturan_foodList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.resturan_food_textView1);
        TextView Price = (TextView) view.findViewById(R.id.resturan_food_textView2);
        TextView Specification = (TextView) view.findViewById(R.id.resturan_food_textView3);
        Name.setText(resturan_food.getName());
        Price.setText(resturan_food.getPrice());
        Specification.setText(resturan_food.getSpecification());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(resturan_food.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.resturan_food_imageView1);
            image.setImageBitmap(resturan_food.getBitmap());
        } else {
            Resturan_FoodAndView container = new Resturan_FoodAndView();
            container.resturan_food = resturan_food;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class Resturan_FoodAndView {
        public Resturan_Food resturan_food;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<Resturan_FoodAndView, Void, Resturan_FoodAndView> {
        @Override
        protected Resturan_FoodAndView doInBackground(Resturan_FoodAndView... params) {
            Resturan_FoodAndView container = params[0];
            Resturan_Food resturan_food = container.resturan_food;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + resturan_food.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                resturan_food.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Resturan_FoodAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.resturan_food_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.resturan_food.getid(), result.bitmap);
        }
    }
}
