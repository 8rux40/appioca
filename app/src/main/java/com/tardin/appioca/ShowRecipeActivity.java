package com.tardin.appioca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tardin.appioca.adapter.RecentRecipesAdapter;
import com.tardin.appioca.adapter.RecipeIngredientsAdapter;
import com.tardin.appioca.adapter.RecipePreparationStepsAdapter;
import com.tardin.appioca.entity.Recipe;

import java.io.File;
import java.util.ArrayList;

public class ShowRecipeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Intent intent;
    Bundle bundle;
    Recipe recipe;

    private static RecyclerView rvRecipeIngredients, rvRecipePreparationSteps;
    private LinearLayoutManager linearLayoutManager;

    TextView tvRecipeTitle, tvRecipeAuthor, tvRecipeDescription, tvRecipePreparationTime,
        tvRecipeYield, tvRecipeFavorites;

    private ImageView ivUserPhoto, ivMyRecipes, ivRecipePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mapActivityComponents();
        mapActivityComponentsMethods();
        getRecipeFromPreviousActivity();
        fillComponentsWithRecipeInformation();
        loadUserPhoto();
    }

    private void getRecipeFromPreviousActivity() {
        intent = getIntent();
        bundle = intent.getExtras();
        recipe = (Recipe) bundle.getSerializable("recipe");
    }

    private void mapActivityComponents(){
        mapToolbarComponents();
        mapRecipeDetailsComponents();
        mapRecipeIngredientsComponents();
        mapRecipePreparationStepsComponents();
    }

    private void mapActivityComponentsMethods() {
        mapToolbarComponentsMethods();
    }

    private void mapRecipePreparationStepsComponents() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRecipePreparationSteps = findViewById(R.id.rvRecipePreparationSteps);
        rvRecipePreparationSteps.setHasFixedSize(true);
        rvRecipePreparationSteps.setLayoutManager(linearLayoutManager);
        rvRecipePreparationSteps.setItemAnimator(new DefaultItemAnimator());
    }

    private void mapRecipeIngredientsComponents() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRecipeIngredients = findViewById(R.id.rvRecipeIngredients);
        rvRecipeIngredients.setHasFixedSize(true);
        rvRecipeIngredients.setLayoutManager(linearLayoutManager);
        rvRecipeIngredients.setItemAnimator(new DefaultItemAnimator());
    }

    private void mapRecipeDetailsComponents() {
        tvRecipePreparationTime = findViewById(R.id.tvRecipePreparationTime2);
        tvRecipeYield = findViewById(R.id.tvRecipeYield2);
        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvRecipeAuthor = findViewById(R.id.tvRecipeAuthor);
        tvRecipeDescription = findViewById(R.id.tvRecipeDescription);
        tvRecipeFavorites = findViewById(R.id.tvRecipeFavorites);
        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
    }

    private void mapToolbarComponents() {
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        ivMyRecipes = findViewById(R.id.ivMyRecipes);
    }

    private void mapToolbarComponentsMethods() {
        ivUserPhoto.setOnClickListener(view -> startActivity(new Intent(getBaseContext(), UserActivity.class)));
        ivMyRecipes.setOnClickListener(view -> {
            startActivity(new Intent(getBaseContext(), MyRecipesActivity.class));
            finish();
        });
    }

    private void fillComponentsWithRecipeInformation() {
        tvRecipeTitle.setText(recipe.getName());
        tvRecipeDescription.setText(recipe.getDescription());
        tvRecipeAuthor.setText(String.format("por %s", recipe.getAuthor()));
        tvRecipePreparationTime.setText(recipe.getPreparationTimeInString());
        tvRecipeYield.setText(recipe.getYieldInString());
        rvRecipeIngredients.setAdapter(new RecipeIngredientsAdapter(recipe.getIngredients()));
        rvRecipePreparationSteps.setAdapter(new RecipePreparationStepsAdapter(recipe.getPreparationSteps()));
        loadRecipePhoto();
        // favoritos implementar
    }

    private void loadRecipePhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(recipe.getPhoto());
        final File file = new File(getBaseContext().getFilesDir(),"DownloadedImage.png");
        httpsReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivRecipePhoto.setImageBitmap(bitmap);
        }).addOnFailureListener(exception ->
                Log.e("ShowRecipe:photo", "Erro ao carregar imagem.", exception));
    }

    private void loadUserPhoto(){
        Glide.with(this).load(currentUser.getPhotoUrl()).into(ivUserPhoto);
    }

}