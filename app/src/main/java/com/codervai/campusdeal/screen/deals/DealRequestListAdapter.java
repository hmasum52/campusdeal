package com.codervai.campusdeal.screen.deals;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.CardDealBinding;
import com.codervai.campusdeal.model.DealRequest;
import com.codervai.campusdeal.screen.common.RecyclerItemClickListener;
import com.codervai.campusdeal.util.Util;

public class DealRequestListAdapter extends RecyclerView.Adapter<DealRequestListAdapter.ViewHolder>{
    private static final String TAG = "DealRequestListAdapter";

    private boolean isBuyer = false;

    public DealRequestListAdapter(boolean buyer){
        this.isBuyer = false;
    }

    public void setBuyer(boolean isBuyer) {
        this.isBuyer = isBuyer;
    }

    private final DiffUtil.ItemCallback<DealRequest> diffCallback = new DiffUtil.ItemCallback<DealRequest>() {
        @Override
        public boolean areItemsTheSame(@NonNull DealRequest oldItem, @NonNull DealRequest newItem) {
            return oldItem.getProductId().equals(newItem.getProductId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DealRequest oldItem, @NonNull DealRequest newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<DealRequest> differ = new AsyncListDiffer<>(this, diffCallback);

    RecyclerItemClickListener<DealRequest> onDealRequestClickListener;

    public DealRequestListAdapter(RecyclerItemClickListener<DealRequest> onDealRequestClickListener) {
        this.onDealRequestClickListener = onDealRequestClickListener;
    }

    @NonNull
    @Override
    public DealRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealRequestListAdapter.ViewHolder holder, int position) {
        // get deal request
        DealRequest dealRequest = differ.getCurrentList().get(position);

        // set data to the view

        // set buyer info
        String buyerInfo = dealRequest.getBuyerName()+" has requested to buy ";
        if(isBuyer) buyerInfo = "You have requested "+dealRequest.getSellerName()+" to buy ";
        holder.mVB.buyerInfoTv.setText(buyerInfo);

        // set ad title
        holder.mVB.productTitleTv.setText(dealRequest.getTitle());

        // set time
        String time = Util.calculateTimeAgo(dealRequest.getDate());
        holder.mVB.timeTv.setText(time);

        // review request
        holder.mVB.reviewBtn.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: review btn clicked");
            if(onDealRequestClickListener != null) onDealRequestClickListener.onItemClick(dealRequest);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardDealBinding mVB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardDealBinding.bind(itemView);
        }
    }
}

