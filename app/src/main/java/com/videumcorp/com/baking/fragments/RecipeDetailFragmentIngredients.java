package com.videumcorp.com.baking.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;
import com.videumcorp.com.baking.R;
import com.videumcorp.com.baking.RecipeListActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragmentIngredients extends Fragment {

    private final List<RecipeItem> recipeItemArrayList = new ArrayList<>();

    public RecipeDetailFragmentIngredients() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;

        if (getArguments().containsKey(RecipeListActivity.JSON)) {

            JSONArray jsonArrayRecipeList;
            try {
                jsonArrayRecipeList = new JSONArray(getArguments().getString(RecipeListActivity.JSON));

                Gson gson = new Gson();
                for (int i = 0; i < jsonArrayRecipeList.length(); i++) {
                    RecipeItem recipeItem = gson.fromJson(String.valueOf(jsonArrayRecipeList.get(i)), RecipeItem.class);
                    recipeItemArrayList.add(recipeItem);
                }

                Activity activity = this.getActivity();
                assert activity != null;
                CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(recipeItemArrayList
                            .get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM))
                            .getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail_ingredients, container, false);

        TextView textViewIngredientList = rootView.findViewById(R.id.textViewIngredientList);
        StringBuilder ingredients = new StringBuilder();
        assert getArguments() != null;
        for (int i = 0; i < recipeItemArrayList.get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM)).getIngredients().size(); i++) {
            ingredients
                    .append(recipeItemArrayList
                            .get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM))
                            .getIngredients().get(i)
                            .getIngredient())
                    .append(", ")
                    .append(recipeItemArrayList
                            .get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM))
                            .getIngredients().get(i)
                            .getQuantity())
                    .append(" (")
                    .append(recipeItemArrayList
                            .get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM))
                            .getIngredients().get(i)
                            .getMeasure())
                    .append(").")
                    .append("\n\n");
        }

        textViewIngredientList.setText(ingredients.toString());

        return rootView;
    }
}
