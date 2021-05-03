package com.tardin.appioca.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Option;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tardin.appioca.entity.Recipe;

import java.util.Optional;

public class RecipeRepository {
    public static FirebaseDatabase database;
    public static DatabaseReference firebaseRecipes;
    public static FirebaseUser user;

    public static void connect() {
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseRecipes = database.getReference("recipes");
    }

    public static Recipe save(Recipe recipe) {
        connect();
        String id = firebaseRecipes.push().getKey();
        recipe.setId(id);
        firebaseRecipes.child(recipe.getId()).setValue(recipe);
        return recipe;
    }

    public static void update(Recipe recipe) {
        connect();
        firebaseRecipes.child(recipe.getId()).setValue(recipe);
    }

    public static void delete(Recipe recipe) {
        connect();
        firebaseRecipes.child(recipe.getId()).removeValue();
    }

//    public static Optional<Recipe> getById(String id){
//        Log.i("Recipe:getByID", "id: " + id);
//        recipe = new Recipe();
//        firebaseRecipes.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                } else {
//                    Log.i("Recipe:getByID", String.valueOf(task.getResult().getValue(Recipe.class)));
//                    teste(task.getResult().getValue(Recipe.class));
//                }
//            }
//        });
////        Log.i("Recipe:teste nulo", String.valueOf(foundRecipe[0] == null));
////        Log.i("Recipe:valor", String.valueOf(foundRecipe[0]));
////        Log.i("Recipe:teste recipe", String.valueOf(recipe == null));
//        Log.i("Recipe:valor recipe", String.valueOf(recipe));
////        if (recipe)
////            return Optional.empty();
//        return Optional.of(recipe);
//    }

//    private Recipe findRecipeById(String recipeId){
//        Recipe[] recipe = new Recipe[1];
//        RecipeRepository.firebaseRecipes.child("recipes").equalTo(recipeId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                recipe[0] = snapshot.getValue(Recipe.class);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                //
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                //
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                //
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                //
//            }
//
//        });
//        return recipe[0];
//    }
}
