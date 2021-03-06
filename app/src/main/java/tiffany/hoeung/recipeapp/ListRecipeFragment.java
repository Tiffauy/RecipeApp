package tiffany.hoeung.recipeapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ListRecipeFragment extends Fragment implements RecyclerAdapter.OnNoteListener {
    private NavController navController;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private ArrayList<Recipe> favoritesList = new ArrayList<>();
    private RecyclerAdapter adapterRV;
    private RecyclerAdapter favoritesAdapterRV;
    private RecyclerView recipeRV;
    private int screen = 0;
    private EditText textFilter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // adds recipes to recipeList arraylist
        if (getArguments() == null && Recipe.recipeArrayList.size() == 0) {
            createInitialRecipeList();
        } else {
            recipeList = Recipe.recipeArrayList;
            favoritesList = Recipe.favoritesArrayList;
            screen = getArguments().getInt("screen");
        }
        sortLists();
        System.out.println("Array Size: " + Recipe.recipeArrayList.size());
        setRecipeAdapter(screen);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_recipe, container, false);

        recipeRV = view.findViewById(R.id.recipeRV);

        NavHostFragment navHostFragment = (NavHostFragment) this.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment != null)
            navController = navHostFragment.getNavController();
        // Favorites button will only display entries in the favoritesList
        view.findViewById(R.id.button_favorites).setOnClickListener(view1 -> {
            recipeRV.setAdapter(favoritesAdapterRV);
            screen = 1;
        });

        view.findViewById(R.id.button_home).setOnClickListener(view1 -> {
            recipeRV.setAdapter(adapterRV);
            screen = 0;
        });

        view.findViewById(R.id.button_new_recipe).setOnClickListener(view1 -> {
            navController.navigate(R.id.list_to_new);
        });

        view.findViewById(R.id.button_random).setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            int position = new Random().nextInt(Recipe.recipeArrayList.size());
            bundle.putInt("position", position);
            bundle.putInt("screen", screen);
            navController.navigate(R.id.list_to_info, bundle);
        });

        textFilter = view.findViewById(R.id.searchbar_text);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterData(editable.toString());
            }
        });

        return view;
    }

    public void setRecipeAdapter(int screen) {
        //get layout manager (aka, what format RV should take)
        layoutManager = new GridLayoutManager(this.getActivity(), 2);
        recipeRV.setLayoutManager(layoutManager);
        //create a new recycler view adapter
        adapterRV = new RecyclerAdapter(recipeList, this, this);
        favoritesAdapterRV = new RecyclerAdapter(Recipe.favoritesArrayList, this, this);
        // set the recycler view's adapter
        if(screen == 0)
            recipeRV.setAdapter(adapterRV);
        else
            recipeRV.setAdapter(favoritesAdapterRV);
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this.getActivity());
        sqLiteManager.populateRecipeListArray();
    }

    @Override
    public void onNoteClick(int position) {
        //Toast.makeText(this.getActivity(), "Position " + position + " pressed\n" + recipeList.get(position).recipeName, Toast.LENGTH_SHORT).show();
        // this is where we switch to the recipe screen
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("screen", screen);
        bundle.putSerializable("recipes", recipeList);
        bundle.putSerializable("favorites", favoritesList);

        navController.navigate(R.id.list_to_info, bundle);
    }

    public void createInitialRecipeList() {
        System.out.println("Creating initial recipes!");
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this.getActivity());
        // Before we start populating the array list, get from the database
        loadFromDBToMemory();
        // Check if recipe array is empty; if so, initialize default values
        if(Recipe.recipeArrayList.size() == 0)
            Recipe.recipeArrayList.addAll(new RecipeArchive(this).createInitialRecipes());
        // Check to see if the values exist in the database
        for(Recipe recipe : Recipe.recipeArrayList)
            if(!sqLiteManager.doesRecipeExistInDB(recipe)) {
                System.out.println("Adding " + recipe.getRecipeName());
                sqLiteManager.addRecipeToDatabase(recipe);
            }
        sortLists();
        System.out.println("Array Size: " + Recipe.recipeArrayList.size());
        recipeList.addAll(Recipe.recipeArrayList);
    }

    public void sortLists() {
        Collections.sort(Recipe.recipeArrayList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {
                String r1Name = r1.recipeName;
                String r2Name = r2.recipeName;

                return r1Name.compareTo(r2Name);
            }
        });
        Collections.sort(Recipe.favoritesArrayList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {
                String r1Name = r1.recipeName;
                String r2Name = r2.recipeName;

                return r1Name.compareTo(r2Name);
            }
        });
    }

    public void filterData(String filter) {
        ArrayList<Recipe> temp = new ArrayList<>();
        for(Recipe recipe: recipeList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(recipe.recipeName.toLowerCase().contains(filter.toLowerCase())){
                temp.add(recipe);
            }
        }
        //update recyclerview
        adapterRV.updateList(temp);
    }
}