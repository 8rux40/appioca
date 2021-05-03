package com.tardin.appioca.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tardin.appioca.CreateNewRecipeActivity;
import com.tardin.appioca.MyRecipesActivity;
import com.tardin.appioca.R;
import com.tardin.appioca.entity.Recipe;
import com.tardin.appioca.repository.RecipeRepository;

import java.io.File;
import java.util.ArrayList;

public class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.MyViewHolder> {
    public static ArrayList<Recipe> recipes;
    private final int MAX_NAME_CHARS = 38;

    public MyRecipesAdapter(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list, parent, false);

        view.setOnClickListener(MyRecipesActivity.openRecipe);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        TextView tvRecipeListName = holder.tvRecipeListName;
        TextView tvRecipeListCategory = holder.tvRecipeListCategory;
        TextView tvRecipeListAuthor = holder.tvRecipeListAuthor;
        TextView tvRecipeId2 = holder.tvRecipeId2;
        Button btnRecipeListEdit = holder.btnRecipeListEdit;
        Button btnRecipeListDelete = holder.btnRecipeListDelete;


        ImageView ivRecipeListPhoto = holder.ivRecipeListPhoto;
//        Bitmap bitmap = null;
        Context context = holder.itemView.getContext();
        ContentResolver contentResolver = context.getContentResolver();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(recipe.getPhoto());
        Glide.with(context)
                .load(httpsReference)
                .centerCrop()
                .into(ivRecipeListPhoto);

//        final File file = new File(context.getFilesDir(), "DownloadedImage.png");
//        httpsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                ivRecipeListPhoto.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("MyRecipes:photo", "Erro ao carregar imagem.", exception);
//            }
//        });

        tvRecipeListName.setText(recipe.getShortName(MAX_NAME_CHARS));
//        tvRecipeListCategory.setText(recipe.getCategory());
        tvRecipeListAuthor.setText("por " + recipe.getAuthor());
        tvRecipeId2.setText(recipe.getId());

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    deleteRecipe(recipe);
                } else {
                    Toast.makeText(context, "Exclusão cancelada", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnRecipeListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deseja realmente excluir sua receita?")
                        .setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener)
                        .show();
            }
        });

        btnRecipeListEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRecipe(recipe, context);
            }
        });

    }

    private void editRecipe(Recipe recipe, Context context) {
        try {
            Intent intent = new Intent(context, CreateNewRecipeActivity.class);
            intent.putExtra("update", true);
            Bundle bundle = new Bundle();
            bundle.putSerializable("recipe", recipe);
            intent.putExtras(bundle);
            context.startActivity(intent);
            ((Activity)context).finish();
            Log.i("RecipeList:edit", "Aberta activity de edição!");
        } catch (Exception e) {
            Log.e("RecipeList:edit", "Erro ao tentar abrir activity de edição!", e);
        }
    }

    private void deleteRecipe(Recipe recipe) {
        try {
            RecipeRepository.delete(recipe);
            this.notifyDataSetChanged();
            Log.i("RecipeList:delete", "Receita excluída com sucesso!");
        } catch (Exception e) {
            Log.e("RecipeList:delete", "Erro ao excluir receita!", e);
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeListName;
        TextView tvRecipeListCategory;
        TextView tvRecipeListAuthor;
        TextView tvRecipeId2;
        Button btnRecipeListEdit;
        Button btnRecipeListDelete;
        ImageView ivRecipeListPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvRecipeListName = itemView.findViewById(R.id.tvRecipeListName);
            this.tvRecipeListCategory = itemView.findViewById(R.id.tvRecipeListCategory);
            this.tvRecipeListAuthor = itemView.findViewById(R.id.tvRecipeListAuthor);
            this.btnRecipeListEdit = itemView.findViewById(R.id.btnRecipeListEdit);
            this.btnRecipeListDelete = itemView.findViewById(R.id.btnRecipeListDelete);
            this.ivRecipeListPhoto = itemView.findViewById(R.id.ivRecipeListPhoto);
            this.tvRecipeId2 = itemView.findViewById(R.id.tvRecipeId2);
        }
    }
}
