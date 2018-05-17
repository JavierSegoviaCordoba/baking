package com.videumcorp.com.baking;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        SharedPreferences sharedPreferences = context.getSharedPreferences(RecipeListActivity.JSON, Context.MODE_PRIVATE);
        String response = sharedPreferences.getString(RecipeListActivity.JSON, "");
        int selectedWidget = sharedPreferences.getInt(RecipeListActivity.SELECTED_WIDGET, 0);

        ArrayList<RecipeItem> listItemList = new ArrayList<>();
        JSONArray jsonArrayAPIResponse;
        try {
            jsonArrayAPIResponse = new JSONArray(response);
            Gson gson = new Gson();
            for (int i = 0; i < jsonArrayAPIResponse.length(); i++) {
                RecipeItem recipeItem = gson.fromJson(String.valueOf(jsonArrayAPIResponse.get(i)), RecipeItem.class);
                listItemList.add(recipeItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        remoteViews.setTextViewText(R.id.textViewNameWidget, listItemList.get(selectedWidget).getName());
        remoteViews.setRemoteAdapter(R.id.listView, svcIntent);
        remoteViews.setEmptyView(R.id.listView, R.id.listView);
        return remoteViews;
    }

}

