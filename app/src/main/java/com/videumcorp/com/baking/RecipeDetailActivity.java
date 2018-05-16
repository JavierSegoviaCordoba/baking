package com.videumcorp.com.baking;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.videumcorp.com.baking.fragments.RecipeDetailFragmentIngredients;
import com.videumcorp.com.baking.fragments.RecipeDetailFragmentSteps;

import java.util.Objects;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(RecipeListActivity.JSON, getIntent().getStringExtra(RecipeListActivity.JSON));
            arguments.putInt(RecipeListActivity.SELECTED_ITEM, getIntent().getIntExtra(RecipeListActivity.SELECTED_ITEM, 0));
            arguments.putString(RecipeListActivity.FRAGMENT_TYPE, getIntent().getStringExtra(RecipeListActivity.FRAGMENT_TYPE));
            if (Objects.requireNonNull(arguments.getString(RecipeListActivity.FRAGMENT_TYPE)).equals(RecipeListActivity.FRAGMENT_INGREDIENT)) {
                RecipeDetailFragmentIngredients fragment = new RecipeDetailFragmentIngredients();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().add(R.id.card_detail_container, fragment).commit();
            } else {
                RecipeDetailFragmentSteps fragment = new RecipeDetailFragmentSteps();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().add(R.id.card_detail_container, fragment).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
