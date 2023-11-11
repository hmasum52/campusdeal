package com.codervai.campusdeal.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentCompleteProfileBinding;
import com.codervai.campusdeal.model.Campus;
import com.codervai.campusdeal.util.MyDialog;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class CompleteProfileFragment extends Fragment {
    private FragmentCompleteProfileBinding mVB;

    private UserViewModel userVM;

    private MyDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentCompleteProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingDialog = new MyDialog(getActivity(), R.layout.dialog_loading);

        // locker picker text input layout click
        mVB.locationEt.setOnClickListener(v -> {
            // show location picker
            // navigate to GoogleMapFragment
            NavHostFragment.findNavController(this).navigate(R.id.action_completeProfileFragment_to_googleMapFragment);
        });

        mVB.completeProfileBtn.setOnClickListener(v -> {
            if(validateInput()){
                // save data to firebase
                String phoneNumber = mVB.phoneNumberEt.getText().toString();

                String campusName = mVB.campusNameEt.getText().toString();
                String campusType = mVB.campusTypeEt.getText().toString();
                Campus campus = new Campus(campusName, 1, 2, "Dhaka", campusType);

                loadingDialog.showDialog("Saving...", R.id.loading_msg_tv);
                userVM.saveProfileCompleteData(phoneNumber, campus)
                        .observe(getViewLifecycleOwner(), new Observer<StateData<Boolean>>() {
                            @Override
                            public void onChanged(StateData<Boolean> booleanStateData) {
                                loadingDialog.hideDialog();
                                if(booleanStateData.getStatus() == StateData.DataStatus.SUCCESS){
                                    MainActivity.navigateToStartDestination(getActivity(),
                                            R.id.action_completeProfileFragment_to_homeFragment,
                                            R.id.homeFragment);
                                }else{
                                    Toast.makeText(getContext(), "Error while saving profile info!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean validateInput() {
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


        return true;
    }
}