package com.example.android.scan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;

public class Profile extends AppCompatActivity {

    public static final String PROFILE_FILENAME = "profile.txt";

    private TextView mTextMessage;
    private EditText mNameInput;
    private AutoCompleteTextView mInterestInput;
    private Button mAddButton;

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

    private void loadProfile() throws IOException {
        final File profile = new File(this.getApplicationContext().getFilesDir(), PROFILE_FILENAME);
        if (!profile.createNewFile()) {
            Scanner br = new Scanner(new InputStreamReader(in));
            if (br.hasNext()) {
                String name = br.nextLine();
                mNameInput.setText(name);
            }
            while (br.hasNext()) {
                String interestKey = br.nextLine();
                if (!TextUtils.isDigitsOnly(interestKey)) {
                    Log.d("ERR", "shouldnt get here");
                } else {
                    addToInterestList(hobbies[Integer.parseInt(interestKey)]);
                }
            }
            return;
        }
    }

    private void addToInterestList(String hobby) {
        interestList.add(hobby);
        //todo: add to ui
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        hobbies = (String[]) getResources().getStringArray(R.array.hobby_array);

        mAddButton = (Button) findViewById(R.id.button); //listener must call addinterest

        mInterestInput = (AutoCompleteTextView) findViewById(R.id.at_interest_input);
        ArrayAdapter<String> mInterestInputAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Arrays.asList(hobbies));
        mInterestInput.setAdapter(mInterestInputAdapter);

        mNameInput = (EditText) findViewById(R.id.et_name); //listener must edit text in first line

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*try {
            loadProfile();
        } catch (IOException e) {
            Log.d("ERR", "shouldnt get here 2");
        }*/
    }

}
