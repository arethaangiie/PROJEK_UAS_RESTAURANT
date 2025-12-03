package com.example.demo.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Transaksi {
    // class ini tidak bisa dibuat objek langsung, hanya bisa diturunkan
    // (inheritance)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderNo;
    protected String ukuran;
    protected int quantity;

    // // total harga (sum subtotal), subclasses may add fee (service/ongkir/packaging)
    // @Column(name = "total_harga")
    // private Long totalHarga = 0L;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    protected Menu objMenu; // objek menu (class lain bernama menu)
   
    public Transaksi(){}
    public Transaksi(Menu menu, int quantity, String ukuran, int orderNo) {
        this.objMenu = menu;
        this.quantity = quantity;
        this.ukuran = ukuran;
        this.orderNo = orderNo;
    }

    // setter and getter
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getUkuran() {
        return ukuran;
    }


    public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

    /**
     * Simpan total ke field totalHarga (dipanggil sebelum simpan ke DB)
     */

    public int getQuantity() {
        return quantity;
    }

    public void setObjMenu(Menu obj) {
        this.objMenu = obj;
    }

    public Menu getObjMenu() {
        return objMenu;
    }

    // pakai method spt ibu dan arrlist dihapus
    /**
     * Tambah menu ke pesanan. key = menu.getMenuId()
     * Harga satuan disimpan dari Menu.getHarga() (Float -> long)
     * Ukuran dan qty disimpan ke JumlahHargaRestoran
     * Menghitung subtotal tiap item langsung (jh.hitungSubtotal()).
     */

    // Subclass biasanya override untuk tambahkan fee (service/ongkir/packaging)
    public abstract Long getTotalHargaFinal();
    
    // method overloading dari getter getOrderNo sbg penanda tiap order yang masuk
    // helper (opsional) untuk format orderNo
    public String getOrderNo(String note) {
        return (note + orderNo);
    }
}