package ir.hatamiarash.zimia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_comment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comment, container, false);

        return v;
    }

    public static Fragment newInstance() {
        Fragment_comment f = new Fragment_comment();

        return f;
    }
}