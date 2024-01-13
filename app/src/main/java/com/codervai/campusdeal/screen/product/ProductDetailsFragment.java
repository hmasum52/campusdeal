package com.codervai.campusdeal.screen.product;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.codervai.campusdeal.databinding.FragmentProductDetailsBinding;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.util.Util;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductDetailsFragment extends Fragment {
    FragmentProductDetailsBinding mVB;

    @Inject
    FirebaseFirestore db;

    Product product;

    UserViewModel userVM;

    boolean owner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            product = Parcels.unwrap(getArguments().getParcelable("product"));
        }
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentProductDetailsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // back btn
        mVB.backBtnCard.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // product image
        showProductImages();

        // upload date in dd MMM yyyy format
        mVB.uploadDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(product.getUploadDate()));

        // distance
        User user = userVM.getUser();
        LatLng campus = new LatLng(user.getCampus().getLatitude(), user.getCampus().getLongitude());
        double distance = Util.calculateDistance(campus, product.getProductLocation().getLatLng());
        mVB.distance.setText(String.format(Locale.getDefault(),"%.2f km", distance));

        // check owner
        owner = user.getUid().equals(product.getSellerId());

        // title
        mVB.title.setText(product.getTitle());

        // description
        mVB.description.setText(product.getDescription());

        // price
        mVB.price.setText("$"+product.getPrice());

        // seller info
        showSellerInfo();

        enableContactButton();

        enableFavoriteButton(user);
    }

    private void showProductImages() {
        ProductImageVPAdapter adapter = new ProductImageVPAdapter();
        mVB.imageVp.setAdapter(adapter);
        adapter.differ.submitList(product.getImageUriList());

        // add dot indicator to view pager
        // https://github.com/AdrianKuta/ViewPagerDotsIndicator
        new TabLayoutMediator(mVB.dotIndicator, mVB.imageVp, (tab, position) -> {
        }).attach();
    }

    private void showSellerInfo() {
        userVM.fetchUserProfile(product.getSellerId())
                .observe(getViewLifecycleOwner(), userStateData -> {
                    if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
                        // get user object from documentSnapshot
                        User user = userStateData.getData();
                        if(user == null){
                            return;
                        }
                        // set owner name
                        mVB.ownerName.setText(user.getName());
                        // set owner email
                        mVB.ownerEmail.setText(user.getEmail());
                        // set owner profile image
                        if(user.getProfileImageUrl()!=null)
                            Glide.with(this).load(user.getProfileImageUrl()).into(mVB.ownerAvatar);
                        else mVB.avatarText.setText(user.getName().substring(0,1));
                    }
                });
    }

    private void enableContactButton() {
        if(owner) {
            mVB.dealActionBtn.setVisibility(View.GONE);
            return;
        }

        mVB.dealActionBtn.setOnClickListener(v -> {
            String subject = "Want to buy "+product.getTitle();
            String to = mVB.ownerEmail.getText().toString();
            String body =  "Hi, I am interested in your product "
                    +product.getTitle()+
                    " on CampusDeal App.";

            String[] addresses = {to};

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
    }

    private void enableFavoriteButton(User user) {
        if(owner){
            mVB.favBtnCard.setVisibility(View.GONE);
            return;
        }

        // users/{userId}/wishlist/{productId}
       DocumentReference dRef = db.collection(Constants.USER_COLLECTION)
                .document(user.getUid())
                        .collection(Constants.WISHLIST_COLLECTION)
                                .document(product.getId());

        // init selected sate
        dRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    mVB.favouriteBtn.setSelected(documentSnapshot.exists());
                })
                        .addOnFailureListener(e -> {
                            mVB.favouriteBtn.setSelected(false);
                        });

        // init button
        mVB.favBtnCard.setOnClickListener(v -> {
            if(mVB.favouriteBtn.isSelected()){
                mVB.favouriteBtn.setSelected(false);
                removeFromWishList(dRef);
            }else{
                mVB.favouriteBtn.setSelected(true);
                addToWishList(dRef);
            }
        });
    }

    private void addToWishList(DocumentReference dRef) {
        // wishlist object
        Map<String, Object> wishlistData = new HashMap<>();
        wishlistData.put("productId", product.getId());
        wishlistData.put("title", product.getTitle());
        wishlistData.put("date", new Date());

        dRef.set(wishlistData)
                .addOnSuccessListener(aVoid ->{
                    Snackbar.make(mVB.getRoot(), "Added to wishlist", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(aVoid ->{
                    Snackbar.make(mVB.getRoot(), "Failed to add to wishlist", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void removeFromWishList(DocumentReference dRef) {
        dRef.delete()
                .addOnSuccessListener(unused -> {
                    Snackbar.make(mVB.getRoot(), "Removed from wishlist", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(mVB.getRoot(), "Failed to remove from wishlist. Something went wrong!", Snackbar.LENGTH_SHORT).show();
                });
    }

}
