package com.videumcorp.com.baking;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;
import com.videumcorp.com.baking.fragments.RecipeDetailFragmentIngredients;
import com.videumcorp.com.baking.fragments.RecipeDetailFragmentSteps;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListActivity extends AppCompatActivity {

    public static final String JSON = "json";
    public static final String SELECTED_ITEM = "selected";
    public static final String FRAGMENT_TYPE = "fragment";
    public static final String FRAGMENT_INGREDIENT = "fragment_INGREDIENT";
    public static final String FRAGMENT_STEPS = "fragment_STEPS";
    public static final String SELECTED_STEP = "selected_step";
    public static final String SELECTED_WIDGET = "selected_widget";

    private boolean mTwoPane;
    private final List<RecipeItem> recipeItemArrayList = new ArrayList<>();
    RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    RecipeDetailRecyclerViewAdapter recipeDetailRecyclerViewAdapter;
    String response;

    int selectedItem = 0;

    //VIEWS
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewRecipeList)
    RecyclerView recyclerViewRecipeList;
    @BindView(R.id.recyclerViewRecipeDetailList)
    RecyclerView recyclerViewRecipeDetailList;
    @BindView(R.id.frameLayoutRecipeList)
    FrameLayout frameLayoutRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.card_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        simpleVolleyRequest(getResources().getString(R.string.api_url));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerViewRecipeList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerViewRecipeList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (frameLayoutRecipeList.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            showHideRecipeDetailList();
        }
    }

    private void showHideRecipeDetailList() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (frameLayoutRecipeList.getVisibility() == View.VISIBLE) {
                frameLayoutRecipeList.setVisibility(View.GONE);
                toolbar.setTitle(recipeItemArrayList.get(selectedItem).getName());
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            } else {
                toolbar.setTitle(getTitle());
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                frameLayoutRecipeList.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    private void simpleVolleyRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        this.response = response;
                        JSONArray jsonArrayAPIResponse = new JSONArray(response);

                        SharedPreferences sharedPreferences = this.getSharedPreferences(RecipeListActivity.JSON, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(JSON, response);
                        editor.apply();

                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArrayAPIResponse.length(); i++) {
                            RecipeItem recipeItem = gson.fromJson(String.valueOf(jsonArrayAPIResponse.get(i)), RecipeItem.class);
                            recipeItemArrayList.add(recipeItem);
                        }

                        assert recyclerViewRecipeList != null;

                        assert recyclerViewRecipeDetailList != null;
                        setupRecyclerViews(recyclerViewRecipeList, recyclerViewRecipeDetailList);

                        //recipeListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });
        queue.add(stringRequest);
    }


    private void setupRecyclerViews(RecyclerView recyclerView, RecyclerView recyclerView2) {
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter();
        recipeDetailRecyclerViewAdapter = new RecipeDetailRecyclerViewAdapter(this, mTwoPane);

        recyclerView2.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        if (mTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

        recyclerView.setAdapter(recipeRecyclerViewAdapter);
        recyclerView2.setAdapter(recipeDetailRecyclerViewAdapter);
    }

    public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

        ArrayList<ViewHolder> views = new ArrayList<>();

        RecipeRecyclerViewAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_item_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            views.add(holder);
            holder.cardViewRecipeItem.setOnClickListener(v -> {
                selectedItem = holder.getAdapterPosition();
                recipeDetailRecyclerViewAdapter.notifyDataSetChanged();
                showHideRecipeDetailList();
            });
            holder.textViewRecipeItemName.setText(recipeItemArrayList.get(position).getName());
            holder.textViewRecipeItemNameDescription
                    .setText(MessageFormat.format("{0} {1} and {2} {3}",
                            recipeItemArrayList.get(position).getIngredients().size(),
                            getResources().getString(R.string.ingredients),
                            recipeItemArrayList.get(position).getSteps().size(),
                            getResources().getString(R.string.steps)));
            Glide.with(getApplicationContext())
                    .load(getResources().obtainTypedArray(R.array.images).getDrawable(position))
                    .into(holder.imageViewRecipeItemImage);

            holder.appCompatButtonRecipeItemOpenRecipe.setOnClickListener(v -> {
                selectedItem = holder.getAdapterPosition();
                recipeDetailRecyclerViewAdapter.notifyDataSetChanged();
                showHideRecipeDetailList();
            });
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(RecipeListActivity.JSON, Context.MODE_PRIVATE);
            if (sharedPreferences.getInt(SELECTED_WIDGET, 0) == position) {
                holder.buttonRecipeItemWidget.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_widgets_24px));
            } else {
                holder.buttonRecipeItemWidget.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_widgets_24px));
            }
            holder.buttonRecipeItemWidget.setOnClickListener(v -> {
                for (int i = 0; i < views.size(); i++) {
                    if (position == i) {
                        views.get(i).buttonRecipeItemWidget.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_widgets_24px));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SELECTED_WIDGET, position);
                        editor.apply();
                    } else {
                        views.get(i).buttonRecipeItemWidget.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_widgets_24px));
                    }
                }
                Intent intent = new Intent(getApplicationContext(), RecipeAppWidget.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipeAppWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);
            });
        }

        @Override
        public int getItemCount() {
            return recipeItemArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.cardViewRecipeItem)
            CardView cardViewRecipeItem;
            @BindView(R.id.textViewRecipeItemName)
            TextView textViewRecipeItemName;
            @BindView(R.id.buttonRecipeItemOpenRecipe)
            AppCompatButton appCompatButtonRecipeItemOpenRecipe;
            @BindView(R.id.buttonRecipeItemWidget)
            ImageButton buttonRecipeItemWidget;
            @BindView(R.id.textViewRecipeItemNameDescription)
            TextView textViewRecipeItemNameDescription;
            @BindView(R.id.imageViewRecipeItemImage)
            ImageView imageViewRecipeItemImage;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }


    public class RecipeDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecipeDetailRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private final boolean mTwoPane;

        private final static int INGREDIENTS = 0;
        private final static int STEPS = 1;

        RecipeDetailRecyclerViewAdapter(RecipeListActivity parent, boolean twoPane) {
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = 0;
            switch (viewType) {
                case INGREDIENTS:
                    layout = R.layout.recipe_item_ingredients;
                    break;
                case STEPS:
                    layout = R.layout.recipe_item_steps;
                    break;
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case INGREDIENTS:
                    if (holder.textViewRecipeIngredientItemIngredient != null) {
                        holder.textViewRecipeIngredientItemIngredient.setText(R.string.ingredients_list);
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(JSON, response);
                            arguments.putInt(SELECTED_ITEM, selectedItem);
                            arguments.putString(FRAGMENT_TYPE, FRAGMENT_INGREDIENT);
                            RecipeDetailFragmentIngredients fragment = new RecipeDetailFragmentIngredients();
                            fragment.setArguments(arguments);
                            mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
                        }
                        holder.cardViewRecipeIngredientItem.setOnClickListener(v -> {
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(JSON, response);
                                arguments.putInt(SELECTED_ITEM, selectedItem);
                                arguments.putString(FRAGMENT_TYPE, FRAGMENT_INGREDIENT);
                                RecipeDetailFragmentIngredients fragment = new RecipeDetailFragmentIngredients();
                                fragment.setArguments(arguments);
                                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, RecipeDetailActivity.class);
                                intent.putExtra(JSON, response);
                                intent.putExtra(SELECTED_ITEM, selectedItem);
                                intent.putExtra(FRAGMENT_TYPE, FRAGMENT_INGREDIENT);
                                context.startActivity(intent);
                            }
                        });
                        holder.appCompatButtonRecipeIngredientItemOpen.setOnClickListener(v -> {
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(JSON, response);
                                arguments.putInt(SELECTED_ITEM, selectedItem);
                                arguments.putString(FRAGMENT_TYPE, FRAGMENT_INGREDIENT);
                                RecipeDetailFragmentIngredients fragment = new RecipeDetailFragmentIngredients();
                                fragment.setArguments(arguments);
                                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, RecipeDetailActivity.class);
                                intent.putExtra(JSON, response);
                                intent.putExtra(SELECTED_ITEM, selectedItem);
                                intent.putExtra(FRAGMENT_TYPE, FRAGMENT_INGREDIENT);
                                context.startActivity(intent);
                            }
                        });
                    }
                    break;
                case STEPS:
                    if (holder.textViewRecipeStepItemShortDescription != null) {
                        holder.textViewRecipeStepItemShortDescription.setText(recipeItemArrayList.get(selectedItem).getSteps().get(position - 1).getShortDescription());
                        holder.cardViewRecipeStepItem.setOnClickListener(v -> {
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(JSON, response);
                                arguments.putInt(SELECTED_ITEM, selectedItem);
                                arguments.putString(FRAGMENT_TYPE, FRAGMENT_STEPS);
                                arguments.putInt(SELECTED_STEP, position - 1);
                                RecipeDetailFragmentSteps fragment = new RecipeDetailFragmentSteps();
                                fragment.setArguments(arguments);
                                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, RecipeDetailActivity.class);
                                intent.putExtra(JSON, response);
                                intent.putExtra(SELECTED_ITEM, selectedItem);
                                intent.putExtra(FRAGMENT_TYPE, FRAGMENT_STEPS);
                                intent.putExtra(SELECTED_STEP, position - 1);
                                context.startActivity(intent);
                            }
                        });
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return INGREDIENTS;
                case 1:
                    return STEPS;
                default:
                    return STEPS;
            }
        }

        @Override
        public int getItemCount() {
            return recipeItemArrayList.get(selectedItem).getSteps().size() + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardViewRecipeIngredientItem;
            CardView cardViewRecipeStepItem;
            TextView textViewRecipeIngredientItemIngredient;
            TextView textViewRecipeStepItemShortDescription;
            AppCompatButton appCompatButtonRecipeIngredientItemOpen;

            ViewHolder(View view) {
                super(view);
                cardViewRecipeIngredientItem = view.findViewById(R.id.cardViewRecipeIngredientItem);
                cardViewRecipeStepItem = view.findViewById(R.id.cardViewRecipeStepItem);
                textViewRecipeIngredientItemIngredient = view.findViewById(R.id.textViewRecipeIngredientItemIngredient);
                textViewRecipeStepItemShortDescription = view.findViewById(R.id.textViewRecipeStepItemShortDescription);
                appCompatButtonRecipeIngredientItemOpen = view.findViewById(R.id.appCompatButtonRecipeIngredientItemView);
            }
        }
    }
}
