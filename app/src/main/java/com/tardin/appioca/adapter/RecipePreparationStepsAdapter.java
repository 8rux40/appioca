package com.tardin.appioca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tardin.appioca.R;

import java.util.ArrayList;

public class RecipePreparationStepsAdapter extends RecyclerView.Adapter<RecipePreparationStepsAdapter.MyViewHolder> {
    private final ArrayList<String> preparationSteps;

    public RecipePreparationStepsAdapter(ArrayList<String> preparationStep) {
        this.preparationSteps = preparationStep;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preparation_steps_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView tvRecipePreparationStepNumberExample = holder.tvRecipePreparationStepNumberExample;
        TextView tvRecipePreparationStepExample = holder.tvRecipePreparationStepExample;
        tvRecipePreparationStepNumberExample.setText(String.format("%d", position + 1));
        tvRecipePreparationStepExample.setText(preparationSteps.get(position));
    }

    @Override
    public int getItemCount() {
        return preparationSteps.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipePreparationStepNumberExample;
        TextView tvRecipePreparationStepExample;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvRecipePreparationStepNumberExample = itemView.findViewById(R.id.tvRecipePreparationStepNumberExample);
            this.tvRecipePreparationStepExample = itemView.findViewById(R.id.tvRecipePreparationStepExample);
        }
    }

}
