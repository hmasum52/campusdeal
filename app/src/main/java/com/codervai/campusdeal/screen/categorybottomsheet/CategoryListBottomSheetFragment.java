package com.codervai.campusdeal.screen.categorybottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codervai.campusdeal.databinding.BottomDialogCategoryListBinding;
import com.codervai.campusdeal.util.ItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CategoryListBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomDialogCategoryListBinding mVB;

    private ItemClickListener<Integer> onCategorySelectListener;

    public void setOnCategorySelectListener(ItemClickListener<Integer> onCategorySelectListener) {
        this.onCategorySelectListener = onCategorySelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = BottomDialogCategoryListBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CategoryListAdapter adapter = new CategoryListAdapter();
        if(onCategorySelectListener!=null){
            adapter.setOnCategorySelectListener(item -> {
                onCategorySelectListener.onItemClick(item);
            });
        }
        mVB.categoryListRv.setAdapter(adapter);
    }
}
