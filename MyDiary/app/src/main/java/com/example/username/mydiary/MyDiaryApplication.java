package com.example.username.mydiary;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyDiaryApplication extends Application {
    /**
     * アプリケーション立ち上げの最初にだけ実行される
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // データーベース初期化
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}