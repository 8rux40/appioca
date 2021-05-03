package com.tardin.appioca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tardin.appioca.adapter.MyRecipesAdapter;
import com.tardin.appioca.adapter.RecentRecipesAdapter;
import com.tardin.appioca.entity.Recipe;
import com.tardin.appioca.listener.OpenRecipeListener;
import com.tardin.appioca.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MyRecipesActivity extends AppCompatActivity {

    public static ArrayList<Recipe> recipes;
    public static View.OnClickListener openRecipe;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView ivUserPhoto;
    private RecyclerView rvMyRecipes;
    private RecyclerView.LayoutManager layoutManager;
    private MyRecipesAdapter recipeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mapActivityComponents();
        mapActivityComponentsMethods();
        loadMyRecipes();
        loadUserPhoto();

    }

    private void loadMyRecipes() {
        if (areRecipesCached())
            loadMyRecipesFromCache();
        else
            loadMyRecipesFromFirebase();
    }

    private boolean areRecipesCached(){
        return getIntent().hasExtra("recipes");
    }

    private void loadMyRecipesFromCache(){
        recipes = ((ArrayList<Recipe>) getIntent().getExtras().getSerializable("recipes"));
        recipes = recipes.stream()
                .filter(recipe -> recipe.getAuthorId()
                        .equals(currentUser.getUid()))
                .collect(Collectors.toCollection(ArrayList::new));
        recipeListAdapter = new MyRecipesAdapter(recipes);
        rvMyRecipes.setAdapter(recipeListAdapter);
    }

    private void loadMyRecipesFromFirebase(){
        RecipeRepository.connect();
        RecipeRepository.firebaseRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes = new ArrayList<>();
                snapshot.getChildren().forEach(data -> {
                    Recipe recipe = data.getValue(Recipe.class);
                    if (recipe.getAuthorId().equals(currentUser.getUid()))
                        recipes.add(recipe);
                });
                recipeListAdapter = new MyRecipesAdapter(recipes);
                rvMyRecipes.setAdapter(recipeListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    private void mapActivityComponents() {
        mapToolbarComponents();
        mapRecipeListComponents();
    }

    private void mapToolbarComponents(){
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
    }

    private void mapRecipeListComponents(){
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        rvMyRecipes.setHasFixedSize(true);
        rvMyRecipes.setLayoutManager(layoutManager);
        rvMyRecipes.setItemAnimator(new DefaultItemAnimator());
        openRecipe = new OpenRecipeListener(this, rvMyRecipes);
    }

    private void mapActivityComponentsMethods() {
        mapToolbarComponentsMethods();
        mapRecipeListComponentsMethods();
    }

    private void mapToolbarComponentsMethods() {
        ivUserPhoto.setOnClickListener(view -> startActivity(new Intent(this, UserActivity.class)));
        // ivFavoriteRecipes.setOnClickListener(view -> startActivity(new Intent(context, FavoriteRecipes.class)));
    }

    private void mapRecipeListComponentsMethods() {
        rvMyRecipes.setAdapter(new RecentRecipesAdapter(new ArrayList<>()));
    }

    private void loadUserPhoto() {
        Glide.with(this).load(currentUser.getPhotoUrl()).into(ivUserPhoto);
    }

}