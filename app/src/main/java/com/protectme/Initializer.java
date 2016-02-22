package com.protectme;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 * Created by IshanFx on 2/21/2016.
 */
public class Initializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getApplicationContext())
                .rxFactory(new RealmObservableFactory())
                .build();
    }
}
