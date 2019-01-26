package com.example.android.scan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Profile extends AppCompatActivity {

    public static final String PROFILE_FILENAME = "profile.txt";

    private File profile;
    private String name;
    private List<String> hobbies;

    private TextView mTextMessage;
    private TextView mTestView;
    private TextView mName;
    private EditText mNameInput;
    private AutoCompleteTextView mHobbyInput;
    private int hobbyInt;
    private Button mAddButton;
    private Button mClearButton;

    private TextView mHobbiesList;
    private HobbiesAdapter mHobbiesListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mNameSetButton;

    private String[] possibleHobbies;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

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
                mName.setText("Name: " + name);
                mNameInput.setText(name);
            }
            while(line != null){
                line = reader.readLine();
                if (line != null) {
                    hobbies.add(possibleHobbies[Integer.parseInt(line)]);
                }
            }
        } else {
            mTestView.setText("file does not exist");
        }
        updateHobbyListDisplay();
    }

    private void updateHobbyListDisplay() {
        if (hobbies.isEmpty()) {
            mHobbiesList.setText("Hobbies");
            mTestView.setText("updateHobby: empty list");
            return;
        }
        mTestView.setText("updateHobbyListDisplay");
        StringBuilder phatString = new StringBuilder();
        for (String h : hobbies) {
            phatString.append(h);
            phatString.append('\n');
        }
        // gets to at least here before crashing
        mHobbiesList.setText(phatString.toString());
    }

    private boolean addToHobbyList(int i) {
        mTestView.setText("addToHobbyList, id: " + Integer.toString(i));

        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(profile, true);
        } catch (FileNotFoundException e) {
            return false;
        }
        if (!hobbies.contains(possibleHobbies[i])) {
            hobbies.add(possibleHobbies[i]);
            updateHobbyListDisplay();

            PrintWriter pw = new PrintWriter(fstream);
            pw.append(Integer.toString(i));
            pw.append('\n');
            pw.close();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "hobby added",
                    Toast.LENGTH_SHORT);
            toast.show();

            return true;
        }
        Toast toast = Toast.makeText(getApplicationContext(),
                "hobby already added",
                Toast.LENGTH_SHORT);
        toast.show();
        return false;
    }

    private boolean removeFromHobbyList(int i) {
        mTestView.setText("removeFromHobbyList");
        if (hobbies.remove(possibleHobbies[i])) {
            try {
                updateProfile();
            } catch (IOException e) {
                Log.d("ERR", "IOException");
                return false;
            }
            return true;
        }

        return false;
    }

    private void updateProfile() throws IOException {
        final PrintWriter writer = new PrintWriter(profile);

        writer.println(name);
        for (String h : hobbies) {
            writer.println(h);
        }
        writer.close();
    }

    private void updateName(String newName) {
        name = newName;
        mName.setText("Name: " + name);
        mTestView.setText("updateName");
        try {
            updateProfile();
        } catch (IOException e) {
            Log.d("ERR", "IOException");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.getApplicationContext();
        setContentView(R.layout.activity_profile);

        mTestView = (TextView) findViewById(R.id.tvTest);

        mHobbiesList = (TextView) findViewById(R.id.tv_hobbies_list);
        mName = (TextView) findViewById(R.id.tv_name);

        profile = new File(context.getFilesDir(), PROFILE_FILENAME);

        if (profile.exists()) {
            try {
                readProfile();
            } catch (IOException e){
                mTestView.setText("kdjfskjd");
            }
        }

        possibleHobbies = (String[]) getResources().getStringArray(R.array.hobby_array);

        mHobbyInput = (AutoCompleteTextView) findViewById(R.id.at_interest_input);
        // TODO: only allow autocomplete hobby values
        ArrayAdapter<String> mInterestInputAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Arrays.asList(possibleHobbies));
        mHobbyInput.setAdapter(mInterestInputAdapter);
        mHobbyInput.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                hobbyInt = -1;

                for (int i = 0; i < possibleHobbies.length; i++) {
                    if (possibleHobbies[i].equals(selection)) {
                        hobbyInt = i;
                        break;
                    }
                }
            }
        });

        mAddButton = (Button) findViewById(R.id.button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToHobbyList(hobbyInt);
            }
        });


        mNameInput = (EditText) findViewById(R.id.et_name);
        mNameSetButton = (Button) findViewById(R.id.button_set);
        mNameSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName(mNameInput.getText().toString());
            }
        });

        mClearButton = (Button) findViewById(R.id.button_clear);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hobbies.clear();
                updateHobbyListDisplay();
                try {
                    updateProfile();
                } catch (IOException e) {
                    Log.d("ERR", "failed to update profile on clear button press");
                }
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
