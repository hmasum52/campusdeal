package com.codervai.campusdeal.hilt;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFireStore(){
        return FirebaseFirestore.getInstance();
    }

    // firebase storage provider
    @Provides
    @Singleton
    public static FirebaseStorage provideFirebaseStorage(){
        return FirebaseStorage.getInstance();
    }
}
