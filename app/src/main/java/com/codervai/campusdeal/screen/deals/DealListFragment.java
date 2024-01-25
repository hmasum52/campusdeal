package com.codervai.campusdeal.screen.deals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentDealListBinding;
import com.codervai.campusdeal.model.DealRequest;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.screen.common.RecyclerItemClickListener;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.util.Util;
import com.codervai.campusdeal.viewmodel.DealViewModel;
import com.codervai.campusdeal.viewmodel.ProductViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DealListFragment extends Fragment {
    FragmentDealListBinding mVB;

    private DealViewModel dealVM;

    private ProductViewModel productVM;

    DealRequestListAdapter dealRequestListAdapter;

    // index of this fragment in the viewpager
    private int position = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dealVM = new ViewModelProvider(this).get(DealViewModel.class);
        productVM = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentDealListBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        position = Util.getViewPagerFragmentIndex(this, Constants.DEALS_TAB_MENU_LIST.size());

        mVB.dealListRv.setVisibility(View.GONE);

        dealRequestListAdapter = new DealRequestListAdapter(dealRequest -> {
            productVM.getProduct(dealRequest.getProductId())
                    .observe(getViewLifecycleOwner(), productStateData -> {
                        if(productStateData.getStatus() == StateData.DataStatus.SUCCESS){
                            Product product = productStateData.getData();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("product", Parcels.wrap(product));
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_myDealsFragment_to_productDetailsFragment, bundle);
                        }
                    });
        });

        switch (position){
            case 0: // Request for you
                mVB.dealListRv.setAdapter(dealRequestListAdapter);
                updateRequestForMeUI();
                break;
            case 1: // Your request
                dealRequestListAdapter.setBuyer(true);
                mVB.dealListRv.setAdapter(dealRequestListAdapter);
                updateMyRequestUI();
                break;
            case 2: // My purchase
                break;
            case 3: // My sell
                break;
        }
    }

    private void updateNoItemUI(String msg, String tips) {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.dealListRv.setVisibility(View.GONE);
        mVB.noItemPlaceholder.messageTv.setText(msg);
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }

    private void updateRequestForMeUI() {
        String noDataMsg = "No request for you";
        String noDataTips = "You can see all the request you have received here";
        mVB.loadingPb.setVisibility(View.VISIBLE);
        dealVM.getDealRequests("sellerId", FirebaseAuth.getInstance().getUid())
                .observe(getViewLifecycleOwner(), listStateData -> {
                    mVB.loadingPb.setVisibility(View.GONE);
                    if(listStateData.getStatus() == StateData.DataStatus.SUCCESS){
                        List<DealRequest> dealRequests = listStateData.getData();
                        if(dealRequests.size() == 0){
                            updateNoItemUI(noDataMsg, noDataTips);
                        }else{
                            mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                            mVB.dealListRv.setVisibility(View.VISIBLE);
                            dealRequestListAdapter.differ.submitList(dealRequests);
                        }
                    }else{
                        updateNoItemUI(noDataMsg, noDataTips);
                    }
                });
    }

    private void updateMyRequestUI() {
        String noDataMsg = "You haven't made any request yet!";
        String noDataTips = "You can see all the request you have sent here";
        mVB.loadingPb.setVisibility(View.VISIBLE);
        dealVM.getDealRequests("buyerId", FirebaseAuth.getInstance().getUid())
                .observe(getViewLifecycleOwner(), listStateData -> {
                    mVB.loadingPb.setVisibility(View.GONE);
                    if(listStateData.getStatus() == StateData.DataStatus.SUCCESS){
                        List<DealRequest> dealRequests = listStateData.getData();
                        if(dealRequests.size() == 0){
                            updateNoItemUI(noDataMsg, noDataTips);
                        }else{
                            mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                            mVB.dealListRv.setVisibility(View.VISIBLE);
                            dealRequestListAdapter.differ.submitList(dealRequests);
                        }
                    }else{
                        updateNoItemUI(noDataMsg, noDataTips);
                    }
                });
    }
}
