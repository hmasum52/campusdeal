package com.codervai.campusdeal.screen.addproduct;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentAddProductBinding;
import com.codervai.campusdeal.model.MyLocation;
import com.codervai.campusdeal.screen.categorybottomsheet.CategoryListBottomSheetFragment;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.viewmodel.AddProductViewModel;

import org.parceler.Parcels;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding mVB;

    public static final int MAX_IMAGE = 5;

    private ProductImageRVAdapter adapter;

    private MyLocation selectedLocation;

    private AddProductViewModel addProductViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductViewModel =  new ViewModelProvider(this).get(AddProductViewModel.class);
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
}