package com.example.demo.entity;

import java.time.LocalTime;
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
// extends transaksi artinya takeaway mewarisi inheritance dari atribut dan
// method dari transaksi
public class Takeaway extends Transaksi implements Serve {
    private String packagingType;
    private LocalTime pickupTime;

    @ElementCollection
    @CollectionTable(name = "pesanan_menu_takeaway",
        joinColumns = @JoinColumn(name = "transaksi_id"))
        @MapKeyColumn(name = "menu_id")
    private Map<String, JumlahHargaRestoran> pesanan = new HashMap<>();

    // constructor
    public Takeaway() {
    }

    public Takeaway(LocalTime pick, String pack) {
        super();
        setPickupTime(pick);
        setPackagingType(pack);
    }

    // Setter getter
    public void setPickupTime(LocalTime pick) {
        this.pickupTime = pick;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPackagingType(String pack) {
        this.packagingType = pack;
    }

    public String getPackagingType() {
        return packagingType;
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
        return subtotal + 3000L; // biaya packaging
    }

    // interface
    @Override
    public String statusOrder() {
        return "Pesanan siap di ambil pukul " + pickupTime + " WIB ";
    }
}
