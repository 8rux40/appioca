package com.tardin.appioca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.tardin.appioca.adapter.RecentRecipesAdapter;
import com.tardin.appioca.entity.Recipe;
import com.tardin.appioca.listener.OpenRecipeListener;
import com.tardin.appioca.repository.RecipeRepository;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Recipe> recipes;
    public static View.OnClickListener openRecentRecipe;
    private static RecyclerView recyclerViewRecentRecipes;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView ivUserPhoto, ivMyRecipes, ivFavoriteRecipes;
    private Button btnCreateNewRecipe;
    private RecentRecipesAdapter recentRecipesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mapActivityComponents();
        mapActivityComponentsMethods();
        loadRecentRecipesFromFirebase();
        loadUserPhoto();
    }

    private void loadRecentRecipesFromFirebase(){
        RecipeRepository.connect();
        RecipeRepository.firebaseRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren())
                    recipes.add(data.getValue(Recipe.class));
                recentRecipesAdapter = new RecentRecipesAdapter(recipes);
                recyclerViewRecentRecipes.setAdapter(recentRecipesAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void mapActivityComponents() {
        mapToolbarComponents();
        mapRecentRecipesComponents();
        mapCreateNewRecipeComponents();
    }

    private void mapCreateNewRecipeComponents() {
        btnCreateNewRecipe = findViewById(R.id.btnCreateNewRecipe);
    }

    private void mapToolbarComponents() {
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        ivMyRecipes = findViewById(R.id.ivMyRecipes);
        ivFavoriteRecipes = findViewById(R.id.ivFavoriteRecipes);
    }

    private void mapRecentRecipesComponents() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        recyclerViewRecentRecipes = findViewById(R.id.recyclerViewRecentRecipes);
        recyclerViewRecentRecipes.setHasFixedSize(true);
        recyclerViewRecentRecipes.setLayoutManager(layoutManager);
        recyclerViewRecentRecipes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRecentRecipes.setAdapter(new RecentRecipesAdapter(new ArrayList<>()));
        openRecentRecipe = new OpenRecipeListener(this, recyclerViewRecentRecipes);
    }

    private void mapActivityComponentsMethods() {
        mapToolbarComponentsMethods();
        mapCreateNewRecipeComponentsMethods();
    }

    private void mapToolbarComponentsMethods() {
        ivUserPhoto.setOnClickListener(view -> startActivity(new Intent(getBaseContext(), UserActivity.class)));
        ivMyRecipes.setOnClickListener(view -> startMyRecipesActivity());
//        ivFavoriteRecipes.setOnClickListener(view -> startActivity(new Intent(context, FavoriteRecipes.class)));
    }

    private void mapCreateNewRecipeComponentsMethods() {
        btnCreateNewRecipe.setOnClickListener(view ->
                startActivity(new Intent(getBaseContext(), CreateNewRecipeActivity.class)));
    }

    private void loadUserPhoto() {
        Glide.with(this).load(currentUser.getPhotoUrl()).into(ivUserPhoto);
    }

    private void startMyRecipesActivity() {
        Intent intent = new Intent(this, MyRecipesActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("recipes", true);
        bundle.putSerializable("recipes", recipes);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}