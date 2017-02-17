/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ir.hatamiarash.MyToast.CustomToast;
import helper.SQLiteHandler;
import helper.SQLiteHandlerItem;
import volley.Config_TAG;

public class ShopCard extends ListActivity {
    private static final String TAG = ShopCard.class.getSimpleName(); // class tag for log
    private ArrayList<HashMap<String, String>> ItemList;     // item array list
    private Button Clear_Card, Pay_Card;                     // pay or clear card
    Button btnConfirm, btnCancel, btnDelete;         // popup buttons
    Button inc, dec;                                 // increase or decrease item count
    private TextView name, count, price;                     // item details
    private TextView CardPrice, TotalPrice, CardDiscount;    // card prices
    TextView item_name;
    private EditText item_count;
    int discount = 0;                                // price discount
    private int itemCount;                                   // item count ( integer )
    private String ItemPrice, ItemName, ItemCount;           // item details
    private PopupWindow pwindo;                      // popup
    private SQLiteHandlerItem db;                    // users database
    private SQLiteHandler db2;                       // items database
    private View.OnClickListener confirm_button_click_listener = new View.OnClickListener() { // confirm event
        public void onClick(View v) {
            // calculate new price
            int new_price = Integer.parseInt(ItemPrice) / Integer.parseInt(ItemCount) * itemCount;
            // update database row
            db.updateItem(ItemName, String.valueOf(new_price), String.valueOf(itemCount));
            pwindo.dismiss();                        // close popup
            MakeToast("سفارش به روزرسانی شد", Config_TAG.SUCCESS);       // show notification
            Intent i = new Intent(getApplicationContext(), ShopCard.class);
            // start card activity and finish this one to refresh
            startActivity(i);
            finish();
        }
    };
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() { // cancel event
        public void onClick(View v) {
            pwindo.dismiss(); // close popup
        }
    };
    private View.OnClickListener delete_button_click_listener = new View.OnClickListener() { // delete event
        public void onClick(View v) {
            db.deleteItem(ItemName);           // delete row from database
            pwindo.dismiss();                  // close popup
            MakeToast("سفارش به روزرسانی شد", Config_TAG.SUCCESS); // show notification
            Intent i = new Intent(getApplicationContext(), ShopCard.class);
            // start card activity and finish this one to refresh
            startActivity(i);
            finish();
        }
    };
    private View.OnClickListener inc_button_click_listener = new View.OnClickListener() { // increase event
        public void onClick(View v) {
            itemCount++;                                       // increase item's count by one
            item_count.setText(String.valueOf(itemCount));     // set new count
        }
    };
    private View.OnClickListener dec_button_click_listener = new View.OnClickListener() { // decrease event
        public void onClick(View v) {
            if (itemCount > 1) {                               // check count ( we should have at least one )
                itemCount--;                                   // increase item's count by one
                item_count.setText(String.valueOf(itemCount)); // set new count
            } else
                MakeToast("حداقل انتخاب یک عدد می باشد", Config_TAG.WARNING);      // show error notification
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_card);
        db = new SQLiteHandlerItem(getApplicationContext());       // items database
        db2 = new SQLiteHandler(getApplicationContext());          // users database
        ItemList = new ArrayList<>();                              // list for save items
        Clear_Card = (Button) findViewById(R.id.btnClear);         // clear button
        Pay_Card = (Button) findViewById(R.id.btnPay);             // pay button
        new LoadAllCardItems().execute();                          // execute load class
        Clear_Card.setOnClickListener(new View.OnClickListener() { // clear event
            @Override
            public void onClick(View view) {
                MakeQuestion("سبد خرید", "تمام کالا های سبد خرید حذف شوند ؟"); // question for clearing card
            }
        });
        Pay_Card.setOnClickListener(new View.OnClickListener() {              // pay event
            @Override
            public void onClick(View view) {
                if (db2.getRowCount() > 0) {                                  // check user logged
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ShopCard.this);
                    dialog.setTitle("پرداخت");                                                    // set title
                    dialog.setMessage("وضعیت");                                                   // set message
                    dialog.setIcon(R.drawable.ic_alert);                                          // set icon
                    dialog.setPositiveButton("بدون خطا", new DialogInterface.OnClickListener() {  // positive answer
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();                                                     // close dialog
                            String pay_code = "ok";
                            Intent i = new Intent(getApplicationContext(), Pay_Log.class);
                            i.putExtra(Config_TAG.PAY_STATUS, pay_code);
                            startActivity(i);
                            finish();
                        }
                    });
                    dialog.setNegativeButton("با خطا", new DialogInterface.OnClickListener() { // negative answer
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss(); // close dialog
                            String pay_code = "error";
                            Intent i = new Intent(getApplicationContext(), Pay_Log.class);
                            i.putExtra(Config_TAG.PAY_STATUS, pay_code);
                            startActivity(i);
                            finish();
                        }
                    });
                    AlertDialog alert = dialog.create(); // create dialog
                    alert.show();                        // show dialog
                } else {
                    MakeToast("شما وارد نشده اید", Config_TAG.ERROR);      // show error notification
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    // start login activity
                    startActivity(i);
                    // finish this one
                    finish();
                }
            }
        });
        final ListView lv = getListView();                                // items list
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // list's item click event
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemName = ((TextView) view.findViewById(R.id.name)).getText().toString();          // get item's name from list
                ItemCount = ((TextView) view.findViewById(R.id.count)).getText().toString();        // get item's count from list
                itemCount = Integer.parseInt(ItemCount);                                            // convert count
                ItemPrice = ((TextView) view.findViewById(R.id.price_backup)).getText().toString(); // get item's price from list
                initiatePopupWindow(ItemName, String.valueOf(itemCount));                           // show popup for item
            }
        });
    }

    private void MakeQuestion(String Title, String Message) {                     // build and show an confirm window
        AlertDialog.Builder dialog = new AlertDialog.Builder(ShopCard.this);
        dialog.setTitle(Title);                                                  // set title
        dialog.setMessage(Message);                                              // set message
        dialog.setIcon(R.drawable.ic_alert);                                     // set icon
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {  // positive answer
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();                      // close dialog
                db.deleteItems();                      // delete all items from database
                MakeToast("سبد خرید خالی شد", Config_TAG.SUCCESS);         // show notification
                MainScreenActivity.card_check = false; // set card table status false
                Intent i = new Intent(getApplicationContext(), ShopCard.class);
                // starts card activity and finish this one to refresh
                startActivity(i);
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() { // negative answer
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // close dialog
            }
        });
        AlertDialog alert = dialog.create(); // create dialog
        alert.show();                        // show dialog
    }

    private void MakeToast(String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(this, Message, R.drawable.ic_alert, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(this, Message, R.drawable.ic_success, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(this, Message, R.drawable.ic_error, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    private void initiatePopupWindow(String name, String count) { // show popup
        try {
            Display display = getWindowManager().getDefaultDisplay(); // get display settings
            Point size = new Point();                                 // define new point var
            display.getSize(size);                                    // get display's size
            int width = size.x;
            int height = size.y;
            LayoutInflater inflater = (LayoutInflater) ShopCard.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.item_edit, (ViewGroup) findViewById(R.id.popup_edit_item));
            pwindo = new PopupWindow(layout, width - (width / 5), height - (height / 2), true); // set layout size
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);                                // set layout position to center of screen
            item_name = (TextView) layout.findViewById(R.id.item_name);   // item name
            item_count = (EditText) layout.findViewById(R.id.item_count); // item count
            item_name.setText(name);                                      // set item name
            item_count.setText(count);                                    // set item count
            btnConfirm = (Button) layout.findViewById(R.id.btnConfirm);   // confirm button
            btnCancel = (Button) layout.findViewById(R.id.btnCancel);     // cancel button
            btnDelete = (Button) layout.findViewById(R.id.btnDelete);     // delete button
            inc = (Button) layout.findViewById(R.id.inc);                 // increase button
            dec = (Button) layout.findViewById(R.id.dec);                 // decrease button
            btnConfirm.setOnClickListener(confirm_button_click_listener); // confirm event
            btnCancel.setOnClickListener(cancel_button_click_listener);   // cancel event
            btnDelete.setOnClickListener(delete_button_click_listener);   // delete event
            inc.setOnClickListener(inc_button_click_listener);            // increase event
            dec.setOnClickListener(dec_button_click_listener);            // decrease event
        } catch (Exception e) {
            Log.e(TAG, "Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class LoadAllCardItems extends AsyncTask<String, String, String> { // load all items from database
        protected String doInBackground(String... args) {
            List<String> Item = db.getItemDetails();    // get all items
            if (Item.size() > 0)                        // if there is item in card
                for (int i = 0; i < (Item.size() / 4); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    String id = Item.get(i * 4);        // get item id
                    String name = Item.get(i * 4 + 1);  // get item name
                    String price = Item.get(i * 4 + 2); // get item price
                    String count = Item.get(i * 4 + 3); // get item count
                    map.put(Config_TAG.ID, id);               // put id to hash
                    map.put(Config_TAG.NAME, name);            // put name to hash
                    map.put(Config_TAG.PRICE, price);          // put price to hash
                    map.put(Config_TAG.COUNT, count);          // put count to hash
                    ItemList.add(map);                  // add hash to list
                }
            else { // card is empty
                name = (TextView) findViewById(R.id.ItemNameHeader);              // item name
                count = (TextView) findViewById(R.id.ItemCountHeader);            // item count
                price = (TextView) findViewById(R.id.ItemPriceHeader);            // item price
                final LinearLayout prices = (LinearLayout) findViewById(R.id.l1); // all prices info layout
                runOnUiThread(new Runnable() {
                    public void run() {
                        prices.setVisibility(View.GONE);                                         // hide final layout
                        name.setText(null);                                                      // clear name text
                        name.getLayoutParams().width = 50;
                        count.setText("سبد خرید شما خالی است");                                  // change count text
                        count.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        price.setText(null);                                                     // clear price name
                        price.getLayoutParams().width = 50;
                        Clear_Card.setEnabled(false);                                            // set button enable status to false
                        Clear_Card.setBackgroundColor(getResources().getColor(R.color.gray));    // set button background to gray
                        Clear_Card.setTextColor(getResources().getColor(R.color.md_black_1000)); // set button foreground to black
                        Pay_Card.setEnabled(false);                                              // set button enable status to false
                        Pay_Card.setBackgroundColor(getResources().getColor(R.color.gray));      // set button background to gray
                        Pay_Card.setTextColor(getResources().getColor(R.color.md_black_1000));   // set button foreground to black
                    }
                });
            }
            int card_price = db.TotalPrice();                                     // get total price from database
            int total_price = card_price - discount;                              // calculate final price with discount
            final String Price = String.valueOf(card_price) + " تومان";           // set price ( original )
            final String Discount = String.valueOf(discount) + " تومان";          // set price ( discount )
            final String FinalPrice = String.valueOf(total_price) + " تومان";     // set price ( final )
            TotalPrice = (TextView) findViewById(R.id.CardTotalPrice);            // original price
            CardDiscount = (TextView) findViewById(R.id.CardDiscount);            // discount
            CardPrice = (TextView) findViewById(R.id.CardPrice);                  // final price
            runOnUiThread(new Runnable() {
                public void run() {
                    TotalPrice.setText(FinalPrice); // set price
                    CardDiscount.setText(Discount); // set price
                    CardPrice.setText(Price);       // set price
                }
            });
            return null;
        }

        protected void onPostExecute(String file_url) {
            // define an adaptor and send all data to layout (ui)
            /* we send another price field - we are using
             persian numbers and we lose original number
             so we can define another field and finally hide that
             */
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            ShopCard.this, ItemList,
                            R.layout.list_card, new String[]{
                            Config_TAG.ID,
                            Config_TAG.NAME,
                            Config_TAG.COUNT,
                            Config_TAG.PRICE,
                            Config_TAG.PRICE
                    },
                            new int[]{
                                    R.id.pid,
                                    R.id.name,
                                    R.id.count,
                                    R.id.price,
                                    R.id.price_backup
                            });
                    setListAdapter(adapter);
                }
            });
        }
    }
}