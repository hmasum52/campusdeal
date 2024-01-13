package com.codervai.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<String, StateLiveData<List<Product>>> allProductMap = new HashMap<>();

    public void fetchProductsByCategory(String category, long limit){
        Log.d("fetch", "category "+category);
        String userId = FirebaseAuth.getInstance().getUid();

        Query query = db.collection(Constants.PRODUCT_COLLECTION)
                .where(
                        Filter.and(
                                Filter.equalTo("category", category),
                                Filter.notEqualTo("sellerId", userId)
                        )
                )
                .orderBy("sellerId", Query.Direction.DESCENDING)
                .orderBy("uploadDate", Query.Direction.DESCENDING);

        if(limit>0) query = query.limit(limit);

        query.get().addOnCompleteListener(task -> {
            if(allProductMap.get(category) == null) allProductMap.put(category, new StateLiveData<>());
            if(task.isSuccessful()){
                List<Product> productList = task.getResult().toObjects(Product.class);
                allProductMap.get(category).postSuccess(productList);
            }else{
                Log.d("fetch->", task.getException().getMessage());
                allProductMap.get(category).postError(new Exception("Error fetching products"));
            }
        });
    }

    public StateLiveData<List<Product>> getProductsByCategory(String category, long limit){
        allProductMap.put(category, new StateLiveData<>());
        fetchProductsByCategory(category, limit);
        return allProductMap.get(category);
    }
}
