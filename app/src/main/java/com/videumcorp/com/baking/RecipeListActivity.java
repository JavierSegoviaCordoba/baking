package com.videumcorp.com.baking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;
import com.videumcorp.com.baking.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Cards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private final List<RecipeItem> recipeItemArrayList = new ArrayList<>();
    RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    RecipeDetailRecyclerViewAdapter recipeDetailRecyclerViewAdapter;

    int selectedItem = 0;

    //VIEWS
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewRecipeList)
    RecyclerView recyclerViewRecipeList;
    @BindView(R.id.recyclerViewRecipeDetailList)
    RecyclerView recyclerViewRecipeDetailList;
    @BindView(R.id.frameLayoutRecipeList)
    FrameLayout frameLayoutRecupeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        floatingActionButton.setOnClickListener(view -> {
            if (frameLayoutRecupeList.getVisibility() == View.VISIBLE) {
                frameLayoutRecupeList.setVisibility(View.GONE);
            } else {
                frameLayoutRecupeList.setVisibility(View.VISIBLE);
            }
        });


        if (findViewById(R.id.card_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        simpleVolleyRequest(getResources().getString(R.string.api_url));
    }

    private void simpleVolleyRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArrayAPIResponse = new JSONArray(response);
                        //JSONObject jsonObjectAPIResponse = new JSONObject(response);
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArrayAPIResponse.length(); i++) {
                            RecipeItem recipeItem = gson.fromJson(String.valueOf(jsonArrayAPIResponse.get(i)), RecipeItem.class);
                            recipeItemArrayList.add(recipeItem);
                            Log.d("simpleVolleyRequest: ", recipeItem.getName());
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
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(recipeItemArrayList);
        recipeDetailRecyclerViewAdapter = new RecipeDetailRecyclerViewAdapter(this, recipeItemArrayList, mTwoPane);

        recyclerView.setAdapter(recipeRecyclerViewAdapter);
        recyclerView2.setAdapter(recipeDetailRecyclerViewAdapter);
    }

    public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {
        private final List<RecipeItem> recipeItemArrayList;

        RecipeRecyclerViewAdapter(List<RecipeItem> recipeItemArrayList) {
            this.recipeItemArrayList = recipeItemArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.cardViewRecipeItem.setOnClickListener(v -> {
                selectedItem = holder.getAdapterPosition();
                recipeDetailRecyclerViewAdapter.notifyDataSetChanged();
            });
            holder.textViewRecipeItemID.setText(String.valueOf(recipeItemArrayList.get(position).getId()));
            holder.textViewRecipeItemName.setText(recipeItemArrayList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return recipeItemArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.cardViewRecipeItem)
            CardView cardViewRecipeItem;
            @BindView(R.id.textViewRecipeItemID)
            TextView textViewRecipeItemID;
            @BindView(R.id.textViewRecipeItemName)
            TextView textViewRecipeItemName;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }


    public class RecipeDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecipeDetailRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private final List<RecipeItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, item.id);
                    RecipeDetailFragment fragment = new RecipeDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.card_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra(RecipeDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };
        private final static int INGREDIENTS = 0;
        private final static int STEPS = 1;

        RecipeDetailRecyclerViewAdapter(RecipeListActivity parent,
                                        List<RecipeItem> items,
                                        boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = 0;
            switch (viewType) {
                case INGREDIENTS:
                    layout = R.layout.recipe_ingredient_item;
                    break;
                case STEPS:
                    layout = R.layout.recipe_step_item;
                    break;
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            //fixme toolbar.setTitle(recipeItemArrayList.get(selectedItem).getName()); -- colocar solo cuando esté oculto el recyclerview principal
            switch (getItemViewType(position)) {
                case INGREDIENTS:
                    if (holder.textViewRecipeIngredientItemIngredient != null) {
                        holder.textViewRecipeIngredientItemIngredient.setText("Ingredients list");
                        holder.cardViewRecipeIngredientItem.setOnClickListener(v -> {
                            //fixme arreglar fragmento y actividad para mostrar los datos correctos
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, String.valueOf(recipeItemArrayList.get(selectedItem).getId()));
                                RecipeDetailFragment fragment = new RecipeDetailFragment();
                                fragment.setArguments(arguments);
                                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, RecipeDetailActivity.class);
                                intent.putExtra(RecipeDetailFragment.ARG_ITEM_ID, String.valueOf(recipeItemArrayList.get(selectedItem).getId()));
                                context.startActivity(intent);
                            }
                        });
                    }
                    break;
                case STEPS:
                    if (holder.textViewRecipeStepItemShortDescription != null) {
                        holder.textViewRecipeStepItemShortDescription.setText(recipeItemArrayList.get(selectedItem).getSteps().get(position - 1).getShortDescription());
                    }
                    break;
            }

            //holder.itemView.setOnClickListener(mOnClickListener);
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

            ViewHolder(View view) {
                super(view);
                cardViewRecipeIngredientItem = view.findViewById(R.id.cardViewRecipeIngredientItem);
                cardViewRecipeStepItem = view.findViewById(R.id.cardViewRecipeStepItem);
                textViewRecipeIngredientItemIngredient = view.findViewById(R.id.textViewRecipeIngredientItemIngredient);
                textViewRecipeStepItemShortDescription = view.findViewById(R.id.textViewRecipeStepItemShortDescription);
            }
        }
    }

}
