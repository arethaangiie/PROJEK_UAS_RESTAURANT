package com.example.demo.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import com.example.demo.entity.*;
import com.example.demo.service.*;

@Controller
@RequestMapping("/transaksi")
@SessionAttributes({ "DineInAktif", "DeliveryAktif", "TakeAwayAktif" })
public class TransaksiController {

    @Autowired
    private DineInService dineInService;
    @Autowired
    private TakeAwayService takeAwayService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private MenuService menuService;

    // ================= HALAMAN PILIH TRANSAKSI =================
    @GetMapping("")
    public String transaksiPage() {
        return "transaksi.html";
    }

    // METODE UNTUK ORDER NO
    private int generateGlobalOrderNo() {
        int maxDineIn = dineInService.getAll().stream()
                .mapToInt(DineIn::getOrderNo)
                .max().orElse(0);

        int maxTake = takeAwayService.getAll().stream()
                .mapToInt(Takeaway::getOrderNo)
                .max().orElse(0);

        int maxDel = deliveryService.getAll().stream()
                .mapToInt(Delivery::getOrderNo)
                .max().orElse(0);

        return Math.max(maxDineIn, Math.max(maxTake, maxDel)) + 1;
    }

    // ========================= DINE IN =========================
    @GetMapping({ "/dinein", "/dinein/" })
    public String dineInPage(Model model) {
        DineIn aktif = null;

        if (model.containsAttribute("DineInAktif")) {
            aktif = (DineIn) model.getAttribute("DineInAktif");
        } else {
            aktif = new DineIn();
            aktif.setPesanan(new HashMap<>());
            model.addAttribute("DineInAktif", aktif);
        }
        model.addAttribute("menuList", menuService.getAllMenus());
        model.addAttribute("dineInList", dineInService.getAll());
        model.addAttribute("pesananAktif", aktif.getPesanan());
        model.addAttribute("menuById",
                menuService.getAllMenus().stream().collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        model.addAttribute("dineInInfo", new DineIn());
        model.addAttribute("editMode", false);

        return "dinein.html";
    }

    // read database
    @GetMapping("/dinein/{id}")
    public String dineInGetRec(Model model, @PathVariable long id) {

        model.addAttribute("menuList", menuService.getAllMenus());
        model.addAttribute("dineInList", dineInService.getAll());

        DineIn dataEdit = dineInService.getById(id);

        model.addAttribute("dineInInfo", dataEdit);
        model.addAttribute("editMode", true);

        model.addAttribute("menuById",
                menuService.getAllMenus().stream()
                        .collect(Collectors.toMap(Menu::getMenuId, m -> m)));

        return "dinein.html";
    }

    @PostMapping("/dinein/add-menu")
    public String tambahMenu(
            @ModelAttribute("DineInAktif") DineIn dineInInfo,
            @RequestParam String menuId,
            @RequestParam int qty,
            @RequestParam String ukuran) {
        Menu menu = menuService.getMenuById(menuId);
        dineInInfo.tambahMenu(menu, qty, ukuran);

        return "redirect:/transaksi/dinein";
    }

    @PostMapping("/dinein/checkout")
    public String checkout(@ModelAttribute("DineInAktif") DineIn dineInInfo, SessionStatus status) {
        DineIn newDineIn = new DineIn();
        newDineIn.setSitNo(dineInInfo.getSitNo());
        newDineIn.setPax(dineInInfo.getPax());
        newDineIn.setPesanan(new HashMap<>(dineInInfo.getPesanan()));

        newDineIn.setOrderNo(generateGlobalOrderNo());

        DineIn saved = dineInService.add(newDineIn);
        return "redirect:/transaksi/dinein/konfirm";
    }

    @GetMapping("/dinein/konfirm")
    public String halamanKonfirmasi(@ModelAttribute("DineInAktif") DineIn aktif, Model model) {

        Map<String, String> namaMenu = new HashMap<>();
        for (String key : aktif.getPesanan().keySet()) {
            String menuId = key.split("_")[0];
            Menu menu = menuService.getMenuById(menuId);
            namaMenu.put(key, menu != null ? menu.getNamaMenu() : "Menu Tidak Ada");
        }
        model.addAttribute("transaksi", aktif.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", aktif);

        return "konfirm_dinein.html";
    }

    @GetMapping("/dinein/konfirm/edit")
    public String editDetailPengantaran(
            @ModelAttribute("DineInAktif") DineIn aktif,
            Model model) {
        model.addAttribute("sit", aktif.getSitNo());
        model.addAttribute("pax", aktif.getPax());
        return "dinein_edit";
    }

    @PostMapping("/dinein/konfirm/edit/save")
    public String saveDetailPengantaran(
            @ModelAttribute("DineInAktif") DineIn aktif,
            @RequestParam String sitNo,
            @RequestParam String pax) {
        aktif.setSitNo(sitNo);
        aktif.setPax(pax);

        return "redirect:/transaksi/dinein/konfirm";
    }

    // CREATE → INSERT DATA ke database
    @PostMapping("/dinein/konfirm/save")
    public String finalSave(
            @ModelAttribute("DineInAktif") DineIn aktif,
            SessionStatus status) {

        DineIn newDineIn = new DineIn();
        newDineIn.setSitNo(aktif.getSitNo());
        newDineIn.setPax(aktif.getPax());
        newDineIn.setPesanan(new HashMap<>(aktif.getPesanan()));
        newDineIn.setOrderNo(generateGlobalOrderNo());

        DineIn saved = dineInService.add(newDineIn);

        status.setComplete(); // hapus session setelah final save

        return "redirect:/transaksi/sukses/dinein/" + saved.getId();
    }

    @GetMapping("/dinein/konfirm/delete/{key}")
    public String hapusPesananKonfirm(
            @ModelAttribute("DineInAktif") DineIn aktif,
            @PathVariable String key) {
        aktif.getPesanan().remove(key);
        return "redirect:/transaksi/dinein/konfirm";
    }

    // Menampilkan detail transaksi tersimpan
    @GetMapping("/sukses/dinein/{id}")
    public String suksesDinein(@PathVariable long id, Model model) {

        DineIn d = dineInService.getById(id);
        Map<String, JumlahHargaRestoran> pesanan = d.getPesanan();
        Map<String, String> namaMenu = new HashMap<>();
        for (String key : pesanan.keySet()) {
            String menuId = key.split("_")[0]; // mengambil ID menu sebelum underscore
            Menu menu = menuService.getMenuById(menuId);
            if (menu != null) {
                namaMenu.put(key, menu.getNamaMenu());
            } else {
                namaMenu.put(key, "Menu Tidak Ditemukan");
            }
        }
        model.addAttribute("transaksi", d.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", d);

        return "sukses_dine.html";
    }

    // UPDATE → EDIT DATA di database
    @PostMapping(value = "/dinein/submit/{id}")
    public String dineInEdit(@ModelAttribute("dineInInfo") DineIn dataBaru,
            @PathVariable long id) {
        DineIn asli = dineInService.getById(id);
        asli.setSitNo(dataBaru.getSitNo());
        asli.setPax(dataBaru.getPax());
        dineInService.update(id, asli);
        return "redirect:/transaksi/dinein-list";
    }

        // DELETE → HAPUS DATA di database
    @PostMapping("/dinein/delete/{id}")
    public String dineInDelete(@PathVariable long id) {
        dineInService.delete(id);
        return "redirect:/transaksi/dinein-list";
    }

    // ========================= TAKEAWAY =========================
    @GetMapping({ "/takeaway", "/takeaway/" })
    public String takeawayPage(Model model) {
        Takeaway aktif = null;

        if (model.containsAttribute("TakeAwayAktif")) {
            aktif = (Takeaway) model.getAttribute("TakeAwayAktif");
        } else {
            aktif = new Takeaway(); // objek delivery kosong untuk pesanan aktif
            aktif.setPesanan(new HashMap<>());
            model.addAttribute("TakeAwayAktif", aktif);
        }

        model.addAttribute("menuList", menuService.getAllMenus());
        model.addAttribute("takeAwayList", takeAwayService.getAll());
        model.addAttribute("pesananAktif", aktif.getPesanan());
        model.addAttribute("menuById",
                menuService.getAllMenus().stream()
                        .collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        model.addAttribute("takeAwayInfo", new Takeaway());
        model.addAttribute("editMode", false);

        return "takeaway.html";
    }

    //Ambil data dari DB untuk diedit
    @GetMapping("/takeaway/{id}")
    public String takeawayGetRec(Model model, @PathVariable long id) {

        model.addAttribute("menuList", menuService.getAllMenus());
        model.addAttribute("takeAwayList", takeAwayService.getAll());

        Takeaway dataEdit = takeAwayService.getById(id);

        model.addAttribute("takeAwayInfo", dataEdit);
        model.addAttribute("editMode", true);

        model.addAttribute("menuById",
                menuService.getAllMenus().stream()
                        .collect(Collectors.toMap(Menu::getMenuId, m -> m)));

        return "takeaway.html";
    }

    @PostMapping("/takeaway/add-menu")
    public String tambahMenu(
            @ModelAttribute("TakeAwayAktif") Takeaway takeInfo,
            @RequestParam String menuId,
            @RequestParam int qty,
            @RequestParam String ukuran) {
        Menu menu = menuService.getMenuById(menuId);
        takeInfo.tambahMenu(menu, qty, ukuran);

        return "redirect:/transaksi/takeaway";
    }

    @PostMapping("/takeaway/checkout")
    public String checkout(@ModelAttribute("TakeAwayAktif") Takeaway takeInfo, SessionStatus status) {
        Takeaway newTakeaway = new Takeaway();
        newTakeaway.setPickupTime(takeInfo.getPickupTime());
        newTakeaway.setPackagingType(takeInfo.getPackagingType());
        newTakeaway.setPesanan(new HashMap<>(takeInfo.getPesanan()));

        newTakeaway.setOrderNo(generateGlobalOrderNo());

        Takeaway saved = takeAwayService.add(newTakeaway);
        return "redirect:/transaksi/takeaway/konfirm";
    }

    @GetMapping("/takeaway/konfirm")
    public String halamanKonfirmasi(@ModelAttribute("TakeAwayAktif") Takeaway aktif, Model model) {

        Map<String, String> namaMenu = new HashMap<>();
        for (String key : aktif.getPesanan().keySet()) {
            String menuId = key.split("_")[0];
            Menu menu = menuService.getMenuById(menuId);
            namaMenu.put(key, menu != null ? menu.getNamaMenu() : "Menu Tidak Ada");
        }

        model.addAttribute("transaksi", aktif.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", aktif);

        return "konfirm_takeaway.html";
    }

    @GetMapping("/takeaway/konfirm/edit")
    public String editDetailPengantaran(
            @ModelAttribute("TakeAwayAktif") Takeaway aktif,
            Model model) {
        model.addAttribute("pick",
                aktif.getPickupTime() == null ? "00:00" : aktif.getPickupTime().toString());
        model.addAttribute("pack", aktif.getPackagingType());
        return "takeaway_edit";
    }

    @PostMapping("/takeaway/konfirm/edit/save")
    public String saveDetailPengantaran(
            @ModelAttribute("TakeAwayAktif") Takeaway aktif,
            @RequestParam LocalTime pick,
            @RequestParam String pack) {
        aktif.setPickupTime(pick);
        aktif.setPackagingType(pack);

        return "redirect:/transaksi/takeaway/konfirm";
    }

    // menyimpan pesanan ke database & clear session
    @PostMapping("/takeaway/konfirm/save")
    public String finalSave(
            @ModelAttribute("TakeAwayAktif") Takeaway aktif,
            SessionStatus status) {

        Takeaway newTakeaway = new Takeaway();
        newTakeaway.setPickupTime(aktif.getPickupTime());
        newTakeaway.setPackagingType(aktif.getPackagingType());
        newTakeaway.setPesanan(new HashMap<>(aktif.getPesanan()));
        newTakeaway.setOrderNo(generateGlobalOrderNo());

        Takeaway saved = takeAwayService.add(newTakeaway);

        status.setComplete(); // hapus session setelah final save

        return "redirect:/transaksi/sukses/takeaway/" + saved.getId();
    }

    @GetMapping("/takeaway/konfirm/delete/{key}")
    public String hapusPesananKonfirm(
            @ModelAttribute("TakeAwayAktif") Takeaway aktif,
            @PathVariable String key) {
        aktif.getPesanan().remove(key);
        return "redirect:/transaksi/takeaway/konfirm";
    }

    //Menampilkan data yang sudah tersimpan dari DB
    @GetMapping("/sukses/takeaway/{id}")
    public String suksesTakeaway(@PathVariable long id, Model model) {

        Takeaway t = takeAwayService.getById(id); // ambil data takeaway
        Map<String, JumlahHargaRestoran> pesanan = t.getPesanan(); // pesanan dari session/entity

        Map<String, String> namaMenu = new HashMap<>();

        for (String key : pesanan.keySet()) {
            String menuId = key.split("_")[0]; // mengambil ID menu sebelum underscore
            Menu menu = menuService.getMenuById(menuId);
            if (menu != null) {
                namaMenu.put(key, menu.getNamaMenu());
            } else {
                namaMenu.put(key, "Menu Tidak Ditemukan");
            }
        }

        model.addAttribute("transaksi", t.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", t);

        return "sukses.html";
    }

    //Update takeaway dari database
    @PostMapping(value = "/takeaway/submit/{id}")
    public String takeawayEdit(@ModelAttribute("takeAwayInfo") Takeaway dataBaru,
            @PathVariable long id) {
        Takeaway asli = takeAwayService.getById(id);
        asli.setPickupTime(dataBaru.getPickupTime()); // FIX
        asli.setPackagingType(dataBaru.getPackagingType()); // FIX
        takeAwayService.update(id, asli);
        return "redirect:/transaksi/takeaway-list";
    }

    //apus takeaway dari database
    @PostMapping("/takeaway/delete/{id}")
    public String takeawayDelete(@PathVariable long id) {
        takeAwayService.delete(id);
        return "redirect:/transaksi/takeaway-list";
    }

    // ========================= DELIVERY =========================
    @GetMapping({ "/delivery", "/delivery/" })
    public String deliveryPage(Model model) {

        // Ambil DeliveryAktif dari model kalau ada, kalau belum buat baru
        Delivery aktif = null;
        if (model.containsAttribute("DeliveryAktif")) {
            aktif = (Delivery) model.getAttribute("DeliveryAktif");
        } else {
            aktif = new Delivery(); // objek delivery kosong untuk pesanan aktif
            aktif.setPesanan(new HashMap<>());
            model.addAttribute("DeliveryAktif", aktif);
        }

        // Daftar menu dari database
        model.addAttribute("menuList", menuService.getAllMenus());
        // Delivery yang sudah tersimpan di DB
        model.addAttribute("deliveryList", deliveryService.getAll());
        model.addAttribute("pesananAktif", aktif.getPesanan());
        model.addAttribute("menuById",
                menuService.getAllMenus().stream()
                        .collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        model.addAttribute("deliveryInfo", new Delivery());
        model.addAttribute("editMode", false);

        return "delivery.html";
    }

    //Load delivery data dari database untuk edit
    @GetMapping("/delivery/{id}")
    public String deliveryGetRec(Model model, @PathVariable long id) {

        model.addAttribute("menuList", menuService.getAllMenus());
        model.addAttribute("deliveryList", deliveryService.getAll());

        Delivery dataEdit = deliveryService.getById(id);

        model.addAttribute("deliveryInfo", dataEdit);
        model.addAttribute("editMode", true);

        model.addAttribute("menuById",
                menuService.getAllMenus().stream()
                        .collect(Collectors.toMap(Menu::getMenuId, m -> m)));

        return "delivery.html";
    }

    @PostMapping("/delivery/add-menu")
    public String tambahMenu(
            @ModelAttribute("DeliveryAktif") Delivery DelInfo,
            @RequestParam String menuId,
            @RequestParam int qty,
            @RequestParam String ukuran) {
        Menu menu = menuService.getMenuById(menuId);
        DelInfo.tambahMenu(menu, qty, ukuran);

        return "redirect:/transaksi/delivery";
    }

    @PostMapping("/delivery/checkout")
    public String gotokonfirmasi(@ModelAttribute("DeliveryAktif") Delivery DelInfo, Model model, SessionStatus status) {
        Delivery newDelivery = new Delivery();
        newDelivery.setMetodePengantaran(DelInfo.getMetodePengantaran());
        newDelivery.setLokasiPengantaran(DelInfo.getLokasiPengantaran());
        newDelivery.setPesanan(new HashMap<>(DelInfo.getPesanan()));

        newDelivery.setOrderNo(generateGlobalOrderNo());

        Delivery saved = deliveryService.add(newDelivery);
        return "redirect:/transaksi/delivery/konfirm";
    }

    @GetMapping("/delivery/konfirm")
    public String halamanKonfirmasi(@ModelAttribute("DeliveryAktif") Delivery aktif, Model model) {

        Map<String, String> namaMenu = new HashMap<>();
        for (String key : aktif.getPesanan().keySet()) {
            String menuId = key.split("_")[0];
            Menu menu = menuService.getMenuById(menuId);
            namaMenu.put(key, menu != null ? menu.getNamaMenu() : "Menu Tidak Ada");
        }

        model.addAttribute("transaksi", aktif.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", aktif);

        return "konfirm_delivery.html";
    }

    @GetMapping("/delivery/konfirm/edit")
    public String editDetailPengantaran(
            @ModelAttribute("DeliveryAktif") Delivery aktif,
            Model model) {
        model.addAttribute("metode", aktif.getMetodePengantaran());
        model.addAttribute("lokasi", aktif.getLokasiPengantaran());
        return "delivery_edit";
    }

    @PostMapping("/delivery/konfirm/edit/save")
    public String saveDetailPengantaran(
            @ModelAttribute("DeliveryAktif") Delivery aktif,
            @RequestParam String metode,
            @RequestParam String lokasi) {
        aktif.setMetodePengantaran(metode);
        aktif.setLokasiPengantaran(lokasi);

        return "redirect:/transaksi/delivery/konfirm";
    }

    //menyimpan ke database dan hapus session
    @PostMapping("/delivery/konfirm/save")
    public String finalSave(
            @ModelAttribute("DeliveryAktif") Delivery aktif,
            SessionStatus status) {

        Delivery newDelivery = new Delivery();
        newDelivery.setMetodePengantaran(aktif.getMetodePengantaran());
        newDelivery.setLokasiPengantaran(aktif.getLokasiPengantaran());
        newDelivery.setPesanan(new HashMap<>(aktif.getPesanan()));
        newDelivery.setOrderNo(generateGlobalOrderNo());

        Delivery saved = deliveryService.add(newDelivery);

        status.setComplete(); // hapus session setelah final save

        return "redirect:/transaksi/sukses/delivery/" + saved.getId();
    }

    @GetMapping("/delivery/konfirm/delete/{key}")
    public String hapusPesananKonfirm(
            @ModelAttribute("DeliveryAktif") Delivery aktif,
            @PathVariable String key) {
        aktif.getPesanan().remove(key);
        return "redirect:/transaksi/delivery/konfirm";
    }

    //Menampilkan delivery yang sudah tersimpan
    @GetMapping("/sukses/delivery/{id}")
    public String suksesPage(@PathVariable long id, Model model) {

        Delivery dl = deliveryService.getById(id);
        Map<String, JumlahHargaRestoran> pesanan = dl.getPesanan();
        Map<String, String> namaMenu = new HashMap<>();
        for (String key : pesanan.keySet()) {
            String menuId = key.split("_")[0]; // mengambil ID menu sebelum underscore
            Menu menu = menuService.getMenuById(menuId);
            if (menu != null) {
                namaMenu.put(key, menu.getNamaMenu());
            } else {
                namaMenu.put(key, "Menu Tidak Ditemukan");
            }
        }

        model.addAttribute("transaksi", dl.getPesanan());
        model.addAttribute("namaMenu", namaMenu);
        model.addAttribute("take", dl);

        return "sukses_del.html";
    }

    //Update delivery yang sudah tersimpan
    @PostMapping(value = "/delivery/submit/{id}")
    public String deliveryEdit(@ModelAttribute("deliveryInfo") Delivery dataBaru,
            @PathVariable long id) {
        Delivery asli = deliveryService.getById(id);
        asli.setMetodePengantaran(dataBaru.getMetodePengantaran()); // FIX
        asli.setLokasiPengantaran(dataBaru.getLokasiPengantaran()); // FIX
        deliveryService.update(id, asli);
        return "redirect:/transaksi/delivery-list";
    }

    //Hapus delivery dari database
    @PostMapping("/delivery/delete/{id}")
    public String deliveryDelete(@PathVariable long id) {
        deliveryService.delete(id);
        return "redirect:/transaksi/delivery-list";
    }

    // DAFTAR TRANSAKSI
    @GetMapping("/daftar")
    public String daftarTransaksi(Model model) {

        // Ambil semua transaksi
        model.addAttribute("deliveryList", deliveryService.getAll());
        model.addAttribute("takeAwayList", takeAwayService.getAll());
        model.addAttribute("dineInList", dineInService.getAll());

        // Map menuId -> Menu untuk lookup nama menu
        Map<String, Menu> menuById = menuService.getAllMenus().stream()
                .collect(Collectors.toMap(Menu::getMenuId, m -> m));
        model.addAttribute("menuById", menuById);

        return "daftartransaksi.html";
    }

    // ========================= HALAMAN LIST SEMUA JENIS DAFTAR TRANSAKSI =========================
    // Halaman daftar delivery
    @GetMapping("/delivery-list")
    public String deliveryListPage(Model model) {
        model.addAttribute("deliveryList", deliveryService.getAll());
        model.addAttribute("menuById", menuService.getAllMenus()
                .stream().collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        return "deliveryList.html";
    }

    // Halaman daftar dine-in
    @GetMapping("/dinein-list")
    public String dineInListPage(Model model) {
        model.addAttribute("dineInList", dineInService.getAll());
        model.addAttribute("menuById", menuService.getAllMenus()
                .stream().collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        return "dineinList.html";
    }

    // Halaman daftar takeaway
    @GetMapping("/takeaway-list")
    public String takeawayListPage(Model model) {
        model.addAttribute("takeAwayList", takeAwayService.getAll());
        model.addAttribute("menuById", menuService.getAllMenus()
                .stream().collect(Collectors.toMap(Menu::getMenuId, m -> m)));
        return "takeawayList.html";
    }

}
