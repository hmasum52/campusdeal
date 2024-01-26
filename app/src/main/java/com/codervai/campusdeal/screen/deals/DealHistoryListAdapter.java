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
import com.codervai.campusdeal.model.Deal;
import com.codervai.campusdeal.model.DealRequest;
import com.codervai.campusdeal.screen.common.RecyclerItemClickListener;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.Util;

public class DealHistoryListAdapter extends RecyclerView.Adapter<DealHistoryListAdapter.ViewHolder>{
    private static final String TAG = "DealRequestListAdapter";

    private boolean isBuyer = false;

    public DealHistoryListAdapter(boolean buyer){
        this.isBuyer = false;
    }

    public void setBuyer(boolean isBuyer) {
        this.isBuyer = isBuyer;
    }

    private final DiffUtil.ItemCallback<Deal> diffCallback = new DiffUtil.ItemCallback<Deal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Deal oldItem, @NonNull Deal newItem) {
            return oldItem.getProduct().getId().equals(newItem.getProduct().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Deal oldItem, @NonNull Deal newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<Deal> differ = new AsyncListDiffer<>(this, diffCallback);

    RecyclerItemClickListener<Deal> onDealHistoryClickListener;

    public DealHistoryListAdapter(RecyclerItemClickListener<Deal> onDealHistoryClickListener) {
        this.onDealHistoryClickListener = onDealHistoryClickListener;
    }

    @NonNull
    @Override
    public DealHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHistoryListAdapter.ViewHolder holder, int position) {
        // get deal request
        Deal deal = differ.getCurrentList().get(position);

        // set data to the view

        // set buyer info
        String dealText = "You sold " + deal.getProduct().getTitle() + " to " + deal.getDealInfo().getBuyerName();
        if(isBuyer) dealText = "You bought " + deal.getProduct().getTitle() + " from " + deal.getDealInfo().getSellerName();
        String priceText = "at BDT " + deal.getProduct().getPrice();
        String dateText = Util.calculateTimeAgo(deal.getDate());

        holder.mVB.buyerInfoTv.setText(dealText);
        holder.mVB.productTitleTv.setText(priceText);
        holder.mVB.timeTv.setText(dateText);
        holder.mVB.reviewBtn.setText("See Details");

        // review request
        holder.mVB.reviewBtn.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: review btn clicked");
            if(onDealHistoryClickListener != null) onDealHistoryClickListener.onItemClick(deal);
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

