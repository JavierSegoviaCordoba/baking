package com.videumcorp.com.baking.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;
import com.videumcorp.com.baking.R;
import com.videumcorp.com.baking.RecipeListActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragmentSteps extends Fragment {

    private final List<RecipeItem> recipeItemArrayList = new ArrayList<>();

    public RecipeDetailFragmentSteps() {
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
                    appBarLayout.setTitle(recipeItemArrayList.get(getArguments().getInt(RecipeListActivity.SELECTED_ITEM)).getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail_steps, container, false);

        //fixme arreglar aquí lo que se muestra en la vista detalle (ingredientes, vídeo, paso en concreto, etc)

        // Show the dummy content as text in a TextView.
        /*if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.card_detail)).setText(mItem.details);
        }*/

        return rootView;
    }
}
