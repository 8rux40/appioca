package com.tardin.appioca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tardin.appioca.R;
import com.tardin.appioca.entity.Ingredient;

import java.util.ArrayList;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.MyViewHolder> {
    private final ArrayList<Ingredient> ingredients;

    public RecipeIngredientsAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView tvRecipeIngredientNameExample = holder.tvRecipeIngredientNameExample;
        TextView tvRecipeIngredientMeasurementExample = holder.tvRecipeIngredientMeasurementExample;
        tvRecipeIngredientNameExample.setText(ingredients.get(position).getName());
        tvRecipeIngredientMeasurementExample.setText(ingredients.get(position).getMeasurement());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeIngredientNameExample;
        TextView tvRecipeIngredientMeasurementExample;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvRecipeIngredientNameExample = itemView.findViewById(R.id.tvRecipeIngredientNameExample);
            this.tvRecipeIngredientMeasurementExample = itemView.findViewById(R.id.tvRecipeIngredientMeasurementExample);
        }
    }

}
