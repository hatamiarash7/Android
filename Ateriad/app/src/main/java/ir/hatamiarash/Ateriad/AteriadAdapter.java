package ir.hatamiarash.Ateriad;

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

import ir.hatamiarash.Ateriad.model.Ateriad;

public class AteriadAdapter extends ArrayAdapter<Ateriad> {

    private Context context;
    private List<Ateriad> ateriadList;

    private LruCache<Integer, Bitmap> imageCache;

    public AteriadAdapter(Context context, int resource, List<Ateriad> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ateriadList = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_ateriad, parent, false);

        //Display flower name in the TextView widget
        Ateriad ateriad = ateriadList.get(position);
        TextView FirstName = (TextView) view.findViewById(R.id.textView1);
        TextView LastName = (TextView) view.findViewById(R.id.textView2);
        TextView job = (TextView) view.findViewById(R.id.textView3);
        TextView phone = (TextView) view.findViewById(R.id.textView4);
        FirstName.setText(ateriad.getFirstName());
        LastName.setText(ateriad.getLastName());
        job.setText(ateriad.getjob());
        phone.setText(" - " + ateriad.getphone());

        //Display flower photo in ImageView widget
        Bitmap bitmap = imageCache.get(ateriad.getid());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(ateriad.getBitmap());
        } else {
            AteriadAndView container = new AteriadAndView();
            container.ateriad = ateriad;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    class AteriadAndView {
        public Ateriad ateriad;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<AteriadAndView, Void, AteriadAndView> {

        @Override
        protected AteriadAndView doInBackground(AteriadAndView... params) {

            AteriadAndView container = params[0];
            Ateriad ateriad = container.ateriad;

            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + ateriad.getPhoto();
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
        protected void onPostExecute(AteriadAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
//			result.flower.setBitmap(result.bitmap);
            imageCache.put(result.ateriad.getid(), result.bitmap);
        }

    }

}
