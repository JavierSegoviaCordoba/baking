package com.videumcorp.com.baking.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.videumcorp.com.baking.Gsons.RecipeItem;
import com.videumcorp.com.baking.R;
import com.videumcorp.com.baking.RecipeListActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragmentSteps extends Fragment {

    private final List<RecipeItem> recipeItemArrayList = new ArrayList<>();

    @BindView(R.id.playerView)
    PlayerView playerView;
    @BindView(R.id.constrainLayout)
    ConstraintLayout constrainLayout;
    @BindView(R.id.textViewShortDescription)
    TextView textViewShortDescription;
    @BindView(R.id.textViewDescription)
    TextView textViewDescription;
    @BindView(R.id.buttonPrevStep)
    AppCompatButton buttonPrevStep;
    @BindView(R.id.buttonNextStep)
    AppCompatButton buttonNextStep;

    SimpleExoPlayer player;

    Dialog dialog;

    boolean playerViewFullscreen = false;
    int selectedItem, selectedStep;
    String videoUrl = "";
    String shortDescription = "";
    String description = "";

    int playerViewHeight;

    Bundle bundle;

    public RecipeDetailFragmentSteps() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        bundle = getArguments();

        if (bundle.containsKey(RecipeListActivity.JSON)) {
            JSONArray jsonArrayRecipeList;

            try {
                jsonArrayRecipeList = new JSONArray(bundle.getString(RecipeListActivity.JSON));

                Gson gson = new Gson();
                for (int i = 0; i < jsonArrayRecipeList.length(); i++) {
                    RecipeItem recipeItem = gson.fromJson(String.valueOf(jsonArrayRecipeList.get(i)), RecipeItem.class);
                    recipeItemArrayList.add(recipeItem);
                }

                selectedItem = (bundle.getInt(RecipeListActivity.SELECTED_ITEM));
                selectedStep = (bundle.getInt(RecipeListActivity.SELECTED_STEP));

                videoUrl = recipeItemArrayList.get(selectedItem).getSteps().get(selectedStep).getVideoURL();
                shortDescription = recipeItemArrayList.get(selectedItem).getSteps().get(selectedStep).getShortDescription();
                description = recipeItemArrayList.get(selectedItem).getSteps().get(selectedStep).getDescription();

                Activity activity = this.getActivity();
                assert activity != null;
                CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout
                            .setTitle(recipeItemArrayList.get(selectedItem).getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_detail_steps, container, false);
        ButterKnife.bind(this, view);

        if (!videoUrl.equals("")) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection
                    .Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory
                    .newSimpleInstance(view.getContext(), trackSelector);

            playerView.setPlayer(player);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(view.getContext(),
                    Util.getUserAgent(view.getContext(),
                            view.getResources().getString(R.string.app_name)), bandwidthMeter);

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl));
            player.prepare(videoSource);

            initFullscreenDialog();
        } else {
            playerView.setVisibility(View.GONE);
        }

        textViewShortDescription.setText(shortDescription);

        textViewDescription.setText(description);

        if (selectedStep == 0) {
            buttonPrevStep.setVisibility(View.GONE);
        } else {
            buttonPrevStep.setVisibility(View.VISIBLE);
        }

        if (selectedStep == recipeItemArrayList.get(selectedItem).getSteps().size() - 1) {
            buttonNextStep.setVisibility(View.GONE);
        } else {
            buttonNextStep.setVisibility(View.VISIBLE);
        }

        buttonPrevStep.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putString(RecipeListActivity.JSON, bundle.getString(RecipeListActivity.JSON));
            bundle2.putInt(RecipeListActivity.SELECTED_ITEM, selectedItem);
            bundle2.putString(RecipeListActivity.FRAGMENT_TYPE, RecipeListActivity.FRAGMENT_STEPS);
            bundle2.putInt(RecipeListActivity.SELECTED_STEP, selectedStep - 1);
            RecipeDetailFragmentSteps fragment = new RecipeDetailFragmentSteps();
            fragment.setArguments(bundle2);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
        });

        buttonNextStep.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putString(RecipeListActivity.JSON, bundle.getString(RecipeListActivity.JSON));
            bundle2.putInt(RecipeListActivity.SELECTED_ITEM, selectedItem);
            bundle2.putString(RecipeListActivity.FRAGMENT_TYPE, RecipeListActivity.FRAGMENT_STEPS);
            bundle2.putInt(RecipeListActivity.SELECTED_STEP, selectedStep + 1);
            RecipeDetailFragmentSteps fragment = new RecipeDetailFragmentSteps();
            fragment.setArguments(bundle2);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.card_detail_container, fragment).commit();
        });

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                openFullscreenDialog();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                closeFullscreenDialog();
            }
        }
    }

    private void initFullscreenDialog() {

        dialog = new Dialog(Objects.requireNonNull(getActivity()), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (playerViewFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        playerViewHeight = playerView.getHeight();
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        dialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        playerViewFullscreen = true;
        dialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        playerView.setMinimumHeight(playerViewHeight);
        constrainLayout.addView(playerView);
        playerViewFullscreen = false;
        dialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        recipeItemArrayList.clear();
        if (player != null) {
            player.release();
        }
    }
}
