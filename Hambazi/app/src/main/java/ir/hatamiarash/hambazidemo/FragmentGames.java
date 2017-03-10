package ir.hatamiarash.hambazidemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import helper.Adapter_GameList;
import helper.Helper;
import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class FragmentGames extends Fragment {
    private static final String TAG = FragmentGames.class.getSimpleName();
    static Context context;
    private ArrayList<HashMap<String, String>> GameList;
    private ListView game_list;
    private ProgressDialog progressDialog;
    private SQLiteHandler db;
    private String genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        GameList = new ArrayList<>();
        game_list = (ListView) view.findViewById(R.id.game_list);

        genre = String.valueOf(Helper.ConvertGenres(getArguments().getString("genre")));
        db = new SQLiteHandler(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        String user_type = db.getUserDetails().get(Config_TAG.TYPE);
        Log.d(TAG, "Genre : " + genre + " Uer Type : " + user_type);
        FetchAllGames(user_type, genre);

        game_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uid = ((TextView) view.findViewById(R.id.uid)).getText().toString();
                Intent i = new Intent(context, GameActivity.class);
                i.putExtra("uid", uid);
                context.startActivity(i);
            }
        });
        return view;
    }

    private void FetchAllGames(final String user_type, final String genre) {
        String string_req = "req_fetch";
        progressDialog.setMessage("در حال به روزرسانی ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Games Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONArray games = jObj.getJSONArray("games");
                        for (int i = 0; i < games.length(); i++) {
                            JSONObject game = games.getJSONObject(i);
                            String uid = game.getString(Config_TAG.UID);
                            String name = game.getString(Config_TAG.NAME);
                            String player_count = game.getString("player_count");
                            Double point = game.getDouble("point");
                            String image = game.getString("image");

                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.UID, uid);
                            map.put(Config_TAG.NAME, name);
                            map.put("image", image);
                            map.put("player_count", player_count);
                            map.put("point", String.valueOf(point));

                            GameList.add(map);
                            Adapter_GameList adapter = new Adapter_GameList(GameList, FragmentGames.context);
                            game_list.setAdapter(adapter);
                        }
                    } else
                        //Helper.MakeToast(FragmentGames.context, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR);
                        Log.e(TAG, "Error : " + jObj.getString(Config_TAG.ERROR_MSG));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(FragmentGames.context, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(FragmentGames.context, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_games");
                params.put(Config_TAG.TYPE, user_type);
                params.put("genre", genre);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    public static Fragment newInstance(String genre, Context Context) {
        FragmentGames fragmentGames = new FragmentGames();
        context = Context;
        Bundle bundle = new Bundle();
        bundle.putString("genre", genre);
        fragmentGames.setArguments(bundle);
        return fragmentGames;
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}