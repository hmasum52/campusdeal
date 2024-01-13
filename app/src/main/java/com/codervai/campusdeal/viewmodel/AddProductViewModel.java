package com.codervai.campusdeal.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddProductViewModel extends ViewModel {
    // image uri list
    private final MutableLiveData<List<Uri>> imageUriList = new MutableLiveData<>();

    // category index
    private final MutableLiveData<Integer> selectedCategoryIndex = new MutableLiveData<>();

    FirebaseStorage fStorage;
    FirebaseFirestore fStore;

    @Inject
    public AddProductViewModel(FirebaseStorage fStorage, FirebaseFirestore fStore){
        this.fStorage = fStorage;
        this.fStore = fStore;
    }

    public MutableLiveData<List<Uri>> getImageUriList() {
        return imageUriList;
    }

    public void addToImageUriList(List<Uri> imageUriList) {
        if (this.imageUriList.getValue() == null) {
            this.imageUriList.setValue(imageUriList);
            return;
        }
        List<Uri> currentImageUriList = this.imageUriList.getValue();
        currentImageUriList.addAll(imageUriList);
        this.imageUriList.setValue(currentImageUriList);
    }

    public MutableLiveData<Integer> getSelectedCategoryIndex() {
        return selectedCategoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        selectedCategoryIndex.setValue(categoryIndex);
    }

    // uploading images
    StateLiveData<List<String>> uploadedImageUrls;

    public StateLiveData<List<String>> uploadImages(Context context){
        uploadedImageUrls = new StateLiveData<>();

        uploadToFirebase(new ArrayList<>(), context);

        return uploadedImageUrls;
    }

    private void uploadToFirebase(@NonNull List<String> firebaseUrlList, Context context){
        if(selectedCategoryIndex.getValue() == null){
            uploadedImageUrls.postError(new Exception("Category not selected!"));
            return;
        }
        String category = Constants.CATEGORY_LIST.get(selectedCategoryIndex.getValue());
        StorageReference storageRef = fStorage.getReference("images/product/"+category);

        if(imageUriList.getValue() == null){
            uploadedImageUrls.postError(new Exception("No image picked!"));
            return;
        }
        // current image to be uploaded
        Uri uri = imageUriList.getValue().get(firebaseUrlList.size());

        String filename = UUID.randomUUID().toString();

        StorageReference fileRef = storageRef.child(filename);

        try{
            // compress the file and covert to byte array
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            // upload the compressed file
            fileRef.putBytes(data)
                    .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                        // get the download url
                        fileRef.getDownloadUrl().addOnSuccessListener((Uri downloadUri) -> {
                            // add the download url to the list
                            firebaseUrlList.add(downloadUri.toString());

                            // //if same size so all image is uploaded, then sent list of url to to some method to upload the ad
                            if(firebaseUrlList.size() == imageUriList.getValue().size()){
                                // upload the ad to firebase firestore
                                uploadedImageUrls.postSuccess(firebaseUrlList);
                            }else{
                                // upload the next image
                                uploadToFirebase(firebaseUrlList, context);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        uploadedImageUrls.postError(e);
                        return;
                    });
        }catch (Exception e){
            uploadedImageUrls.postError(e);
        }
    }


    public StateLiveData<Void> uploadProduct(Product product){
        StateLiveData<Void> returnValue = new StateLiveData<>();

        fStore.collection(Constants.PRODUCT_COLLECTION)
                .document(product.getId())
                .set(product)
                .addOnSuccessListener(unused -> {
                    returnValue.postSuccess(null);
                }).addOnFailureListener(e -> {
                    returnValue.postError(e);
                });
        return  returnValue;
    }
}
