/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean _signUpModeActive = true;
    TextView _loginTextView;
    EditText _usernameEditText;
    EditText _passwordEditText;
    ImageView _logoImageView;
    RelativeLayout _backgroundLayout;


    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClicked(v);
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _loginTextView = (TextView) findViewById(R.id.loginTextView);
        _usernameEditText = (EditText) findViewById(R.id.userNameEditText);
        _passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        _passwordEditText.setOnKeyListener(this);
        _loginTextView.setOnClickListener(this);
        _logoImageView = (ImageView) findViewById(R.id.logoImageView);
        _backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
        _logoImageView.setOnClickListener(this);
        _backgroundLayout.setOnClickListener(this);


        //check to see if there is a user already logged in
        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginTextView) {

            Button signupButton = (Button) findViewById(R.id.signupButton);
            if (_signUpModeActive) {
                _signUpModeActive = false;
                signupButton.setText("Login");
                _loginTextView.setText("or, Sign Up");
            } else {
                _signUpModeActive = true;
                signupButton.setText("Sign up");
                _loginTextView.setText("or, Login");
            }
        } else if ((v.getId() == R.id.logoImageView) || (v.getId() == R.id.backgroundLayout)){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void signUpClicked(View view) {

        if ((_usernameEditText.getText().toString().matches("")) || (_passwordEditText.getText().toString().matches(""))) {
            Toast.makeText(this, "A username and a password are required", Toast.LENGTH_SHORT).show();
        } else {
            if (_signUpModeActive) {
                //signup
                ParseUser user = new ParseUser();
                user.setUsername(_usernameEditText.getText().toString());
                user.setPassword(_passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup status", "Successful");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                //login
                ParseUser.logInInBackground(_usernameEditText.getText().toString(), _passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Log.i("Login status", "successful");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}