package com.codervai.campusdeal.screen.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.CardProfileMenuBinding;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.ProfileMenuItem;
import com.codervai.campusdeal.util.ItemClickListener;

public class ProfileMenuItemAdapter extends RecyclerView.Adapter<ProfileMenuItemAdapter.ViewHolder> {

    private ItemClickListener<ProfileMenuItem> onProfileMenuItemClickListener;

    public void setOnProfileMenuItemClickListener(ItemClickListener<ProfileMenuItem> onProfileMenuItemClickListener) {
        this.onProfileMenuItemClickListener = onProfileMenuItemClickListener;
    }

    @NonNull
    @Override
    public ProfileMenuItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_profile_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileMenuItemAdapter.ViewHolder holder, int position) {
        ProfileMenuItem item = Constants.PROFILE_MENU_OPTIONS.get(position);

        holder.mVB.icon.setImageDrawable(
                ResourcesCompat.getDrawable(holder.itemView.getResources(), item.getIcon(),null)
        );

        holder.mVB.optionName.setText(item.getTitle());
        holder.mVB.optionDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if(onProfileMenuItemClickListener!=null){
                onProfileMenuItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Constants.PROFILE_MENU_OPTIONS.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private CardProfileMenuBinding mVB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardProfileMenuBinding.bind(itemView);
        }
    }
}
