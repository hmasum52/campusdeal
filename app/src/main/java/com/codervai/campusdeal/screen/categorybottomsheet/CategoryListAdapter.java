package com.codervai.campusdeal.screen.categorybottomsheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.CardCategoryListItemBinding;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.ItemClickListener;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder>{

    private ItemClickListener<Integer> onCategorySelectListener;

    public void setOnCategorySelectListener(ItemClickListener<Integer> onCategorySelectListener) {
        this.onCategorySelectListener = onCategorySelectListener;
    }

    @NonNull
    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_category_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.ViewHolder holder, int position) {
        holder.mVB.categoryNameTv.setText(Constants.CATEGORY_LIST.get(position));
        holder.mVB.categoryIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                        holder.itemView.getContext().getResources(),
                        Constants.CATEGORY_ICON_LIST.get(position),
                        null
                )
        );

        holder.itemView.setOnClickListener(v -> {
            if(onCategorySelectListener!=null){
                onCategorySelectListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Constants.CATEGORY_ICON_LIST.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardCategoryListItemBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardCategoryListItemBinding.bind(itemView);
        }
    }
}
