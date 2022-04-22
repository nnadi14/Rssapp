package org.me.gcu.nnadi_emma_s1906614;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView tvDate;
    Spinner spinner;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;

    String urlStr = "http://m.highwaysengland.co.uk/feeds/rss/AllEvents.xml";
    String TAG = "theS";
    Item item;
    String text;
    String date = null;
    int roadIndex = 0;
    ArrayList<Item> mainItemList = new ArrayList<>();
    ArrayList<String> roadList = new ArrayList<>();

    ProgressDialog dialog;
    Thread thread;

    final Calendar c = Calendar.getInstance();
    final int mYear = c.get(Calendar.YEAR);
    final int mMonth = c.get(Calendar.MONTH);
    final int mDay = c.get(Calendar.DAY_OF_MONTH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tv_date);
        spinner = findViewById(R.id.road_spinner);
        recyclerView = findViewById(R.id.recycler_view);

        if (savedInstanceState == null) {
            getInformation();
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year;
                                tvDate.setText(date);
                                Date date1 = Utils.changeStringIntoDate(date);

                                ArrayList<Item> itemList = new ArrayList<>();

                                int r = spinner.getSelectedItemPosition();
                                if (r != 0) {
                                    for (Item item : mainItemList) {
                                        Date date2 = Utils.changePudDate(item.getPubDate());
                                        if (roadList.get(r).equals(item.getRoad()) && date1.equals(date2)) {
                                            itemList.add(item);
                                        }
                                    }
                                } else {
                                    for (Item item : mainItemList) {
                                        Date date2 = Utils.changePudDate(item.getPubDate());
                                        Log.d(TAG, "onDateSet: date1 " + date1 + " date2 " + date2);
                                        if (date1.equals(date2)) {
                                            itemList.add(item);
                                        }
                                    }
                                }
                                setRecyclerView(itemList);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    public void setRecyclerView(ArrayList<Item> itemList) {
        if (itemList.isEmpty()) {
            Toast.makeText(MainActivity.this, "Sorry no information matched.", Toast.LENGTH_SHORT).show();
        }
        Collections.sort(itemList, (item1, item2) -> {
            Date date1 = Utils.stringToDate(item1.getPubDate());
            Date date2 = Utils.stringToDate(item2.getPubDate());

            if (date1 != null && date2 != null) {
                boolean b1;
                boolean b2;
//                if (isAscending) {
//                    b1 = date2.after(date1);
//                    b2 = date2.before(date1);
//                }else {
                b1 = date1.after(date2);
                b2 = date1.before(date2);
//                }
                if (b1 != b2) {
                    if (b1) {
                        return -1;
                    }
                    if (!b1) {
                        return 1;
                    }
                }
            }
            return 0;
        });

        itemAdapter = new ItemAdapter(MainActivity.this, itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(itemAdapter);
    }

    public void setSpinner() {
        roadList.clear();
        roads(mainItemList);

        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.item_spinner, roadList);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roadIndex = i;
                if (i != 0) {
                    ArrayList<Item> itemList = new ArrayList<>();
                    if (TextUtils.isEmpty(date)) {
                        for (Item item : mainItemList) {
                            if (roadList.get(i).equals(item.getRoad())) {
                                itemList.add(item);
                            }
                        }
                    } else {
                        Date date1 = Utils.changeStringIntoDate(date);
                        for (Item item : mainItemList) {
                            Date date2 = Utils.changePudDate(item.getPubDate());
                            if (roadList.get(i).equals(item.getRoad()) && date1.equals(date2)) {
                                itemList.add(item);
                            }
                        }
                    }

                    setRecyclerView(itemList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void getInformation() {
        if (!isNetworkAvailable()){
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    Log.d(TAG, "run: getResponseCode: " + urlConnection.getResponseCode());
                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "run: http request error");
                        return;
                    }

                    pullParser.setInput(new InputStreamReader(urlConnection.getInputStream()));
                    int eventType = pullParser.getEventType();
                    Log.d(TAG, "run: eventType: " + eventType);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tag = pullParser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tag.equalsIgnoreCase("item")) {
                                    item = new Item();
                                }
                                break;

                            case XmlPullParser.TEXT:
                                text = pullParser.getText();
                                break;

                            case XmlPullParser.END_TAG:
                                if (tag.equalsIgnoreCase("item")) {
                                    mainItemList.add(item);
                                } else if (item != null) {
                                    if (tag.equalsIgnoreCase("author")) {
                                        item.setAuthor(text);
                                    } else if (tag.equalsIgnoreCase("category")) {
                                        item.setCategory(text);
                                    } else if (tag.equalsIgnoreCase("description")) {
                                        item.setDescription(text);
                                    } else if (tag.equalsIgnoreCase("link")) {
                                        item.setLink(text);
                                    } else if (tag.equalsIgnoreCase("pubDate")) {
                                        item.setPubDate(text);
                                    } else if (tag.equalsIgnoreCase("title")) {
                                        item.setTitle(text);
                                    } else if (tag.equalsIgnoreCase("road")) {
                                        item.setRoad(text);
                                    } else if (tag.equalsIgnoreCase("region")) {
                                        item.setRegion(text);
                                    } else if (tag.equalsIgnoreCase("county")) {
                                        item.setCounty(text);
                                    } else if (tag.equalsIgnoreCase("latitude")) {
                                        item.setLatitude(text);
                                    } else if (tag.equalsIgnoreCase("longitude")) {
                                        item.setLongitude(text);
                                    }
                                }
                                break;

                            default:
                                break;
                        }
                        eventType = pullParser.next();
                        if (eventType == XmlPullParser.END_DOCUMENT) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    setRecyclerView(mainItemList);
                                    setSpinner();
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Information loaded.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }


                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "catch: " + e);
                }
            }
        });
        thread.start();
    }

    public void roads(ArrayList<Item> items) {
        Set<String> roads = new HashSet<>();
        for (Item item : items) {
            roads.add(item.getRoad());
        }
        roadList.addAll(roads);
        roadList.add(0, "Select Road");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {

            if (!isNetworkAvailable()){
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                return false;
            }

            date = null;
            tvDate.setText(date);
            setRecyclerView(mainItemList);
            setSpinner();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("date", date);
        outState.putInt("roadIndex", roadIndex);
        Preferences.setItems(MainActivity.this, mainItemList);
        Preferences.setRoads(MainActivity.this, roadList);
        Log.d(TAG, "savedInstanceState: " + date + " " + roadIndex + " " + mainItemList.size() + " " + roadList.size());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date = savedInstanceState.getString("date", null);
        roadIndex = savedInstanceState.getInt("roadIndex", 0);
        mainItemList = Preferences.getItems(MainActivity.this);
        roadList = Preferences.getRoads(MainActivity.this);
        Log.d(TAG, "onRestoreInstanceState: " + date + " " + roadIndex + " " + mainItemList.size() + " " + roadList.size());

        ArrayList<Item> itemList = new ArrayList<>();
        if (roadIndex != 0 && !TextUtils.isEmpty(date)) {
            Date date1 = Utils.changeStringIntoDate(date);
            for (Item item : mainItemList) {
                Date date2 = Utils.changePudDate(item.getPubDate());
                if (roadList.get(roadIndex).equals(item.getRoad()) && date1.equals(date2)) {
                    itemList.add(item);
                }
            }
        } else {
            itemList.addAll(mainItemList);
        }

        setRecyclerView(itemList);
        setSpinner();
        spinner.setSelection(roadIndex, true);
        tvDate.setText(date);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}