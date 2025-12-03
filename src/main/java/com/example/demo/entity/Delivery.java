package com.example.demo.entity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Delivery extends Transaksi implements Serve {
    private String metodePengantaran;
    private String lokasiPengantaran;

    @ElementCollection
    @CollectionTable(name = "pesanan_menu_delivery",
        joinColumns = @JoinColumn(name = "transaksi_id"))
        @MapKeyColumn(name = "menu_id")       
    private Map<String, JumlahHargaRestoran> pesanan = new HashMap<>();

    //constructor
    public Delivery(){}
    //quantity, ukuran, dan ObjMenu diammbil dari method tambahPesanan di parent class
    public Delivery(String mp, String lp) {
        super();
        this.metodePengantaran = mp;
        this.lokasiPengantaran = lp;
    }

    public void setLokasiPengantaran(String lp) {
        this.lokasiPengantaran = lp;
    }

    public void setMetodePengantaran(String mp) {
        this.metodePengantaran = mp;
    }

    public String getMetodePengantaran() {
        return metodePengantaran;
    }

    public String getLokasiPengantaran() {
        return lokasiPengantaran;
    }
public void tambahMenu(Menu menu, int qty, String ukuran) {
        JumlahHargaRestoran jh = new JumlahHargaRestoran();
        jh.setQty(qty);
        jh.setUkuran(ukuran);
        jh.setHargaSatuan(menu.getHarga().longValue());
        jh.hitungSubtotal();
        
        String key = menu.getMenuId() + "_" + ukuran;
        pesanan.put(key, jh);
    }

    public Long hitungTotal() {
        long total = 0;
        for (JumlahHargaRestoran jh : pesanan.values()) {
            total += jh.getSubtotal();
        }
        return total;
    } 
    // METHOD @OVERRIDING DARI CLASS TRANSAKSI
    @Override
    public Long getTotalHargaFinal() {
        long subtotal = hitungTotal();
        return subtotal + 5000L; // ongkir
    }

    // interface
    @Override
    public String statusOrder() {
        return "Pesanan sedang dikirim via " + metodePengantaran + " ke " + lokasiPengantaran;
    }
}