package com.example.android.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.scan.Profile.PROFILE_FILENAME;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    gotoProfile();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private List<String> hobbies;
    private String name;
    private File profile;
    private String[] possibleHobbies;

    private void readProfile() throws IOException {
        //mTestView.setText("readProfile");
        hobbies = new ArrayList<>();
        FileInputStream is;
        BufferedReader reader;
        if (profile.exists()) {
            is = new FileInputStream(profile);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if (line != null) {
                name = line;
            }
            while(line != null){
                line = reader.readLine();
                if (line != null) {
                    hobbies.add(possibleHobbies[Integer.parseInt(line)]);
                }
            }
        }
    }

    private void giveNoName() throws IOException {
        name = "name not set";
        final PrintWriter writer = new PrintWriter(profile);

        writer.println(name);
        writer.close();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        List<String> steveHobbies = new ArrayList<String>();
        steveHobbies.add("Tea bag collecting");
        steveHobbies.add("Bird watching");
        steveHobbies.add("Amateur yodelling");

        List<String> waldoHobbies = new ArrayList<String>();
        waldoHobbies.add("Gunsmithing");
        waldoHobbies.add("Swimming");
        waldoHobbies.add("Hunting");

        addDude("waldo", waldoHobbies);
        addDude("steve", steveHobbies);
    }

    private void addDude(String name, List<String> theirHobbies) {
        listDataHeader.add(name);
        List<String> theirHobbiesFiltered = new ArrayList<>();
        for (String h : theirHobbies) {
            if (hobbies.contains(h)) {
                theirHobbiesFiltered.add(h);
            }
        }
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), theirHobbiesFiltered);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        possibleHobbies = (String[]) getResources().getStringArray(R.array.hobby_array);

        profile = new File(this.getApplicationContext().getFilesDir(), PROFILE_FILENAME);

        if (profile.exists()) {
            try {
                readProfile();
            } catch (IOException e){
                Log.d("ERR", "IOEXc");
            }
        } else {
            try {
                profile.createNewFile();
                hobbies = new ArrayList<>();
                giveNoName();
            } catch (IOException e) {
                Log.d("ERR", "IOEXc");
            }
        }


        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void gotoProfile() {
        Context context = MainActivity.this;
        Class destinationActivity = Profile.class;
        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

}
