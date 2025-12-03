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

public class DineIn extends Transaksi implements Serve {
    private String sitNo, pax;

    @ElementCollection
    @CollectionTable(name = "pesanan_menu_dinein",
        joinColumns = @JoinColumn(name = "transaksi_id"))
        @MapKeyColumn(name = "menu_id")
    private Map<String, JumlahHargaRestoran> pesanan = new HashMap<>();

    // constructor
    public DineIn(){}
    //quantity, ukuran, dan ObjMenu diammbil dari method tambahPesanan di parent class
    public DineIn(String s, String p) {
        super();
        setSitNo(s);
        setPax(p);
    }

    public void setSitNo(String sitNo) {
        this.sitNo = sitNo;
    }

    public String getSitNo() {
        return sitNo;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getPax() {
        return pax;

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
        return subtotal + 5000L; // biaya service
    }

    // interfaces
    @Override
    public String statusOrder() {
        return "Pesanan diantarkan ke meja " + sitNo;
    }

}