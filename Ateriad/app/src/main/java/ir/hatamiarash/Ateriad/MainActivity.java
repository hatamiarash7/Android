package ir.hatamiarash.Ateriad;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.hatamiarash.Ateriad.model.Ateriad;
import ir.hatamiarash.Ateriad.parsers.AteriadJSONParser;
import ir.hatamiarash.Ateriad.parsers.AteriadXMLParser;

public class MainActivity extends ListActivity {

    public static final String PHOTOS_BASE_URL =
            "http://zimia.arash-hatami.ir/photo/";

    TextView output;
    ProgressBar pb;
    List<MyTask> tasks;

    List<Ateriad> ateriadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            if (isOnline()) {
                requestData("http://zimia.arash-hatami.ir/secure/ateriad.xml");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use FlowerAdapter to display data
        AteriadAdapter adapter = new AteriadAdapter(this, R.layout.item_ateriad, ateriadList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, List<Ateriad>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Ateriad> doInBackground(String... params) {

            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            ateriadList = AteriadXMLParser.parseFeed(content);
            return ateriadList;
        }

        @Override
        protected void onPostExecute(List<Ateriad> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            ateriadList = result;
            updateDisplay();

        }

    }

}