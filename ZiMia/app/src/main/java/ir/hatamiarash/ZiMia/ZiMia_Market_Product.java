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

import ir.hatamiarash.ZiMia.model.Market_Product;

public class ZiMia_Market_Product extends ArrayAdapter<Market_Product> {
    private Context context;
    private List<Market_Product> market_productList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_Market_Product(Context context, int resource, List<Market_Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.market_productList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_market_product, parent, false);
        //Display informations in the TextView widgets
        Market_Product market_product = market_productList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.market_product_textView1);
        TextView Price = (TextView) view.findViewById(R.id.market_product_textView2);
        TextView Specification = (TextView) view.findViewById(R.id.market_product_textView3);
        Name.setText(market_product.getName());
        Price.setText(market_product.getPrice());
        Specification.setText(market_product.getSpecification());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(market_product.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.market_product_imageView1);
            image.setImageBitmap(market_product.getBitmap());
        } else {
            Market_ProductAndView container = new Market_ProductAndView();
            container.market_product = market_product;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class Market_ProductAndView {
        public Market_Product market_product;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<Market_ProductAndView, Void, Market_ProductAndView> {
        @Override
        protected Market_ProductAndView doInBackground(Market_ProductAndView... params) {
            Market_ProductAndView container = params[0];
            Market_Product market_product = container.market_product;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + market_product.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                market_product.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Market_ProductAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.market_product_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.market_product.getid(), result.bitmap);
        }
    }
}
