package com.example.demo.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class JumlahHargaRestoran {

    private int qty;
    private Long hargaSatuan;
    private String ukuran;
    private Long subtotal;

    public JumlahHargaRestoran() {}

    public void hitungSubtotal() {
        long harga = hargaSatuan;

        if (ukuran.equalsIgnoreCase("Large")) {
            harga += 5000; // tambahan untuk ukuran besar
        }

        subtotal = harga * qty;
    }

    // Getter & Setter
    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Long getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(Long hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }
}
