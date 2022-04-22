package org.me.gcu.nnadi_emma_s1906614;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Preferences {

    private static final String PREFS_TAG = "nnadi_emma_s1906614.pref";
    private static final String ITEMS_TAG = "Items";
    private static final String ROADS_TAG = "Roads";

    public static void setItems(Context context, List<Item> itemList) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(itemList);
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ITEMS_TAG, jsonCurProduct);
        editor.apply();
    }

    public static ArrayList<Item> getItems(Context context) {
        Gson gson = new Gson();
        ArrayList<Item> items = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ITEMS_TAG, "");
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        items = gson.fromJson(jsonPreferences, type);
        return items;
    }

    public static void setRoads(Context context, List<String> stringList) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(stringList);
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ROADS_TAG, jsonCurProduct);
        editor.apply();
    }

    public static ArrayList<String> getRoads(Context context) {
        Gson gson = new Gson();
        ArrayList<String> strings = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ROADS_TAG, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        strings = gson.fromJson(jsonPreferences, type);
        return strings;
    }
}
