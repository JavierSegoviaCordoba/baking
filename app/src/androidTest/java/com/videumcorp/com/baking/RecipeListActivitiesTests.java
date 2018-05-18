package com.videumcorp.com.baking;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivitiesTests {

    private SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<RecipeListActivity> recipeListActivityActivityTestRule
            = new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void getSharedPreferences() {
        Context context = getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(RecipeListActivity.JSON, Context.MODE_PRIVATE);
    }

    @Test
    public void completeTest() {

        String response = sharedPreferences.getString(RecipeListActivity.JSON, "");

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

        //Check click on the first item recyclierVire and change frameLayout visibility to VISIBLE (this last only on phones)
        onView(withId(R.id.recyclerViewRecipeList)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(listItemList.get(0).getName())), click()));

        //Check if it is visible on phones
        if (!recipeListActivityActivityTestRule.getActivity().getResources().getBoolean(R.bool.isTablet)) {
            onView(withId(R.id.frameLayout)).check(matches(isDisplayed()));
        }

        //Check if the secondRecyclerView has the first item (Ingredient list)
        onView(withId(R.id.recyclerViewRecipeDetailList)).check(matches(hasDescendant(withText(R.string.ingredients_list))));

        //Check if the secondRecyclerViews has first step
        onView(withId(R.id.recyclerViewRecipeDetailList)).check(matches(hasDescendant(withText(listItemList.get(0).getSteps().get(0).getShortDescription()))));

        //Press back to navigate to the first activigty
        onView(isRoot()).perform(ViewActions.pressBack());
    }
}
