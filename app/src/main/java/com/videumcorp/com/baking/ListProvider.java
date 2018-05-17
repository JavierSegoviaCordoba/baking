package com.videumcorp.com.baking;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<RecipeItem> listItemList = new ArrayList<>();
    private Context context;
    private int appWidgetId;
    private int selectedWidget = 0;

    ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {

        SharedPreferences sharedPreferences = context.getSharedPreferences(RecipeListActivity.JSON, Context.MODE_PRIVATE);
        String response = sharedPreferences.getString(RecipeListActivity.JSON, "");
        selectedWidget = sharedPreferences.getInt(RecipeListActivity.SELECTED_WIDGET, 0);
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
    }

    @Override
    public int getCount() {
        return listItemList.get(selectedWidget).getIngredients().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.recipe_item_widget);
        RecipeItem listItem = listItemList.get(selectedWidget);
        remoteView.setTextViewText(R.id.textViewRecipeItemName, listItem.getIngredients().get(position).getIngredient());

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}