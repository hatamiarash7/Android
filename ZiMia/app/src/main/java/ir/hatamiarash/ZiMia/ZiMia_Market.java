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

import ir.hatamiarash.ZiMia.model.Market;

public class ZiMia_Market extends ArrayAdapter<Market> {
    private Context context;
    private List<Market> marketList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_Market(Context context, int resource, List<Market> objects) {
        super(context, resource, objects);
        this.context = context;
        this.marketList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_market, parent, false);
        //Display informations in the TextView widgets
        Market market = marketList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.market_textView1);
        Name.setText(market.getName());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(market.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.market_imageView1);
            image.setImageBitmap(market.getBitmap());
        } else {
            MarketAndView container = new MarketAndView();
            container.market = market;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class MarketAndView {
        public Market market;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<MarketAndView, Void, MarketAndView> {
        @Override
        protected MarketAndView doInBackground(MarketAndView... params) {
            MarketAndView container = params[0];
            Market market = container.market;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + market.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                market.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MarketAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.market_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.market.getid(), result.bitmap);
        }
    }
}
