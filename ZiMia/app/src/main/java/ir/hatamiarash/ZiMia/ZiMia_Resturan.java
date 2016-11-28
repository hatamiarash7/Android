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

import ir.hatamiarash.ZiMia.model.Resturan;

public class ZiMia_Resturan extends ArrayAdapter<Resturan> {
    private Context context;
    private List<Resturan> resturanList;
    private LruCache<Integer, Bitmap> imageCache;

    public ZiMia_Resturan(Context context, int resource, List<Resturan> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resturanList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zimia_resturan, parent, false);
        //Display informations in the TextView widgets
        Resturan resturan = resturanList.get(position);
        TextView Name = (TextView) view.findViewById(R.id.resturan_textView1);
        Name.setText(resturan.getName());
        //Display photo in ImageView widget
        Bitmap bitmap = imageCache.get(resturan.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.resturan_imageView1);
            image.setImageBitmap(resturan.getBitmap());
        } else {
            ResturanAndView container = new ResturanAndView();
            container.resturan = resturan;
            container.view = view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class ResturanAndView {
        public Resturan resturan;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<ResturanAndView, Void, ResturanAndView> {
        @Override
        protected ResturanAndView doInBackground(ResturanAndView... params) {
            ResturanAndView container = params[0];
            Resturan ateriad = container.resturan;
            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + ateriad.getPicture();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                ateriad.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResturanAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.resturan_imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.resturan.getid(), result.bitmap);
        }
    }
}
