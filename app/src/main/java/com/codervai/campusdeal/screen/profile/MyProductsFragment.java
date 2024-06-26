package com.codervai.campusdeal.screen.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentMyProductsBinding;
import com.codervai.campusdeal.model.Product;
//import com.codervai.campusdeal.screen.common.ProductListAdapter;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.screen.common.ProductListAdapter;
import com.codervai.campusdeal.screen.common.RecyclerItemClickListener;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.AddProductViewModel;
import com.codervai.campusdeal.viewmodel.ProductViewModel;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyProductsFragment extends Fragment{

    FragmentMyProductsBinding mVB;

    // view model
    ProductViewModel productVM;

    @Inject
    FirebaseAuth fAuth;

    UserViewModel userVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productVM =  new ViewModelProvider(this).get(ProductViewModel.class);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentMyProductsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
        // back button
        mVB.backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });


        User user = userVM.getUser();
        if(user==null){
            updateNoItemUI();
            return;
        }

        ProductListAdapter adapter = new ProductListAdapter(user, true);
        mVB.adListRv.setAdapter(adapter);

        adapter.setRecyclerItemClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", Parcels.wrap(product));
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_myProductsFragment_to_productDetailsFragment, bundle);
        });

        // fetch data

        // show loading
        mVB.loadingCpb.setVisibility(View.VISIBLE);

        productVM.getProducts(fAuth.getCurrentUser().getUid())
                .observe(getViewLifecycleOwner(), products -> {
                    if(products.getStatus() == StateData.DataStatus.SUCCESS){
                        List<Product> productList = products.getData();
                        if(productList.isEmpty()){
                            updateNoItemUI();
                        }else{
                            mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                            mVB.adListRv.setVisibility(View.VISIBLE);
                            adapter.differ.submitList(productList);
                        }
                    }else{
                        updateNoItemUI();
                    }
                });

    }

    private void updateNoItemUI() {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.adListRv.setVisibility(View.GONE);

        String msg = "You have no products";
        mVB.noItemPlaceholder.messageTv.setText(msg);
        String tips = "Get start by uploading your first product.";
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }
}
