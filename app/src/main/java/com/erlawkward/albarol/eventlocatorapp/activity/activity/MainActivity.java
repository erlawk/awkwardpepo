package com.erlawkward.albarol.eventlocatorapp.activity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.erlawkward.albarol.eventlocatorapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;


;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnNext;
    private Button btnSave;
    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CheckBox btn_music = (CheckBox) findViewById(R.id.btn_music);
        final CheckBox btn_food = (CheckBox) findViewById(R.id.btn_food);
        final CheckBox btn_festival = (CheckBox) findViewById(R.id.btn_festival);
        final CheckBox btn_sports = (CheckBox) findViewById(R.id.btn_sports);
        final CheckBox btn_party = (CheckBox) findViewById(R.id.btn_party);
        final CheckBox btn_social = (CheckBox) findViewById(R.id.btn_social);

        btnLogout = (Button) findViewById(R.id.btn_Logout);
        btnNext = (Button) findViewById(R.id.btn_Next);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);

        btnSave = (Button) findViewById(R.id.btn_Save);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        final String not_selected = "Not selected";
        final String selected = "Selected";

        // SqLite daabase handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Fetching user details from sqlite
                db = new SQLiteHandler(getApplication());
                //session = new SessionManager(getApplication());
                HashMap<String, String> user = db.getUserDetails();

                String userID = user.get("name");
                String emailAddress = user.get("emailAddress");

                // Displaying the user details on the screen
                txtName.setText(userID);
                txtEmail.setText(emailAddress);

                String music;
                String food;
                String festival;
                String sports;
                String social;
                String party;

                if (btn_music.isChecked()) {
                    music = selected;
                } else {
                    music = not_selected;
                }
                if (btn_food.isChecked()) {
                    food = selected;
                } else {
                    food = not_selected;
                }
                if (btn_festival.isChecked()) {
                    festival = selected;
                } else {
                    festival = not_selected;
                }
                if (btn_sports.isChecked()) {
                    sports = selected;
                } else {
                    sports = not_selected;
                }
                if (btn_social.isChecked()) {
                    social = selected;
                } else {
                    social = not_selected;
                }
                if (btn_party.isChecked()) {
                    party = selected;
                } else {
                    party = not_selected;
                }

                //registering preference
                storePreference(Integer.parseInt(userID), music, food, festival, sports, social, party);

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainEventActivity.class);
                //i.putExtra("userID", userID);
                startActivity(i);
                finish();
            }
        });
    }
        /**
         * Function to store user preferences in MySQL database will post params(tag, name,
         * email, password) to register url
         * */

        private void storePreference(final int userID, final String music, final String food, final String festival, final String sports,
         final String social, final String party) {
        String tagstring_req = "req_store";

        pDialog.setMessage("Storing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_PREFERENCE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Storing Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User preference successfully stored in MySQL
                        // Now store the user preference in sqlite
                        //String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String userID = user.getString("userID");
                        String music = user.getString("music");
                        String food = user.getString("food");
                        String festival = user.getString("festival");
                        String social = user.getString("social");
                        String sports = user.getString("sports");
                        String party = user.getString("party");

                        db.addPreference(userID, music, food, festival, social, sports, party);

                        Toast.makeText(getApplicationContext(), "User preference successfully registered.", Toast.LENGTH_LONG);


                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Registration Error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(),
                        volleyError.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", String.valueOf(userID));
                params.put("music", music);
                params.put("food", food);
                params.put("festival", festival);
                params.put("social", social);
                params.put("sports", sports);
                params.put("party", party);


                return params;
            }
        };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tagstring_req);
        }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        db.deletePreference();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}