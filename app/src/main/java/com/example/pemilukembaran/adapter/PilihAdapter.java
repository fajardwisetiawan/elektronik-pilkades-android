package com.example.pemilukembaran.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pemilukembaran.LoginAct;
import com.example.pemilukembaran.R;
import com.example.pemilukembaran.model.PilihModel;
import com.example.pemilukembaran.util.Server;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.pemilukembaran.PilihAct.TAG_ID;
import static com.example.pemilukembaran.PilihAct.TAG_NIK;

public class PilihAdapter extends RecyclerView.Adapter<PilihAdapter.ViewProcessHolder> {

    SharedPreferences sharedpreferences;
    KetikaPilihCallback ketikaPilihCallback;
    Context context;
    private List<PilihModel> item = new ArrayList<>(); //memanggil modelData

    public PilihAdapter(Context context, List<PilihModel> item, KetikaPilihCallback ketikaPilihCallback) {
        this.context = context;
        this.item = item;
        this.ketikaPilihCallback = ketikaPilihCallback;
    }

    @Override
    public ViewProcessHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pilih, parent, false); //memanggil layout list recyclerview
        ViewProcessHolder processHolder = new ViewProcessHolder(view);
        return processHolder;
    }

    @Override
    public void onBindViewHolder(ViewProcessHolder holder, final int position) {

        final PilihModel data = item.get(position);

        Glide.with(context)
                .load(Server.URL_GAMBAR+data.getFoto())
                .into(holder.imgPilih);
        holder.tvNomorCalon.setText(data.getNomor_urut());
        holder.tvNamaCalon.setText(data.getNama());
//        holder.cvPilih.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, BerandaAct.class);
//                // start the activity
//                context.startActivity(intent);
//
//            }
//        });


        holder.cvPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apa Anda yakin untuk memilih Kepala Desa ini?");

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        ketikaPilihCallback.PilihClicked(data.getId_calon(),data.getNama());

                        Intent intent = new Intent(context, LoginAct.class);
                        // start the activity
                        context.startActivity(intent);

//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putBoolean(LoginAct.session_status, false);
//                        editor.putString(TAG_ID, null);
//                        editor.putString(TAG_NIK, null);
//                        editor.commit();
//                        Toast.makeText(context, "Berhasil logout!", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(context, LoginAct.class);
//                        // start the activity
//                        context.startActivity(intent);
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


    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class ViewProcessHolder extends RecyclerView.ViewHolder {

        TextView tvNomorCalon;
        TextView tvNamaCalon;
        CircularImageView imgPilih;
        CardView cvPilih ;

        public ViewProcessHolder(View itemView) {
            super(itemView);

            tvNomorCalon = (TextView) itemView.findViewById(R.id.tvNomorCalon) ;
            tvNamaCalon = (TextView) itemView.findViewById(R.id.tvNamaCalon) ;
            imgPilih = (CircularImageView) itemView.findViewById(R.id.imgPilih);
            cvPilih = (CardView) itemView.findViewById(R.id.cvPilih);

        }
    }

    public interface KetikaPilihCallback {

        void PilihClicked(String id_calon, String nama);
    }
}
