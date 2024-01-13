package com.codervai.campusdeal.screen.addproduct;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentAddProductBinding;
import com.codervai.campusdeal.model.MyLocation;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.screen.categorybottomsheet.CategoryListBottomSheetFragment;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.MyDialog;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.AddProductViewModel;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.A;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.Subcomponent;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding mVB;

    public static final int MAX_IMAGE = 5;

    private ProductImageRVAdapter adapter;

    private MyLocation selectedLocation;

    private AddProductViewModel addProductViewModel;

    UserViewModel userViewModel;

    private MyDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductViewModel =  new ViewModelProvider(this).get(AddProductViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    // pick image from gallery // https://developer.android.com/training/data-storage/shared/photopicker
    ////                // Registers a photo picker activity launcher in multi-select mode.
    ////                //  the app lets the user select up to 5 media files.
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                if(!uris.isEmpty()){
                    int allowedImageCount = MAX_IMAGE - adapter.getImageUriList().size();

                    if(uris.size()>allowedImageCount){
                        String  msg = "You can add maximum 5 images";
                        if(allowedImageCount == 0){
                            msg = "You can't add more images";
                        }else if (allowedImageCount < MAX_IMAGE) {
                            msg = "You can add "+allowedImageCount+" more images";
                        }
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addProductViewModel.addToImageUriList(uris);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentAddProductBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingDialog = new MyDialog(getActivity(), R.layout.dialog_loading);

        // back button listener
        mVB.backButtonIv.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // set up product image picker recycler view
        setUpProductImageRV();

        // selected category card and add category bottom sheet
        updateSelectedCategory();
        mVB.selectCategoryCard.setOnClickListener(v -> {
            openCategoryBottomSheet();
        });



        // location input
        parseLocationFromBackStackEntry();
        mVB.locationEt.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_addProductFragment_to_googleMapFragment);
        });


        // upload the product
        mVB.adPostBtn.setOnClickListener(v -> {
            // validate the input
            boolean ok = validateInput();
            if(!ok){
                return;
            }

            loadingDialog.showDialog("Uploading...", R.id.loading_msg_tv);

            // upload the images first
            addProductViewModel.uploadImages(getContext()).observe(getViewLifecycleOwner(), listStateData -> {
                if(listStateData.getStatus()== StateData.DataStatus.SUCCESS){
                    List<String> urls = listStateData.getData();

                    userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), userStateData -> {
                        if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
                            String id = UUID.randomUUID().toString();
                            String title = mVB.titleEt.getText().toString();
                            String description = mVB.descriptionEt.getText().toString();
                            String category = mVB.selectedCategoryTv.getText().toString();
                            double price = Double.parseDouble(mVB.priceEt.getText().toString());
                            boolean negotiable = mVB.negotiableSwitch.isChecked();
                            boolean urgent = mVB.urgentSwitch.isChecked();
                            User seller = userStateData.getData();

                            Product product = new Product(
                                    id,title, description, category, price, negotiable, urgent,
                                    seller.getUid(), seller.getName(), new Date(),
                                    urls,
                                    selectedLocation
                            );

                            addProductViewModel.uploadProduct(product).observe(getViewLifecycleOwner(), voidStateData -> {
                                loadingDialog.hideDialog();
                                if(voidStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                    Toast.makeText(getContext(), "Product uploaded!", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(this).popBackStack();
                                }else{
                                    Toast.makeText(getContext(), "Error uploading product", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            loadingDialog.hideDialog();
                            Toast.makeText(getContext(), "User getting seller info", Toast.LENGTH_SHORT).show();
                        }
                    });



                }else{
                    loadingDialog.hideDialog();
                    Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateSelectedCategory() {
        // bind with view model
        addProductViewModel.getSelectedCategoryIndex().observe(getViewLifecycleOwner(), position -> {
            // set category name
            mVB.selectedCategoryTv.setText(Constants.CATEGORY_LIST.get(position));
            // set category icon
            mVB.selectedCategoryIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            getResources(),
                            Constants.CATEGORY_ICON_LIST.get(position),
                            null
                    )
            );
        });
    }

    private void setUpProductImageRV() {
        adapter = new ProductImageRVAdapter();
        adapter.setOnAddImageClickedListener(item -> {
            // pick image from gallery
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    // media type image video
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE) // ignore the error
                    .build());
        });
        mVB.imageRv.setAdapter(adapter);

        // set observer
        addProductViewModel.getImageUriList().observe(getViewLifecycleOwner(), uris -> {
            adapter.setImageUriList(uris);
        });
    }

    private void openCategoryBottomSheet() {
        CategoryListBottomSheetFragment categoryListBottomSheetFragment = new CategoryListBottomSheetFragment();
        categoryListBottomSheetFragment.setOnCategorySelectListener(position -> {
            addProductViewModel.setCategoryIndex(position);
            categoryListBottomSheetFragment.dismiss();
        });
        categoryListBottomSheetFragment.show(getChildFragmentManager(), categoryListBottomSheetFragment.getTag());
    }

    private void parseLocationFromBackStackEntry() {
        NavController navController = NavHostFragment.findNavController(this);
        if(navController.getCurrentBackStackEntry() != null){
            navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("location")
                    .observe(getViewLifecycleOwner(), parcelable -> {
                        selectedLocation =  Parcels.unwrap((Parcelable) parcelable);
                        mVB.locationEt.setText(selectedLocation.getFullAddress());
                    });
        }
    }
    private boolean validateInput() {
        if(adapter.getImageUriList().isEmpty()){
            Toast.makeText(getContext(), "Please select at least one image", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check if the title is empty
        if(mVB.titleEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            // show error in title edit text
            mVB.titleEt.setError("Please enter a title");
            return false;
        }

        // check if the description is empty
        if(mVB.descriptionEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            // show error in description edit text
            mVB.descriptionEt.setError("Please enter a description");
            return false;
        }

        // check if the price is empty
        if(mVB.priceEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a price", Toast.LENGTH_SHORT).show();
            // show error in price edit text
            mVB.priceEt.setError("Please enter a price");
            return false;
        }

        // check if is equal R.string.select_category
        if(mVB.selectedCategoryTv.getText().toString().equals(getString(R.string.select_category))){
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check if location is selected
        if(selectedLocation == null){
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}