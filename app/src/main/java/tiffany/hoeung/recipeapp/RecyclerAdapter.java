package tiffany.hoeung.recipeapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private ArrayList<Recipe> recipes;
    private OnNoteListener onNoteListener;
    private ListRecipeFragment frag;

    public RecyclerAdapter(ArrayList<Recipe> recipeList, OnNoteListener onNoteListener, ListRecipeFragment frag) {
        this.frag = frag;
        recipes = recipeList;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_layout, parent, false);
        return new ViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.recipeName.setText(recipes.get(position).getRecipeName());
        Bitmap imageBitmap = recipes.get(position).getImage();
        holder.recipeImage.setImageBitmap(imageBitmap);
    }

    public void updateList(ArrayList<Recipe> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView recipeName;
        private final ImageView recipeImage;
        OnNoteListener onNoteListener;

        public ViewHolder(View view, OnNoteListener onNoteListener) {
            super(view);
            recipeName = view.findViewById(R.id.recipeName);
            recipeImage = view.findViewById(R.id.recipeImage);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
