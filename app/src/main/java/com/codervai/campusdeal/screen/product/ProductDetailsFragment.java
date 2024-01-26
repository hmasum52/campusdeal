package com.codervai.campusdeal.screen.product;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentProductDetailsBinding;
import com.codervai.campusdeal.model.DealRequest;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.MyDialog;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.util.Util;
import com.codervai.campusdeal.viewmodel.DealViewModel;
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
    public static final String MAKE_REQUEST = "Make Request";
    public static final String CANCEL_REQUEST = "Cancel Request";
    public static final String ACCEPT = "Accept";
    private static final String MAKE_DEAL = "Make Deal";

    FragmentProductDetailsBinding mVB;

    @Inject
    FirebaseFirestore db;

    Product product;

    UserViewModel userVM;

    DealViewModel dealVM;

    boolean owner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            product = Parcels.unwrap(getArguments().getParcelable("product"));
        }
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        dealVM = new ViewModelProvider(this).get(DealViewModel.class);
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

        enableDealActionButton();
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
            mVB.contact.setVisibility(View.GONE);
            return;
        }

        mVB.contact.setOnClickListener(v -> {
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



    private void enableDealActionButton() {
        // check if owner
        if(owner){
            // have to accept the request or decline
            mVB.dealActionBtn.setVisibility(View.GONE);
        }

        dealVM.getDealRequest(product.getId())
                    .observe(getViewLifecycleOwner(), dealRequestStateData -> {
                        if(dealRequestStateData.getStatus() == StateData.DataStatus.SUCCESS){
                            DealRequest dealRequest = dealRequestStateData.getData();
                            if (dealRequest != null ) {

                                // check if owner
                                if(owner){
                                    // have to accept the request or decline
                                    mVB.dealActionBtn.setVisibility(View.VISIBLE);
                                    mVB.dealActionBtn.setText(MAKE_DEAL);
                                    return;
                                }


                                // check if user is buyer
                                if(dealRequest.getBuyerId().equals(userVM.getUser().getUid())){
                                    mVB.dealActionBtn.setText(CANCEL_REQUEST);
                                    return;
                                }
                            }
                            // change button text
                            mVB.dealActionBtn.setEnabled(false);
                            mVB.dealActionBtn.setText("Reserved");
                        }else{
                            mVB.dealActionBtn.setText(MAKE_REQUEST);
                        }
                    });

        mVB.dealActionBtn.setOnClickListener(v -> {
            String buttonText = mVB.dealActionBtn.getText().toString().trim();

            if(buttonText.equals(MAKE_REQUEST)){
                // make deal request
                dealVM.addDealRequest(userVM.getUser(), product)
                        .observe(getViewLifecycleOwner(), booleanStateData -> {
                            if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                Snackbar.make(mVB.getRoot(), "Request sent", Snackbar.LENGTH_SHORT).show();
                                mVB.dealActionBtn.setText(CANCEL_REQUEST);
                            }
                        });
            }else if(buttonText.equals(CANCEL_REQUEST)){
                // cancel the request
                dealVM.cancelDealRequest(product.getId())
                        .observe(getViewLifecycleOwner(), booleanStateData -> {
                            if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                Snackbar.make(mVB.getRoot(), "Request cancelled", Snackbar.LENGTH_SHORT).show();
                                mVB.dealActionBtn.setText(MAKE_REQUEST);
                            }
                        });
            }
            else if (buttonText.equals(MAKE_DEAL)){
                // accept or decline request
                // open a dialog
                MyDialog dialog = new MyDialog(requireContext(), R.layout.dialog_make_deal);
                dialog.showDialog();

                Button makeDealBtn = dialog.findViewById(R.id.make_deal_btn);
                Button declineBtn = dialog.findViewById(R.id.decline_btn);
                Button cancelBtn = dialog.findViewById(R.id.cancel_btn);

                // make deal
                makeDealBtn.setOnClickListener(v1 -> {
                    dialog.hideDialog();

                    MyDialog loading = new MyDialog(requireContext(), R.layout.dialog_loading);
                    loading.showDialog("Loading...", R.id.loading_msg_tv);
                    // make deal
                    dealVM.makeDeal(product)
                            .observe(getViewLifecycleOwner(), booleanStateData -> {
                                loading.hideDialog();
                                if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                    Snackbar.make(mVB.getRoot(), "Deal made", Snackbar.LENGTH_SHORT).show();
                                    mVB.dealActionBtn.setVisibility(View.GONE);
                                }else{
                                    Snackbar.make(mVB.getRoot(), "Failed to make deal", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                });

                //decline
                declineBtn.setOnClickListener(v1 -> {
                    dialog.hideDialog();
                    // decline the request
                    dealVM.cancelDealRequest(product.getId())
                            .observe(getViewLifecycleOwner(), booleanStateData -> {
                                if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                    Snackbar.make(mVB.getRoot(), "Request declined", Snackbar.LENGTH_SHORT).show();
                                    mVB.dealActionBtn.setVisibility(View.GONE);
                                }else{
                                    Snackbar.make(mVB.getRoot(), "Failed to decline request", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                });

                // cancel
                cancelBtn.setOnClickListener(v1 -> {
                    dialog.hideDialog();
                });
            }
            else{
                mVB.dealActionBtn.setEnabled(false);
            }

        });

    }

}
