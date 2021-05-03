package com.tardin.appioca.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.tardin.appioca.MainActivity;
import com.tardin.appioca.MyRecipesActivity;
import com.tardin.appioca.R;
import com.tardin.appioca.ShowRecipeActivity;
import com.tardin.appioca.entity.Recipe;

import java.util.Optional;

public class OpenRecipeListener implements View.OnClickListener {
    private final Context context;
    private final RecyclerView recyclerView;

    public OpenRecipeListener(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onClick(View view) {
        openRecipe(view);
    }

    private void openRecipe(View view) {
        //startActivity(new Intent(context, ShowRecipeActivity.class));
        int selectedRecipePosition = recyclerView.getChildLayoutPosition(view);
        Log.i("Recipe selectedRecipePosition", String.valueOf(selectedRecipePosition));
        RecyclerView.ViewHolder viewHolder = recyclerView
                .findViewHolderForLayoutPosition(selectedRecipePosition);
//            ((TextView) viewHolder.itemView.findViewById(R.id.tvRecipeId)).getText().toString();
//        Log.i("Recipe", ((TextView) viewHolder.itemView.findViewById(R.id.tvRecipeId)).getText().toString());
//        Log.i("Recipe", ((TextView) viewHolder.itemView.findViewById(R.id.tvRecipeName)).getText().toString());
//        Log.i("Recipe", ((TextView) viewHolder.itemView.findViewById(R.id.tvRecipePreparationTime)).getText().toString());
//        Log.i("Recipe", ((TextView) viewHolder.itemView.findViewById(R.id.tvRecipeYield)).getText().toString());
        Log.i("Recipe:viewHolder_getItemID", String.valueOf(viewHolder.getItemId()));
        Log.i("Recipe:viewHolder_toString", viewHolder.toString());
        Log.i("Recipe:viewHolder_itemview_context", String.valueOf(viewHolder.itemView.getContext()));
        int textViewRecipeId;
        String recipeId;
        Optional<Recipe> selectedRecipe;
        switch (recyclerView.getId()){
            case R.id.rvMyRecipes:
                textViewRecipeId = R.id.tvRecipeId2;
                recipeId = ((TextView) viewHolder.itemView.findViewById(textViewRecipeId))
                        .getText().toString();
                selectedRecipe = MyRecipesActivity.recipes.stream()
                        .filter(recipe -> recipe.getId() == recipeId).findFirst();
                break;
            default:
                textViewRecipeId = R.id.tvRecipeId;
                recipeId = ((TextView) viewHolder.itemView.findViewById(textViewRecipeId))
                        .getText().toString();
                selectedRecipe = MainActivity.recipes.stream()
                        .filter(recipe -> recipe.getId() == recipeId).findFirst();
                break;
        }
        Log.i("Recipe ID", recipeId);
        Log.i("Recipe:Optional value", String.valueOf(selectedRecipe.get()));
        if (selectedRecipe.isPresent()) {
            Log.i("Recipe:selected", String.valueOf(selectedRecipe));
            Intent intent = new Intent(context, ShowRecipeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("recipe", selectedRecipe.get());
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Erro: não foi possível encontrar a receita.", Toast.LENGTH_SHORT).show();
        }
    }
}