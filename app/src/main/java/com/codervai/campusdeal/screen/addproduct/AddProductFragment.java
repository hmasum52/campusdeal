package com.codervai.campusdeal.screen.addproduct;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.databinding.FragmentAddProductBinding;
import com.codervai.campusdeal.screen.categorybottomsheet.CategoryListBottomSheetFragment;
import com.codervai.campusdeal.util.Constants;


public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding mVB;

    public static final int MAX_IMAGE = 5;

    private ProductImageRVAdapter adapter;

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

                    for (Uri u : uris) {
                        adapter.addImage(u);
                    }
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

        // set up product image picker recycler view
        setUpProductImageRV();

        // add category bottom sheet
        mVB.selectCategoryCard.setOnClickListener(v -> {
            addCategoryBottomSheet();
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
    }

    private void addCategoryBottomSheet() {
        CategoryListBottomSheetFragment categoryListBottomSheetFragment = new CategoryListBottomSheetFragment();
        categoryListBottomSheetFragment.setOnCategorySelectListener(position -> {
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

            categoryListBottomSheetFragment.dismiss();
        });
        categoryListBottomSheetFragment.show(getChildFragmentManager(), categoryListBottomSheetFragment.getTag());
    }
}