package com.example.pemilukembaran;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.pemilukembaran.app.AppController;
import com.example.pemilukembaran.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAct extends AppCompatActivity {

    ProgressDialog pDialog;
    Intent intent;

    private EditText etNik, etPassword;
    private Button btnLogin;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "login.php";

    private static final String TAG = LoginAct.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_STATUS = "status";
    public final static String TAG_NIK = "nik";
    public final static String TAG_ID = "id_pemilih";
    public final static String TAG_RW = "rw";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id_pemilih, nik, rw, status;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        etNik = findViewById(R.id.etNikLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id_pemilih = sharedpreferences.getString(TAG_ID, null);
        nik = sharedpreferences.getString(TAG_NIK, null);
        rw = sharedpreferences.getString(TAG_RW, null);
        status = sharedpreferences.getString(TAG_STATUS, null);
//
//        if (session) {
//            Intent intent = new Intent(LoginAct.this, PilihAct.class);
//            intent.putExtra(TAG_ID, id_pemilih);
//            intent.putExtra(TAG_NIK, nik);
//            intent.putExtra(TAG_STATUS, status);
//            finish();
//            startActivity(intent);
//        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String nik = etNik.getText().toString();
                String password = etPassword.getText().toString();

                // mengecek kolom yang kosong
                if (nik.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(nik, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkLogin(final String nik, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String nik = jObj.getString(TAG_NIK);
                        String rw = jObj.getString(TAG_RW);
                        String id_pemilih = jObj.getString(TAG_ID);
                        String status = jObj.getString(TAG_STATUS);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id_pemilih);
                        editor.putString(TAG_NIK, nik);
                        editor.putString(TAG_RW, rw);
                        editor.putString(TAG_STATUS, status);
                        editor.commit();

                        // Memanggil main activity
//                        Intent intent = new Intent(LoginAct.this, HomeAct.class);
//                        intent.putExtra(TAG_ID, id);
//                        intent.putExtra(TAG_USERNAME, username);
//                        intent.putExtra(TAG_KETERANGAN, keterangan);
//                        finish();
//                        startActivity(intent);
                        if (status.equals("0")) {
                            Intent intent = new Intent(LoginAct.this, PilihAct.class);
                            intent.putExtra(TAG_ID, id_pemilih);
                            intent.putExtra(TAG_NIK, nik);
                            intent.putExtra(TAG_RW, rw);
                            intent.putExtra(TAG_STATUS, status);
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginAct.this, "Maaf Anda sudah melakukan pemilihan Kepala Desa!", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nik", nik);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}