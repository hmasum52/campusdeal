package com.codervai.campusdeal.viewmodel;

import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateLiveData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProductViewModel extends ViewModel {

    FirebaseFirestore db;

    private StateLiveData<List<Product>> products;

    @Inject
    public ProductViewModel(FirebaseFirestore db) {this.db = db;}

    public StateLiveData<List<Product>> getProducts(String userId) {
        fetchProducts(userId);
        return products;
    }

    private void fetchProducts(String userId) {
        products = new StateLiveData<>();

        db.collection(Constants.PRODUCT_COLLECTION)
                .whereEqualTo("sellerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        products.postSuccess(new ArrayList<>());
                        return;
                    }
                    products.postSuccess(queryDocumentSnapshots.toObjects(Product.class));
                })
                .addOnFailureListener(e -> products.postError(new Exception("Error fetching products")));
    }
}
