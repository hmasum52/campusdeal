package com.codervai.campusdeal.screen.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.CardProductHorizontalBinding;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.util.Util;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{

    // Diffutil will be used to update the recyclerview
    // https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
    private final DiffUtil.ItemCallback<Product> diffCallback = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };

    // a list of Products
    public AsyncListDiffer<Product> differ = new AsyncListDiffer<>(this, diffCallback);

    private RecyclerItemClickListener<Product> recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener<Product> recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int position) {
        // get Product
        Product product = differ.getCurrentList().get(position);

        // set image with glide
        // https://futurestud.io/tutorials/glide-image-resizing-scaling
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUriList().get(0))
                .into(holder.mVB.thumbImage);

        // set location
        holder.mVB.locationTv.setText(product.getProductLocation().getAdminArea());

        // set title
        holder.mVB.titleTv.setText(product.getTitle());

        // set price
        String priceDesc = "Tk"+product.getPrice();
        if(product.isUrgent()){
            priceDesc += " • Urgent";
        }
        if(product.isNegotiable()) {
            priceDesc += " • Negotiable";
        }
        holder.mVB.priceDesc.setText(priceDesc);

        // set owner name
        holder.mVB.ownerNameTv.setText(product.getSellerName());

        // set distance

        // set upload date
        String timeAlgo = Util.calculateTimeAgo(product.getUploadDate());
        holder.mVB.timeTv.setText(timeAlgo);

        // set click listener
        holder.itemView.setOnClickListener(v -> {
            if(recyclerItemClickListener!=null){
                recyclerItemClickListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardProductHorizontalBinding mVB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardProductHorizontalBinding.bind(itemView);
        }
    }
}
