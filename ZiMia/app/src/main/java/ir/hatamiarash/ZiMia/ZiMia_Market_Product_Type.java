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

import ir.hatamiarash.ZiMia.model.Market_Product_Type;

public class ZiMia_Market_Product_Type extends ArrayAdapter<Market_Product_Type> {
    private Context context;
    private List<Market_Product_Type> market_product_typeList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_Market_Product_Type(Context context, int resource, List<Market_Product_Type> objects) {
        super(context, resource, objects);
        this.context = context;
        this.market_product_typeList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_market_product_type, parent, false);
        //Display informations in the TextView widgets
        Market_Product_Type market_product_type = market_product_typeList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.market_product_type_textView1);
        Name.setText(market_product_type.getName());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(market_product_type.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.market_product_type_imageView1);
            image.setImageBitmap(market_product_type.getBitmap());
        } else {
            Market_Product_TypeAndView container = new Market_Product_TypeAndView();
            container.market_product_type = market_product_type;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class Market_Product_TypeAndView {
        public Market_Product_Type market_product_type;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<Market_Product_TypeAndView, Void, Market_Product_TypeAndView> {
        @Override
        protected Market_Product_TypeAndView doInBackground(Market_Product_TypeAndView... params) {
            Market_Product_TypeAndView container = params[0];
            Market_Product_Type market_product_type = container.market_product_type;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + market_product_type.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                market_product_type.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Market_Product_TypeAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.market_product_type_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.market_product_type.getid(), result.bitmap);
        }
    }
}