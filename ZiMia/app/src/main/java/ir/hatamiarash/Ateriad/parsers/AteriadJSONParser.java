package ir.hatamiarash.Ateriad.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.hatamiarash.Ateriad.model.Ateriad;

public class AteriadJSONParser {

    public static List<Ateriad> parseFeed(String content) {

        try {
            JSONArray ar = new JSONArray(content);
            List<Ateriad> ateriadList = new ArrayList<>();

            for (int i = 0; i < ar.length(); i++) {

                JSONObject obj = ar.getJSONObject(i);
                Ateriad ateriad = new Ateriad();

                ateriad.setid(obj.getInt("id"));
                ateriad.setFirstName(obj.getString("FirstName"));
                ateriad.setLastName(obj.getString("LastName"));
                ateriad.setphone(obj.getString("phone"));
                ateriad.setemail(obj.getString("email"));
                ateriad.setPhoto(obj.getString("photo"));
                ateriad.setjob(obj.getString("job"));

                ateriadList.add(ateriad);
            }

            return ateriadList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}