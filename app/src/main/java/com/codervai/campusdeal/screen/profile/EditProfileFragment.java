package com.codervai.campusdeal.screen.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentCompleteProfileBinding;
import com.codervai.campusdeal.databinding.FragmentEditProfileBinding;
import com.codervai.campusdeal.model.Campus;
import com.codervai.campusdeal.model.MyLocation;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.MyDialog;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding mVB;

    private UserViewModel userVM;

    private MyDialog loadingDialog;

    private MyLocation selectedLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentEditProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // init back button
        mVB.backBtn.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        loadingDialog = new MyDialog(getActivity(), R.layout.dialog_loading);

        setProfileDataUI();

        parseLocationFromBackStackEntry();

        // locker picker text input layout click
        mVB.locationEt.setOnClickListener(v -> {
            // show location picker
            // navigate to GoogleMapFragment
            NavHostFragment.findNavController(this).navigate(R.id.action_editProfileFragment_to_googleMapFragment);
        });

        mVB.updateProfileBtn.setOnClickListener(v -> {
            if(validateInput()){
                // save data to firebase
                String name = mVB.nameEt.getText().toString();
                String phoneNumber = mVB.phoneNumberEt.getText().toString();
                String campusName = mVB.campusNameEt.getText().toString();
                String campusType = mVB.campusTypeEt.getText().toString();
                Campus campus = new Campus(
                        campusName,
                        selectedLocation.getLat(),
                        selectedLocation.getLng(),
                        selectedLocation.getFullAddress(),
                        campusType);

                loadingDialog.showDialog("Saving...", R.id.loading_msg_tv);
                Map<String, Object> updatedInfo = new HashMap<>();
                updatedInfo.put("name", name);
                updatedInfo.put("phone", phoneNumber);
                updatedInfo.put("campus", campus);
                userVM.updateProfileData(updatedInfo)
                        .observe(getViewLifecycleOwner(), new Observer<StateData<Boolean>>() {
                            @Override
                            public void onChanged(StateData<Boolean> booleanStateData) {
                                loadingDialog.hideDialog();
                                if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                    Snackbar.make(mVB.getRoot(), "Profile Updated Successfully!", Snackbar.LENGTH_SHORT)
                                            .setDuration(800).show();
                                    NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
                                }else{
                                    Toast.makeText(getContext(), "Error while saving profile info!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void setProfileDataUI() {
        userVM.getUserLiveData().observe(getViewLifecycleOwner(), userStateData -> {
            User user = userStateData.getData();
            if(user == null){
                Snackbar.make(mVB.getRoot(), "Error getting user data", Snackbar.LENGTH_SHORT)
                        .setDuration(800).show();
                return;
            }
            if(mVB.nameEt.getText().toString().isEmpty()){
                mVB.nameEt.setText(user.getName());
                mVB.phoneNumberEt.setText(user.getPhone());
                mVB.campusNameEt.setText(user.getCampus().getName());
                mVB.campusTypeEt.setText(user.getCampus().getType(), false);
                mVB.locationEt.setText(user.getCampus().getFullAddress());

                selectedLocation = new MyLocation(
                        user.getCampus().getLatitude(),
                        user.getCampus().getLongitude(),
                        user.getCampus().getFullAddress());
            }

        });
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

        // check if name is empty
        if(mVB.nameEt.getText().toString().isEmpty()){
            mVB.nameEt.setError("Name can't be empty");
            return false;
        }

        if(mVB.phoneNumberEt.getText().toString().isEmpty()){
            mVB.phoneNumberEt.setError("Phone number is required");
            return false;
        }

        // phone number must be 11 digit
        if(mVB.phoneNumberEt.getText().toString().length() != 11){
            mVB.phoneNumberEt.setError("Phone number must be 11 digit");
            return false;
        }

        if(mVB.campusNameEt.getText().toString().isEmpty()){
            mVB.campusNameEt.setError("Campus name is required");
            return false;
        }

        if(mVB.campusTypeEt.getText().toString().isEmpty()){
            mVB.campusTypeEt.setError("Campus type is required");
            return false;
        }

        if(selectedLocation==null){
            mVB.locationEt.setError("Please select a location");
            return false;
        }

        return true;
    }
}