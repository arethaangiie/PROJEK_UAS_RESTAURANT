package com.example.demo.entity;

import java.util.Locale;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.text.NumberFormat;


@Entity
public class Menu {
    @Id
    private String menuId;

    @NotNull
    @Size(min = 3, max = 15)
    private String namaMenu;

    @NotNull
    private Float harga;

    public Menu(){} //untuk JPA
    // constructor (untuk membuat dan menginisialisasi objek)
    public Menu(String id, String n, Float h) {
        this.menuId = id;
        this.namaMenu = n;
        this.harga = h;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    public String getMenuId() {
        return menuId;
    }
    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }
    public String getNamaMenu() {
        return namaMenu;
    }
    public void setHarga(Float harga) {
        this.harga = harga;
    }
    public Float getHarga() {
        return harga;
    }

    //agar menjadi rupiah
    public String getHargaRupiah() {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return rupiah.format(harga);
    }

}
