package com.codervai.campusdeal.screen.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentCategoryBinding;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.screen.common.ProductListAdapter;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.ProductViewModel;
import com.codervai.campusdeal.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding mVB;

    private String name; // category name passed argument

    ProductViewModel productVM;
    UserViewModel userVM;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           name = getArguments().getString("name");
        }
        productVM = new ViewModelProvider(this).get(ProductViewModel.class);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentCategoryBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userVM.getUserLiveData()
                .observe(getViewLifecycleOwner(),userStateData -> {
                    if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
                        User user = userStateData.getData();
                        if(user == null){
                            Log.d("Category->", "No user");
                            return;
                        }
                        ProductListAdapter adapter = new ProductListAdapter(user, false);

                        mVB.allProductRv.setAdapter(adapter);

                        productVM.getProductsByCategory(user,name, -1)
                                .observe(getViewLifecycleOwner(), productLD ->{
                                    if(productLD.getStatus() == StateData.DataStatus.SUCCESS){
                                        adapter.differ.submitList(productLD.getData());
                                    }
                                    mVB.loadingPb.setVisibility(View.GONE);
                                });

                        // nearest
                        ProductListAdapter nearest = new ProductListAdapter(user, false);
                        mVB.nearestProductRv.setAdapter(nearest);

                        productVM.getNearestProductsByCategory(name)
                                .observe(getViewLifecycleOwner(), nearestProductLD -> {
                                    mVB.loadingPb.setVisibility(View.GONE);
                                    if(nearestProductLD.getStatus() == StateData.DataStatus.SUCCESS){
                                        nearest.differ.submitList(nearestProductLD.getData());
                                    }
                                });
                    }
                } );

    }
}