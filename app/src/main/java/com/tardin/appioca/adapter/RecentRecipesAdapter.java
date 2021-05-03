package com.tardin.appioca.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tardin.appioca.MainActivity;
import com.tardin.appioca.R;
import com.tardin.appioca.entity.Recipe;
import com.tardin.appioca.util.MyAppGlideModule;

import java.io.File;
import java.util.ArrayList;

public class RecentRecipesAdapter extends RecyclerView.Adapter<RecentRecipesAdapter.MyViewHolder> {
    private final ArrayList<Recipe> recipes;
    private final int MAX_NAME_CHARS = 19;

    public RecentRecipesAdapter(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_recipes, parent, false);
        view.setOnClickListener(MainActivity.openRecentRecipe);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        TextView tvRecipeName = holder.tvRecipeName;
        TextView tvRecipePreparationTime = holder.tvRecipePreparationTime;
        TextView tvRecipeYield = holder.tvRecipeYield;
        TextView tvRecipeId = holder.tvRecipeId;
        ImageView ivRecipePhoto = holder.ivRecipePhoto;
        Bitmap bitmap = null;
        Context context = holder.itemView.getContext();
        ContentResolver contentResolver = context.getContentResolver();

//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference httpsReference = storage.getReferenceFromUrl(recipe.getPhoto());

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(recipe.getPhoto());

        Glide.with(context)
            .load(storageReference)
            .into(ivRecipePhoto);

//        final File file = new File(context.getFilesDir(), "DownloadedImage.png");
//        httpsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                ivRecipePhoto.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("RecentRecipes:photo", "Erro ao carregar imagem.", exception);
//            }
//        });
        tvRecipeName.setText(recipe.getShortName(MAX_NAME_CHARS));
        tvRecipePreparationTime.setText(recipe.getPreparationTimeInString());
        tvRecipeYield.setText(recipe.getYieldInString());
        tvRecipeId.setText(recipe.getId());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        TextView tvRecipePreparationTime;
        TextView tvRecipeYield;
        TextView tvRecipeId;
        ImageView ivRecipePhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            this.tvRecipePreparationTime = itemView.findViewById(R.id.tvRecipePreparationTime);
            this.tvRecipeYield = itemView.findViewById(R.id.tvRecipeYield);
            this.tvRecipeId = itemView.findViewById(R.id.tvRecipeId);
            this.ivRecipePhoto = itemView.findViewById(R.id.ivRecipePhoto);
        }
    }
}
