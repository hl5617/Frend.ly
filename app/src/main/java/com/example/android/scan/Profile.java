package com.example.android.scan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
    private List<String> profileLines;

    private TextView mTextMessage;
    private TextView mTestView;
    private EditText mNameInput;
    private AutoCompleteTextView mInterestInput;
    private Button mAddButton;
    private Button mNameSetButton;

    private List<String> interestList;
    private String[] hobbies;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private void readProfileToList() throws IOException {
        profileLines = new ArrayList<>();
        FileInputStream fstream = new FileInputStream(PROFILE_FILENAME);
        Scanner br = new Scanner(new InputStreamReader(fstream));
        while (br.hasNext()) {
            String line = br.nextLine();
            profileLines.add(line);
        }
        fstream.close();
    }

    private void updateHobbyListDisplay() {
        mTestView.setText("updateHobbyListDisplay");
        //todo: update the displayed ui element that has all the hobbies
        for (String pl : profileLines) {
            // todo:
        }
    }

    private boolean addToHobbyList(int i) {
        mTestView.setText("addToHobbyList");

        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(profile, true);
        } catch (FileNotFoundException e) {
            return false;
        }
        String hobbyIntStr = Integer.toString(i);
        if (!profileLines.contains(hobbyIntStr)) {
            profileLines.add(hobbyIntStr);

            PrintWriter pw = new PrintWriter(fstream);
            pw.append(hobbyIntStr);
            pw.append("\n");
            pw.close();
            updateHobbyListDisplay();
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
        // todo: updated hobby list ui
        return false;
    }

    private void updateName(String newName) throws IOException {
        mTestView.setText("updateName");

        final PrintWriter writer = new PrintWriter(profile);

        if (profileLines.isEmpty()) {
            writer.print(newName);
            writer.close();
            return;
        }

        profileLines.set(0, newName);
        for (String pl : profileLines) {
            writer.println(pl);
        }
        writer.close();
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
                readProfileToList();
                mTestView.setText("not createnewfile");
            } else {
                profileLines = new ArrayList<>();
                mTestView.setText("createnewfile");
            }
        } catch (IOException e) {
            Log.d("ERR","sholdnt");
        }

        hobbies = (String[]) getResources().getStringArray(R.array.hobby_array);

        mAddButton = (Button) findViewById(R.id.button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToHobbyList(mInterestInput.getListSelection());
            }
        });

        mInterestInput = (AutoCompleteTextView) findViewById(R.id.at_interest_input);
        // TODO: only allow autocomplete hobby values
        ArrayAdapter<String> mInterestInputAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Arrays.asList(hobbies));
        mInterestInput.setAdapter(mInterestInputAdapter);

        mNameInput = (EditText) findViewById(R.id.et_name);
        mNameSetButton = (Button) findViewById(R.id.button_set);
        mNameSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateName(mNameInput.getText().toString());
                } catch (IOException e) {
                    Log.d("ERR", "shouldnt get here 3");
                }
            }
        });

        updateHobbyListDisplay();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
