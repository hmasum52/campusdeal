package com.codervai.campusdeal.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentSearchBinding;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.screen.common.ProductListAdapter;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.ProductViewModel;
import com.codervai.campusdeal.viewmodel.UserViewModel;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;


public class SearchFragment extends Fragment {

    FragmentSearchBinding mVB;

    UserViewModel userVM;

    ProductViewModel productVM;

    ProductListAdapter searchResultAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        productVM = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentSearchBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //adapter setup
        searchResultAdapter = new ProductListAdapter(userVM.getUser(), true);
        mVB.searchResultRv.setAdapter(searchResultAdapter);
        searchResultAdapter.setRecyclerItemClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", Parcels.wrap(product) );
            NavHostFragment.findNavController(SearchFragment.this).navigate(R.id.action_searchFragment_to_productDetailsFragment, bundle);
        });

        updateNoItemUI("Search something!");

        mVB.searchTextEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mVB.loadingSearchPb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if(query.isEmpty()){
                    updateNoItemUI("Search something!");
                    return;
                }

                productVM.searchProduct(query).observe(getViewLifecycleOwner(), productsLD -> {
                    mVB.loadingSearchPb.setVisibility(View.GONE);
                    if(productsLD.getStatus() == StateData.DataStatus.SUCCESS){
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.searchResultRv.setVisibility(View.VISIBLE);
                        List<Product> products = productsLD.getData();

                        if(products == null) {
                            updateNoItemUI("No item found!");
                            return;
                        }

                        if(products.isEmpty()){
                            updateNoItemUI("No item found!");
                            return;
                        }

                        searchResultAdapter.differ.submitList(productsLD.getData());
                        return;
                    }
                    updateNoItemUI("Something went wrong!");
                });
            }
        });
    }

    private void updateNoItemUI(String message){
        mVB.loadingSearchPb.setVisibility(View.GONE);
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.noItemPlaceholder.messageTv.setText(message);
        mVB.searchResultRv.setVisibility(View.GONE);
        searchResultAdapter.differ.submitList(Collections.emptyList());
    }
}