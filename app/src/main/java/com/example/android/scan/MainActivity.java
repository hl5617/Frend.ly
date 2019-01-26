package com.example.android.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("steve");
        listDataHeader.add("waldo");

        List<String> steve = new ArrayList<String>();
        steve.add("Tea bag collecting");
        steve.add("Bird watching");
        steve.add("Amateur yodelling");

        List<String> waldo = new ArrayList<String>();
        waldo.add("Gunsmithing");
        waldo.add("Swimming");
        waldo.add("Hunting");

        listDataChild.put(listDataHeader.get(0), waldo); // Header, Child data
        listDataChild.put(listDataHeader.get(1), steve);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
