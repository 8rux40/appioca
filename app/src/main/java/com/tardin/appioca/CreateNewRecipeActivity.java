package com.tardin.appioca;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tardin.appioca.entity.Ingredient;
import com.tardin.appioca.entity.Recipe;
import com.tardin.appioca.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.UUID;

public class CreateNewRecipeActivity extends AppCompatActivity {
    private static final int
            INGREDIENT_NAME_INDEX = 0,
            INGREDIENT_MEASUREMENT_INDEX = 1,
            PREPARATION_STEP_INDEX = 0;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText etIngredientName, etIngredientMeasurement, etPreparationStep,
            etNewRecipeName, etNewRecipeDescription, etNewRecipeYield, etNewRecipePreparationTime;
    private Button btnAddNewIngredient, btnAddNewPreparationStep, btnSaveNewRecipe, btnAddNewRecipePhoto;
    private LinearLayout containerNewIngredients, containerNewPreparationSteps;
    private ImageView ivUserPhoto;

    private Recipe recipe;
    private Uri selectedUri, newRecipePhotoUri;

    private boolean editExistingRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_recipe);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mapActivityComponents();
        mapActivityComponentsMethods();

        loadUserPhoto();
        newRecipePhotoUri = null;
        editExistingRecipe = getIntent().hasExtra("update");

        if (editExistingRecipe)
            fillInputsWithExistingRecipe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            selectedUri = data.getData();
            String filename = UUID.randomUUID().toString();
            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("/images/" + filename);
            storageReference.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newRecipePhotoUri = uri;
                                    Log.i("NewRecipe:photo", uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("NewRecipe:photo", e.getMessage(), e);
                        }
                    });
        }
    }

    private void mapActivityComponents() {
        // Toolbar
        ivUserPhoto = findViewById(R.id.ivUserPhoto);

        // Recipe details
        etNewRecipeName = findViewById(R.id.etNewRecipeName);
        etNewRecipeDescription = findViewById(R.id.etNewRecipeDescription);
        etNewRecipeYield = findViewById(R.id.etNewRecipeYield);
        etNewRecipePreparationTime = findViewById(R.id.etNewRecipePreparationTime);

        // Ingredients
        containerNewIngredients = findViewById(R.id.containerNewIngredients);
        etIngredientName = findViewById(R.id.etNewIngredientName);
        etIngredientMeasurement = findViewById(R.id.etIngredientMeasurement);
        btnAddNewIngredient = findViewById(R.id.btnAddNewIngredient);

        // Preparation Steps
        containerNewPreparationSteps = findViewById(R.id.containerNewPreparationSteps);
        etPreparationStep = findViewById(R.id.etPreparationStep);
        btnAddNewPreparationStep = findViewById(R.id.btnAddNewPreparationStep);

        // Add Recipe Photo Button
        btnAddNewRecipePhoto = findViewById(R.id.btnAddNewRecipePhoto);

        // Save Button
        btnSaveNewRecipe = findViewById(R.id.btnSaveNewRecipe);
    }

    private void mapActivityComponentsMethods() {
        btnAddNewIngredient.setOnClickListener(view -> addNewIngredient());
        btnAddNewPreparationStep.setOnClickListener(view -> addNewPreparationStep());
        btnSaveNewRecipe.setOnClickListener(view -> saveOrUpdateRecipe());
        btnAddNewRecipePhoto.setOnClickListener(view -> addNewRecipePhoto());
        ivUserPhoto.setOnClickListener(view -> startActivity(new Intent(getBaseContext(), UserActivity.class)));
    }

    private void fillInputsWithExistingRecipe(){
        recipe = (Recipe) getIntent().getExtras().getSerializable("recipe");
        etNewRecipeName.setText(recipe.getName());
        etNewRecipeDescription.setText(recipe.getDescription());
        etNewRecipeYield.setText(recipe.getYield().toString());
        etNewRecipePreparationTime.setText(recipe.getPreparationTime().toString());
        newRecipePhotoUri = Uri.parse(recipe.getPhoto());
        recipe.getIngredients().forEach(ingredient -> {
            etIngredientName.setText(ingredient.getName());
            etIngredientMeasurement.setText(ingredient.getMeasurement());
            addNewIngredient();
        });
        recipe.getPreparationSteps().forEach(preparationStep -> {
            etPreparationStep.setText(preparationStep);
            addNewPreparationStep();
        });

        btnAddNewRecipePhoto.setText("Mudar foto");
    }

    private void addNewRecipePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void saveOrUpdateRecipe() {
        if (!editExistingRecipe){
            if (saveNewRecipe()){
                Intent intent = new Intent(this, ShowRecipeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("recipe", recipe);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        } else {
            if (updateExistingRecipe()){
                Intent intent = new Intent(this, ShowRecipeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("recipe", recipe);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        }
    }

    private boolean saveNewRecipe() {
        recipe = new Recipe();
        try {
            recipe.setName(etNewRecipeName.getText().toString());
            recipe.setAuthor(currentUser.getDisplayName());
            recipe.setAuthorId(currentUser.getUid());
            recipe.setDescription(etNewRecipeDescription.getText().toString());
            recipe.setYield(Integer.parseInt(etNewRecipeYield.getText().toString()));
            recipe.setPreparationTime(Integer.parseInt(etNewRecipePreparationTime.getText().toString()));
            recipe.setIngredients(getIngredients());
            recipe.setPreparationSteps(getPreparationSteps());
            recipe.setPhoto(newRecipePhotoUri.toString());
        } catch (Exception e) {
            Log.e("CreateNewRecipe:save", "Campos inválidos.", e);
        }
        if (recipe.isValid()) {
            recipe = RecipeRepository.save(recipe);
            return true;
        } else
            Toast.makeText(this,
                    "Não foi possível salvar a sua receita, preencha todos os campos e envie uma foto!",
                    Toast.LENGTH_LONG).show();
        return false;
    }


    private boolean updateExistingRecipe() {
        try {
            recipe = (Recipe) getIntent().getExtras().getSerializable("recipe");
            Log.i("CreateNewRecipe:update", recipe.toString());
            recipe.setName(etNewRecipeName.getText().toString());
            recipe.setDescription(etNewRecipeDescription.getText().toString());
            recipe.setYield(Integer.parseInt(etNewRecipeYield.getText().toString()));
            recipe.setPreparationTime(Integer.parseInt(etNewRecipePreparationTime.getText().toString()));
            recipe.setIngredients(getIngredients());
            recipe.setPreparationSteps(getPreparationSteps());
            recipe.setPhoto(newRecipePhotoUri.toString());
        } catch (Exception e) {
            Log.e("CreateNewRecipe:update", "Campos inválidos.", e);
        }
        if (recipe.isValid()) {
            RecipeRepository.update(recipe);
            return true;
        } else
            Toast.makeText(this,
                    "Não foi possível salvar a sua receita, preencha todos os campos e envie uma foto!",
                    Toast.LENGTH_LONG).show();
        return false;
    }

    private ArrayList<String> getPreparationSteps() {
        ArrayList<String> preparationSteps = new ArrayList<>();
        for (int i = 1; i < containerNewPreparationSteps.getChildCount(); i++) {
            ConstraintLayout thisChild = (ConstraintLayout) containerNewPreparationSteps.getChildAt(i);
            preparationSteps.add(((TextView) thisChild.getChildAt(PREPARATION_STEP_INDEX)).getText().toString());
        }
        return preparationSteps;
    }

    private ArrayList<Ingredient> getIngredients() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (int i = 1; i < containerNewIngredients.getChildCount(); i++) {
            ConstraintLayout thisChild = (ConstraintLayout) containerNewIngredients.getChildAt(i);
            Ingredient ingredient = new Ingredient();
            ingredient.setName(((TextView) thisChild.getChildAt(INGREDIENT_NAME_INDEX)).getText().toString());
            ingredient.setMeasurement(((TextView) thisChild.getChildAt(INGREDIENT_MEASUREMENT_INDEX)).getText().toString());
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private void addNewPreparationStep() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addNewPreparationStepView = layoutInflater.inflate(R.layout.preparation_step_row, null);
        TextView tvNewPreparationStep = addNewPreparationStepView.findViewById(R.id.tvNewPreparationStep);
        Button btnRemoveNewPreparationStep = addNewPreparationStepView.findViewById(R.id.btnRemoveNewPreparationStep);
        tvNewPreparationStep.setText(etPreparationStep.getText().toString());
        final View.OnClickListener thisListener = view ->
                ((LinearLayout) addNewPreparationStepView.getParent()).removeView(addNewPreparationStepView);
        btnRemoveNewPreparationStep.setOnClickListener(thisListener);
        containerNewPreparationSteps.addView(addNewPreparationStepView);
        clearNewPreparationStepInputs();
    }

    private void addNewIngredient() {
        if (!etIngredientName.getText().toString().isEmpty()) {
            if (!etIngredientMeasurement.getText().toString().isEmpty()) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addNewIngredientView = layoutInflater.inflate(R.layout.ingredient_row, null);
                TextView tvNewIngredientName = addNewIngredientView.findViewById(R.id.tvNewIngredientName);
                TextView tvNewIngredientMeasurement = addNewIngredientView.findViewById(R.id.tvNewIngredientMeasurement);
                Button btnRemoveNewIngredient = addNewIngredientView.findViewById(R.id.btnRemoveNewIngredient);
                tvNewIngredientName.setText(etIngredientName.getText().toString());
                tvNewIngredientMeasurement.setText(etIngredientMeasurement.getText().toString());

                final View.OnClickListener thisListener = view ->
                        ((LinearLayout) addNewIngredientView.getParent()).removeView(addNewIngredientView);
                btnRemoveNewIngredient.setOnClickListener(thisListener);
                containerNewIngredients.addView(addNewIngredientView);
                clearNewIngredientInputs();
            } else {
                Toast.makeText(CreateNewRecipeActivity.this, "Peencha a medida do ingrediente!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(CreateNewRecipeActivity.this, "Peencha o nome do ingrediente!", Toast.LENGTH_LONG).show();
        }
    }

    private void clearNewIngredientInputs() {
        etIngredientMeasurement.setText("");
        etIngredientName.setText("");
    }

    private void clearNewPreparationStepInputs() {
        etPreparationStep.setText("");
    }

    private void loadUserPhoto() {
        Glide.with(this).load(currentUser.getPhotoUrl()).into(ivUserPhoto);
    }
}