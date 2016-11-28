package ir.hatamiarash.ZiMia;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.hatamiarash.ZiMia.model.FastFood;
import ir.hatamiarash.ZiMia.model.FastFood_Food;
import ir.hatamiarash.ZiMia.model.Market;
import ir.hatamiarash.ZiMia.model.Market_Product;
import ir.hatamiarash.ZiMia.model.Market_Product_Type;
import ir.hatamiarash.ZiMia.model.Resturan;
import ir.hatamiarash.ZiMia.model.Resturan_Food;
import ir.hatamiarash.ZiMia.parsers.ZiMiaXMLParser;


public class MainActivity extends ListActivity {
    public static final String PHOTOS_BASE_URL = "http://zimia.ir/photos/";
    ProgressBar pb;

    List<ResturanTask> resturantasks;
    List<FastFoodTask> fastfoodtasks;
    List<MarketTask> markettasks;
    List<Resturan_FoodTask> resturan_foodtasks;
    List<FastFood_FoodTask> fastfood_foodtasks;
    List<Market_ProductTask> market_producttasks;
    List<Market_Product_TypeTask> market_pruduct_typetasks;

    List<FastFood> fastfoodList;
    List<FastFood_Food> fastfood_foodList;
    List<Resturan> resturanList;
    List<Resturan_Food> resturan_foodList;
    List<Market> marketList;
    List<Market_Product> market_productList;
    List<Market_Product_Type> market_product_typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        resturantasks = new ArrayList<>();
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
                Time now = new Time();
                now.setToNow();
                String time = "" + now.hour + now.minute + now.second;
                int time_int = Integer.parseInt(time);
                requestData("http://zimia.ir/feed/secure/main.xml" + "?unused=" + time_int);
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData(String uri) {
        ResturanTask resturantask = new ResturanTask();
        FastFoodTask fastfoodtask = new FastFoodTask();
        MarketTask markettask = new MarketTask();
        Resturan_FoodTask resturan_foodtask = new Resturan_FoodTask();
        FastFood_FoodTask fastfood_foodtask = new FastFood_FoodTask();
        Market_ProductTask market_producttask = new Market_ProductTask();
        Market_Product_TypeTask market_product_typetask = new Market_Product_TypeTask();

        resturantask.execute(uri);
        fastfoodtask.execute(uri);
        markettask.execute(uri);
        resturan_foodtask.execute(uri);
        fastfood_foodtask.execute(uri);
        market_producttask.execute(uri);
        market_product_typetask.execute(uri);
    }

    protected void updateDisplay() {
        //Use FlowerAdapter to display data
        ZiMia_Resturan resturanadapter = new ZiMia_Resturan(this, R.layout.zimia_resturan, resturanList);
        ZiMia_FastFood fastfoodadapter = new ZiMia_FastFood(this, R.layout.zimia_fastfood, fastfoodList);
        ZiMia_Market marketadapter = new ZiMia_Market(this, R.layout.zimia_market, marketList);
        ZiMia_Resturan_Food resturan_foodadapter = new ZiMia_Resturan_Food(this, R.layout.zimia_resturan_food, resturan_foodList);
        ZiMia_FastFood_Food fastfood_foodadapter = new ZiMia_FastFood_Food(this, R.layout.zimia_fastfood_food, fastfood_foodList);
        ZiMia_Market_Product market_productadapter = new ZiMia_Market_Product(this, R.layout.zimia_market_product, market_productList);
        ZiMia_Market_Product_Type market_product_typeadapter = new ZiMia_Market_Product_Type(this, R.layout.zimia_market_product_type, market_product_typeList);

        setListAdapter(resturanadapter);
        setListAdapter(fastfoodadapter);
        setListAdapter(marketadapter);
        setListAdapter(resturan_foodadapter);
        setListAdapter(fastfood_foodadapter);
        setListAdapter(market_productadapter);
        setListAdapter(market_product_typeadapter);
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

    private class ResturanTask extends AsyncTask<String, String, List<Resturan>> {
        @Override
        protected void onPreExecute() {
            if (resturantasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            resturantasks.add(this);
        }

        @Override
        protected List<Resturan> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            resturanList = ZiMiaXMLParser.Resturan_parseFeed(content);
            return resturanList;
        }

        @Override
        protected void onPostExecute(List<Resturan> result) {
            resturantasks.remove(this);
            //if (resturantasks.size() == 0) {
              //  pb.setVisibility(View.INVISIBLE);
            //}
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            resturanList = result;
            updateDisplay();
        }
    }

    private class MarketTask extends AsyncTask<String, String, List<Market>> {
        @Override
        protected void onPreExecute() {
            //if (markettasks.size() == 0) {
              //  pb.setVisibility(View.VISIBLE);
            //}
            //markettasks.add(this);
        }

        @Override
        protected List<Market> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            marketList = ZiMiaXMLParser.Market_parseFeed(content);
            return marketList;
        }

        @Override
        protected void onPostExecute(List<Market> result) {
            //markettasks.remove(this);
            //if (markettasks.size() == 0) {
              //  pb.setVisibility(View.INVISIBLE);
            //}
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            marketList = result;
            updateDisplay();
        }
    }

    private class FastFoodTask extends AsyncTask<String, String, List<FastFood>> {
        @Override
        protected void onPreExecute() {
            //if (fastfoodtasks.size() == 0) {
              //  pb.setVisibility(View.VISIBLE);
            //}
            //fastfoodtasks.add(this);
        }

        @Override
        protected List<FastFood> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            fastfoodList = ZiMiaXMLParser.FastFood_parseFeed(content);
            if(fastfoodList == null)
            {
                Toast.makeText(MainActivity.this, "null", Toast.LENGTH_LONG).show();
            }
            return fastfoodList;
        }

        @Override
        protected void onPostExecute(List<FastFood> result) {
            //fastfoodtasks.remove(this);
            //if (fastfoodtasks.size() == 0) {
              //  pb.setVisibility(View.INVISIBLE);
            //}
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            fastfoodList = result;
            updateDisplay();
        }
    }

    private class Resturan_FoodTask extends AsyncTask<String, String, List<Resturan_Food>> {
        @Override
        protected void onPreExecute() {
//            if (resturan_foodtasks.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
//            }
//            resturan_foodtasks.add(this);
        }

        @Override
        protected List<Resturan_Food> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            resturan_foodList = ZiMiaXMLParser.Resturan_Food_parseFeed(content);
            return resturan_foodList;
        }

        @Override
        protected void onPostExecute(List<Resturan_Food> result) {
//            resturan_foodtasks.remove(this);
//            if (resturan_foodtasks.size() == 0) {
//                pb.setVisibility(View.INVISIBLE);
//            }
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            resturan_foodList = result;
            updateDisplay();
        }
    }

    private class FastFood_FoodTask extends AsyncTask<String, String, List<FastFood_Food>> {
        @Override
        protected void onPreExecute() {
//            if (fastfood_foodtasks.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
//            }
//            fastfood_foodtasks.add(this);
        }

        @Override
        protected List<FastFood_Food> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            fastfood_foodList = ZiMiaXMLParser.FastFood_Food_parseFeed(content);
            return fastfood_foodList;
        }

        @Override
        protected void onPostExecute(List<FastFood_Food> result) {
//            fastfood_foodtasks.remove(this);
//            if (fastfood_foodtasks.size() == 0) {
//                pb.setVisibility(View.INVISIBLE);
//            }
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            fastfood_foodList = result;
            updateDisplay();
        }
    }

    private class Market_ProductTask extends AsyncTask<String, String, List<Market_Product>> {
        @Override
        protected void onPreExecute() {
//            if (market_producttasks.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
//            }
//            market_producttasks.add(this);
        }

        @Override
        protected List<Market_Product> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            market_productList = ZiMiaXMLParser.Market_Product_parseFeed(content);
            return market_productList;
        }

        @Override
        protected void onPostExecute(List<Market_Product> result) {
//            market_producttasks.remove(this);
//            if (market_producttasks.size() == 0) {
//                pb.setVisibility(View.INVISIBLE);
//            }
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            market_productList = result;
            updateDisplay();
        }
    }

    private class Market_Product_TypeTask extends AsyncTask<String, String, List<Market_Product_Type>> {
        @Override
        protected void onPreExecute() {
//            if (market_pruduct_typetasks.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
//            }
//            market_pruduct_typetasks.add(this);
        }

        @Override
        protected List<Market_Product_Type> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "hatamiarash7", "3920512197");
            //ateriadList = AteriadJSONParser.parseFeed(content);
            market_product_typeList = ZiMiaXMLParser.Market_Product_Type_parseFeed(content);
            return market_product_typeList;
        }

        @Override
        protected void onPostExecute(List<Market_Product_Type> result) {
//            market_pruduct_typetasks.remove(this);
//            if (market_pruduct_typetasks.size() == 0) {
//                pb.setVisibility(View.INVISIBLE);
//            }
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            market_product_typeList = result;
            updateDisplay();
        }
    }
}