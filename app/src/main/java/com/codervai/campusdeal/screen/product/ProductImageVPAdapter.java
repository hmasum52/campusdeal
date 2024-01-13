package com.codervai.campusdeal.screen.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.ProductImageViewBinding;

import java.util.List;

public class ProductImageVPAdapter extends RecyclerView.Adapter<ProductImageVPAdapter.ViewHolder>{
    public static final String TAG = "ProductImageVPAdapter";
    private final DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<String> differ = new AsyncListDiffer<>(this, diffCallback);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_image_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = differ.getCurrentList().get(position);
        Glide.with(holder.mVB.getRoot())
                .load(imageUrl)
                .into(holder.mVB.productIV);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ProductImageViewBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = ProductImageViewBinding.bind(itemView);
        }
    }

}
