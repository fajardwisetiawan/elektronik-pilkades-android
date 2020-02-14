package com.example.pemilukembaran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.pemilukembaran.adapter.PilihAdapter;
import com.example.pemilukembaran.app.AppController;
import com.example.pemilukembaran.model.PilihModel;
import com.example.pemilukembaran.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PilihAct extends AppCompatActivity implements PilihAdapter.KetikaPilihCallback {

    public static final String EXTRA_ID = "id_calon";
    public static final String EXTRA_FOTO = "foto";
    public static final String EXTRA_NO_URUT = "no_urut";
    public static final String EXTRA_NAMA = "nama";

    private String url = Server.URL + "tampil_calon.php";

    Button btnTidakMemilih;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ProgressDialog pd;
    List<PilihModel> mItems;

    int success;
    private static final String TAG = PilihAct.class.getSimpleName();
    private String urli = Server.URL + "klikpilih.php";
    private String urlii = Server.URL + "klikgolput.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";
    String pemesanan_id;

    String id_pemilih, nik, rw, status;
    SharedPreferences sharedpreferences;

    public static final String my_shared_preferences = "my_shared_preferences";

    public static final String TAG_ID = "id_pemilih";
    public static final String TAG_NIK = "nik";
    public static final String TAG_RW = "rw";
    public static final String TAG_STATUS = "status";

    public static final String id_calonn = "0";
    public static final String namaa = "Tidak Memilih";

    ActionBar actionbar;
    TextView textview;
    LinearLayout.LayoutParams layoutparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih);

        sharedpreferences = getSharedPreferences(LoginAct.my_shared_preferences, Context.MODE_PRIVATE);

        id_pemilih = getIntent().getStringExtra(TAG_ID);
        nik = getIntent().getStringExtra(TAG_NIK);
        rw = getIntent().getStringExtra(TAG_RW);
        status = getIntent().getStringExtra(TAG_STATUS);

        pd = new ProgressDialog(PilihAct.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvPilihCalon);
        btnTidakMemilih = (Button) findViewById(R.id.btnTidakMemilih);
        mItems = new ArrayList<>();

//      mAdapter.setOnItemClickListener(TroubleShootingAct.this);

        btnTidakMemilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PilihAct.this);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apa Anda yakin untuk menerima penyewaan ini?");

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        golputPilih(id_calonn, namaa);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        loadjson();
    }
    private void loadjson(){
        pd.setMessage("Mengambil Data..");
        pd.setCancelable(false);
        pd.show();

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pd.cancel();
                Log.d("volley", "response : " + response.toString());
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject data = response.getJSONObject(i);
                        PilihModel md = new PilihModel();
                        md.setId_calon(data.getString("id_calon"));
                        md.setFoto(data.getString("foto"));
                        md.setNomor_urut(data.getString("no_urut"));
                        md.setNama(data.getString("nama"));// memanggil nama array yang kita buat
                        mItems.add(md);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mManager = new LinearLayoutManager(PilihAct.this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(mManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(dividerItemDecoration);
                mAdapter = new PilihAdapter(PilihAct.this, mItems,PilihAct.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(arrayRequest);
    }
    @Override
    public void PilihClicked(final String id_calon, final String nama) {

        StringRequest strReq = new StringRequest(Request.Method.POST, urli, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());

                     //   Toast.makeText(PilihAct.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

//                        Intent intent = new Intent(PilihAct.this, LoginAct.class);
//                        intent.putExtra("status",1);
//                        startActivity(intent);
//                        finish();

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(LoginAct.session_status, false);
                        editor.putString(TAG_ID, null);
                        editor.putString(TAG_NIK, null);
                        editor.putString(TAG_RW, null);
                        editor.clear();
                        editor.commit();
                        Toast.makeText(PilihAct.this, "Terimakasih atas suara Anda", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PilihAct.this, LoginAct.class);
                        intent.putExtra("status",1);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(PilihAct.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(PilihAct.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_calon", id_calon);
                params.put("nama", nama);
                params.put("nik", nik);
                params.put("rw", rw);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void golputPilih(final String id_calonn, final String namaa){
        StringRequest strReq = new StringRequest(Request.Method.POST, urlii, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(LoginAct.session_status, false);
                        editor.putString(TAG_ID, null);
                        editor.putString(TAG_NIK, null);
                        editor.putString(TAG_RW, null);
                        editor.clear();
                        editor.commit();
                        Toast.makeText(PilihAct.this, "Terimakasih atas suara Anda", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PilihAct.this, LoginAct.class);
                        intent.putExtra("status",1);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(PilihAct.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(PilihAct.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_calon", id_calonn);
                params.put("nama", namaa);
                params.put("nik", nik);
                params.put("rw", rw);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
