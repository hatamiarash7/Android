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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.Helper;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class FragmentInfo extends Fragment {
    private static final String TAG = FragmentInfo.class.getSimpleName();
    static Context context;
    private ProgressDialog progressDialog;
    TextView game_name, game_desc, game_point, game_levels, game_duration, game_players, game_age;
    Button play;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        game_name = (TextView) view.findViewById(R.id.name);
        game_desc = (TextView) view.findViewById(R.id.description);
        game_point = (TextView) view.findViewById(R.id.point);
        game_levels = (TextView) view.findViewById(R.id.level_count);
        game_duration = (TextView) view.findViewById(R.id.play_duration);
        game_players = (TextView) view.findViewById(R.id.player_count);
        game_age = (TextView) view.findViewById(R.id.age);
        play=(Button)view.findViewById(R.id.play);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        FetchGameData(getArguments().getString("arg"));

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(context, WebPage.class);
                i.putExtra("address", "http://mehrdad.arash-hatami.ir");
                context.startActivity(i);
            }
        });

        return view;
    }

    private void FetchGameData(final String uid) {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setMessage("در حال به روزرسانی ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Game Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONObject game = jObj.getJSONObject("game");
                        String name = game.getString("name");
                        String description = game.getString("description");
                        Double point = game.getDouble("point");
                        String levels = game.getString("level_count");
                        String duration = game.getString("play_duration");
                        String age = game.getString("age");
                        String players = game.getString("player_count");

                        game_name.setText(name);
                        game_desc.setText(description);
                        game_levels.setText(levels);
                        game_age.setText(age + " سال");
                        game_duration.setText(duration + " دقیقه");
                        game_players.setText(players);
                        game_point.setText(String.valueOf(point));
                    } else
                        Helper.MakeToast(FragmentInfo.context, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR);
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
                    Helper.MakeToast(FragmentInfo.context, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(FragmentInfo.context, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "game_details");
                params.put(Config_TAG.UID, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    public static Fragment newInstance(String text, Context Context) {
        FragmentInfo f = new FragmentInfo();
        context = Context;
        Bundle b = new Bundle();
        b.putString("arg", text);
        f.setArguments(b);
        return f;
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