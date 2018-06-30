package com.example.therick.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JournalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public String mUsername;
    public TextView userName, viewEntry, viewDate, viewTime,updateEntryView;
    ViewFlipper vf;
    EditText mEntry,etUpdateEntry;
    Button submitEntry, backHome, viewBackHome, viewUpdate,updateBackHome, deleteEntry,updateEntry;
    DatabaseReference databasejournal;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ListView listViewEntries;
    List <JournalEntry> entriesList;
    Toolbar toolbar;
    String entryId;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;


    public void addEntry(){
        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(1);
        toolbar.setTitle("Add new entry");
        mEntry = findViewById(R.id.entry);
        mEntry.setText("");

    }

    public void backHome(){
        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(0);
        toolbar.setTitle("Journal App");
    }
    private void openUpdate() {
        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(3);
        toolbar.setTitle("Update entry");
        updateEntryView = findViewById(R.id.updateEntryView);
        updateEntryView.setText(viewEntry.getText());
        etUpdateEntry = findViewById(R.id.etUpdateEntry);
        etUpdateEntry.setText(viewEntry.getText());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journal App");
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Save a new entry to your Journal", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addEntry();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        userName = header.findViewById(R.id.userName);


        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(0);

        listViewEntries = findViewById(R.id.theList);
        entriesList = new ArrayList<>();


        databasejournal = database.getInstance().getReference("entries");
        databasejournal.keepSynced(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        // Choose authentication providers
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //signed in
            Toast.makeText(JournalActivity.this,"your in",Toast.LENGTH_SHORT).show();
            onSigninInit(user.getDisplayName());
            userName.setText(mUsername.toUpperCase());

        }else {
            //signed out
            onSignedOutCleanUp();
            Toast.makeText(JournalActivity.this,"your not logged in",Toast.LENGTH_SHORT).show();

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.logo)
                            .setTheme(R.style.AppThemeNoBar)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        }
        }

};



        mEntry = findViewById(R.id.entry);
        submitEntry = findViewById(R.id.addEntry);
        backHome = findViewById(R.id.addBackHome);
        viewUpdate = findViewById(R.id.viewUpdate);
        viewBackHome = findViewById(R.id.viewBackHome);
        updateBackHome = findViewById(R.id.updateBackHome);
        deleteEntry = findViewById(R.id.deleteEntry);
        updateEntry = findViewById(R.id.updateEntry);

        updateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUpdateEntry = findViewById(R.id.etUpdateEntry);
                String entry = etUpdateEntry.getText().toString();
                String date = viewDate.getText().toString();
                String time = viewTime.getText().toString();

                updateEntry(entryId, entry, date, time);
            }
        });

        deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJournalEntry(entryId);
            }
        });
        submitEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEntry();
            }
        });
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });
        viewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdate();
            }
        });
        updateBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });
        viewBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });
        listViewEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JournalEntry entry = entriesList.get(position);

                entryId = entry.getUid();

                viewEntry = findViewById(R.id.viewEntry);
                viewDate = findViewById(R.id.viewDate);
                viewTime = findViewById(R.id.viewTime);

                viewEntry.setText(entry.getEntry());
                viewDate.setText(entry.getDate());
                viewTime.setText(entry.getTime());

                vf = (ViewFlipper)findViewById(R.id.vf);
                vf.setDisplayedChild(2);



            }
        });


    }

    private void updateEntry(String Id, String entry, String date, String time) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("entries").child(Id);
        databaseReference.keepSynced(true);

        JournalEntry j = new JournalEntry(mUsername,entry,date,time,Id);
        databaseReference.setValue(j);
        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(0);
        Toast.makeText(JournalActivity.this,"Entry has been Updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteJournalEntry(String entryId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("entries").child(entryId);
        databaseReference.keepSynced(true);
        databaseReference.removeValue();
        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(0);
        Toast.makeText(JournalActivity.this,"Entry has been deleted", Toast.LENGTH_SHORT).show();
    }




    private void addNewEntry() {
        String entryBody = mEntry.getText().toString().trim();
        Date d = new Date();
        CharSequence date  = DateFormat.format("EEE, MMMM d, yyyy ", d.getTime());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(c.getTime());

        if (!TextUtils.isEmpty(entryBody)){
            String id = databasejournal.push().getKey();

            JournalEntry journalEntry = new JournalEntry(mUsername, entryBody, date.toString(), time, id);
            databasejournal.child(id).setValue(journalEntry);
            mEntry.clearComposingText();
            vf = (ViewFlipper)findViewById(R.id.vf);
            vf.setDisplayedChild(0);
            Toast.makeText(JournalActivity.this,"Entry added", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(JournalActivity.this,"You must submit something", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSignedOutCleanUp() {
        mUsername = null;

    }

    private void onSigninInit(String displayName) {
    mUsername = displayName;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            }else if(requestCode == RESULT_CANCELED){
                finish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(vf.getDisplayedChild() == 1){

            vf.setDisplayedChild(0);
        }else if(vf.getDisplayedChild() == 2){

            vf.setDisplayedChild(0);
        }else if(vf.getDisplayedChild() == 3){

            vf.setDisplayedChild(2);
        }else {
            if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getBaseContext(), "Press once again to exit!",
                        Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.journal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_logout) {
            AuthUI.getInstance().signOut(this);


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            addEntry();
        }  else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this);

        }  else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out the Journal app at: https://play.google.com/store/apps/details?id=com.hytech256.therick.journalapp");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databasejournal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entriesList.clear();
                for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()){
                    JournalEntry journalEntry = entrySnapshot.getValue(JournalEntry.class);
                    entriesList.add(journalEntry);

                }

                JournalAdaptor journalAdaptor = new JournalAdaptor(JournalActivity.this,entriesList);
                listViewEntries.setAdapter(journalAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
