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
    private EditText mNameInput;
    private AutoCompleteTextView mHobbyInput;
    private int hobbyInt;
    private Button mAddButton;

    private RecyclerView mHobbiesList;
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
        hobbies = new ArrayList<>();
        FileInputStream fstream = new FileInputStream(PROFILE_FILENAME);
        Scanner br = new Scanner(new InputStreamReader(fstream));
        if (br.hasNext()) {
            name = br.nextLine();
        }
        while (br.hasNext()) {
            String line = br.nextLine();
            hobbies.add(line);
        }
        fstream.close();
    }

    private void updateHobbyListDisplay() {
        mTestView.setText("updateHobbyListDisplay");
        mHobbiesListAdapter.updateList(hobbies);
    }

    private boolean addToHobbyList(int i) {
        mTestView.setText("addToHobbyList");

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
        // todo: remove from file
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

        profile = new File(context.getFilesDir(), PROFILE_FILENAME);
        try {
            if (!profile.createNewFile()) {
                readProfile();
                mTestView.setText("not createnewfile");
            } else {
                hobbies = new ArrayList<>();
                mTestView.setText("createnewfile");
            }
        } catch (IOException e) {
            Log.d("ERR","sholdnt");
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

        mHobbiesList = findViewById(R.id.rv_interests_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mHobbiesList.setLayoutManager(layoutManager);

        mLayoutManager = new LinearLayoutManager(this);
        mHobbiesList.setLayoutManager(mLayoutManager);
        mHobbiesListAdapter = new HobbiesAdapter(hobbies);
        mHobbiesList.setAdapter(mHobbiesListAdapter);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
