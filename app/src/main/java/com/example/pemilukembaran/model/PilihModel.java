package com.example.pemilukembaran.model;

public class PilihModel {

    String id_calon;
    String foto;
    String nomor_urut;
    String nama;

    public PilihModel() {
        this.id_calon = id_calon;
        this.foto = foto;
        this.nomor_urut = nomor_urut;
        this.nama = nama;
    }

    public String getId_calon() {
        return id_calon;
    }

    public void setId_calon(String id_calon) {
        this.id_calon = id_calon;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNomor_urut() {
        return nomor_urut;
    }

    public void setNomor_urut(String nomor_urut) {
        this.nomor_urut = nomor_urut;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
