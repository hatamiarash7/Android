package ir.adad.sampleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Random;

import ir.adad.client.Adad;
import ir.adad.client.InterstitialAdListener;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    public int mFirstNum;
    public int mSecondNum;

    private InterstitialAdListener mAdListener = new InterstitialAdListener() {
        @Override
        public void onAdLoaded() {
            Toast.makeText(getApplicationContext(), "Interstitial Ad loaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad() {
            Toast.makeText(getApplicationContext(), "Interstitial Ad failed to load", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMessageReceive(JSONObject message) {
        }

        @Override
        public void onRemoveAdsRequested() {
            Toast.makeText(getApplicationContext(), "User requested to remove interstitial ads from app", Toast.LENGTH_SHORT).show();
            //Move your user to shopping center of your app
        }

        @Override
        public void onInterstitialAdDisplayed() {

        }

        @Override
        public void onInterstitialClosed() {
            Toast.makeText(getApplicationContext(),"Interstitial Ad closed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Adad.prepareInterstitialAd(mAdListener);

        makeQuestions();
    }

    private void makeQuestions() {
        TextView firstNumberTextView = (TextView) findViewById(R.id.first_number);
        TextView secondNumberTextView = (TextView) findViewById(R.id.second_number);

        firstNumberTextView.setOnClickListener(this);
        secondNumberTextView.setOnClickListener(this);

        mFirstNum = randomNumberGenerator();
        mSecondNum = randomNumberGenerator();

        firstNumberTextView.setText(String.valueOf(mFirstNum));
        secondNumberTextView.setText(String.valueOf(mSecondNum));

        if (mFirstNum >= mSecondNum){
            secondNumberTextView.setTextSize(50);
        } else {
            firstNumberTextView.setTextSize(50);
        }
    }

    private int randomNumberGenerator(){
        Random r = new Random();
        int Low = 1;
        int High = 100;
        return r.nextInt(High-Low) + Low;
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setPositiveButton("Return", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GameActivity.this.finish();
                    }
                })
                .setNegativeButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Adad.prepareInterstitialAd(mAdListener);
                        makeQuestions();
                    }
                });

        if (isClickedItemCorrect(mFirstNum, mSecondNum, v)){
            builder.setMessage("Your answer was correct");
        } else {
            builder.setMessage("You were WORNG!!!!!");
        }

        AlertDialog alert = builder.create();
        alert.show();
        Adad.showInterstitialAd(this);
    }

    public boolean isClickedItemCorrect(int firstNum, int secondNum, View v){
        if (firstNum > secondNum && v.getId() == R.id.first_number)
            return true;
        else if (secondNum >= firstNum  && v.getId() == R.id.second_number)
            return true;

        return false;
    }
}
