package com.example.therick.journalapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class JournalApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
