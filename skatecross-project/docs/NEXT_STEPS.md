# Tahap implementasi setelah skeleton

Prioritas menuju target Februari 2027:

1. Pastikan `mvn spring-boot:run` dan `npm run dev` berjalan.
2. Tambahkan konfigurasi PostgreSQL, Spring Data JPA dan Flyway; cek ulang ERD (34 tabel awal) + RBAC (2 tabel).
3. Implementasi `roles`, `role_access`, `users.role_id` dengan migration dan DTO.
4. Authentication dan proteksi API dengan Spring Security.
5. User Master dan akses halaman React.
6. Event + kategori, lalu penugasan panitia.
7. Pendaftaran PIC/atlet/keikutsertaan kategori.
8. Pembayaran, QR, check-in pembukaan.
9. Pra-race, eliminasi, verifikasi dua akun, Cup, laporan.
10. Build aplikasi web; integrasi Tauri desktop online di akhir.

Jangan menambah seluruh class Entity hanya agar folder terisi. Buatnya per modul
saat aturan bisnis dan schema tabel sudah final sehingga tidak banyak revisi.
