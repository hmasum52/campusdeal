package com.codervai.campusdeal.screen.addproduct;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.CardPostAdImageBinding;
import com.codervai.campusdeal.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ProductImageRVAdapter extends RecyclerView.Adapter<ProductImageRVAdapter.ViewHolder> {
    private final List<Uri> imageUriList;

    public ProductImageRVAdapter() {
        this.imageUriList = new ArrayList<>();
    }

    public List<Uri> getImageUriList() {
        return imageUriList;
    }

    public void addImage(Uri imageUri){
        imageUriList.add(imageUri);
        notifyDataSetChanged(); // redraw
    }

    // update list and ui
    public void setImageUriList(List<Uri> imageUriList){
        this.imageUriList.clear();
        this.imageUriList.addAll(imageUriList);
        notifyDataSetChanged();
    }


    private RecyclerItemClickListener<Void> onAddImageButtonClickListener;

    public void setOnAddImageClickedListener(RecyclerItemClickListener<Void> onProfileMenuItemClickListener) {
        this.onAddImageButtonClickListener = onProfileMenuItemClickListener;
    }

    @NonNull
    @Override
    public ProductImageRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post_ad_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageRVAdapter.ViewHolder holder, int position) {
        if(position<imageUriList.size()){
            holder.mVB.image.setPadding(0, 0, 0, 0);
            holder.mVB.image.setImageURI(imageUriList.get(position));
        }else{
            holder.itemView.setOnClickListener(v -> {
                if(onAddImageButtonClickListener!=null){
                    onAddImageButtonClickListener.onItemClick(null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageUriList.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardPostAdImageBinding mVB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardPostAdImageBinding.bind(itemView);
        }
    }
}
