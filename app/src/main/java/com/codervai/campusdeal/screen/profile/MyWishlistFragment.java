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
import com.codervai.campusdeal.databinding.FragmentMyWishlistBinding;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.screen.common.ProductListAdapter;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.ProductViewModel;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyWishlistFragment extends Fragment{

    FragmentMyWishlistBinding mVB;

    @Inject
    FirebaseFirestore db;

    UserViewModel userVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentMyWishlistBinding.inflate(inflater, container, false);
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
            Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
            Log.d("Wishlist->", "User not found!");
            updateNoItemUI();
            return;
        }
        Log.d("Wishlist->", "User found!");

        ProductListAdapter adapter = new ProductListAdapter(user, true);
        mVB.wishlistRv.setAdapter(adapter);

        adapter.setRecyclerItemClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", Parcels.wrap(product));
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_myWishlistFragment_to_productDetailsFragment, bundle);
        });

        // fetch data

        // show loading
        mVB.loadingCpb.setVisibility(View.VISIBLE);

        // fetch all document id from users/<uid>/wishlist collection
        db.collection(Constants.USER_COLLECTION)
                .document(user.getUid())
                .collection(Constants.WISHLIST_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.isEmpty()){
                        Log.d("wishlist->", "nothing in wishlist");
                        updateNoItemUI();
                    }else{
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.wishlistRv.setVisibility(View.VISIBLE);

                        // make product id list
                        List<String> productIdList = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                            productIdList.add(snapshot.getId());
                        }
                        Log.d("Wishlist->", "Total product id "+productIdList.size());

                        // fetch all products
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .whereIn("id", productIdList)
                                .get()
                                .addOnSuccessListener(productDocumentSnapshots -> {
                                    mVB.loadingCpb.setVisibility(View.GONE);
                                    Log.d("Wishlist->", "total product "+productDocumentSnapshots.size());
                                    if(productDocumentSnapshots.isEmpty()){
                                        updateNoItemUI();
                                        return;
                                    }
                                    List<Product> productList = productDocumentSnapshots.toObjects(Product.class);
                                    adapter.differ.submitList(productList);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("Wishlist->", e.getMessage());
                                    Snackbar.make(mVB.getRoot(), "Something went wrong", Snackbar.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Log.d("Wishlist->", e.getMessage());
                    updateNoItemUI();
                });

    }

    private void updateNoItemUI() {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.wishlistRv.setVisibility(View.GONE);

        String msg = "You have no products in your wishlist";
        mVB.noItemPlaceholder.messageTv.setText(msg);
        String tips = "Add product to wishlist first!";
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }
}
