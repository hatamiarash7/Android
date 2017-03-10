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

public class FragmentComment extends Fragment {
    private static final String TAG = FragmentComment.class.getSimpleName();
    static Context context;
    TextView no_comment;
    Button play;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        return view;
    }

    public static Fragment newInstance(String text, Context Context) {
        FragmentComment f = new FragmentComment();
        context = Context;
        Bundle b = new Bundle();
        b.putString("arg", text);
        f.setArguments(b);
        return f;
    }
}