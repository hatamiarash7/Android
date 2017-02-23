package ir.hatamiarash.hambazidemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import volley.Config_TAG;

public class FragmentList extends Fragment {
    ListView listView;
    static Context contextt;
    private ArrayList<HashMap<String, String>> RestaurantList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.first_frag, container, false);
        RestaurantList = new ArrayList<>();

        listView = (ListView) v.findViewById(R.id.baziche_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(contextt.getApplicationContext(), WebPage.class);
                i.putExtra(Config_TAG.ADDRESS, "http://mehrdad.arash-hatami.ir");
                startActivity(i);
            }
        });
        FetchAllRestaurants();
        return v;
    }

    private void FetchAllRestaurants() {
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Config_TAG.ID, String.valueOf(i));
            map.put(Config_TAG.NAME, "بازی " + String.valueOf(i));
            String add = "ii" + String.valueOf(i);
            int pic = getResources().getIdentifier(add, "drawable", contextt.getPackageName());
            map.put(Config_TAG.PICTURE, String.valueOf(pic));

            RestaurantList.add(map);

            ListAdapter adapter = new SimpleAdapter(
                    contextt, RestaurantList,
                    R.layout.item_seller, new String[]{
                    Config_TAG.ID,
                    Config_TAG.NAME,
                    Config_TAG.PICTURE
            },
                    new int[]{
                            R.id.pid,
                            R.id.name,
                            R.id.img
                    });
            listView.setAdapter(adapter);
        }
    }

    public static Fragment newInstance(String text, Context context) {
        FragmentList f = new FragmentList();
        contextt = context;
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
}