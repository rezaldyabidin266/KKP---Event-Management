# Skyline Skate Cross - Skeleton Project (KKP)

Skeleton **Modular Monolith** untuk aplikasi Event Management Skate Cross Competition.

| Komponen | Teknologi |
|---|---|
| Backend | Java 8, Spring Boot 2.7.18, Maven |
| Frontend | React, TypeScript, Vite |
| Database target | PostgreSQL (belum dihubungkan pada tahap skeleton) |
| Desktop target | Tauri untuk Windows, memakai API server online (belum diinisialisasi) |

## Isi proyek

- `backend/`: satu aplikasi Spring Boot, dipisahkan berdasarkan fitur (`modules/*`).
- `frontend/`: satu aplikasi React yang kelak dipakai bersama oleh web dan Tauri.
- `docs/`: pedoman modul dan urutan implementasi.
- `docker-compose.yml`: layanan PostgreSQL development **opsional**, belum digunakan oleh endpoint Health.

## Jalankan backend (CMD Windows)

Prasyarat: JDK 8, Maven, internet untuk mengunduh dependency pertama kali.

```cmd
cd C:\Github\KKP\skatecross-project\backend
mvn spring-boot:run
```

Buka: http://localhost:8080/api/health

Respons contoh:

```json
{"status":"UP","application":"skatecross-api"}
```

## Jalankan frontend (CMD baru)

Prasyarat: Node.js 24 (atau versi kompatibel) dan npm.

```cmd
cd C:\Github\KKP\skatecross-project\frontend
npm install
npm run dev
```

Buka: http://localhost:5173

Frontend memanggil `/api/health` melalui proxy Vite menuju backend di `localhost:8080`.
Jika backend belum hidup, frontend tetap dapat dibuka tetapi menampilkan bahwa koneksi API belum tersedia.

## Perintah build

Backend:
```cmd
cd backend
mvn clean package
```

Frontend:
```cmd
cd frontend
npm run build
```

## Aturan arsitektur

1. Gunakan **package by feature** di `backend/modules` dan `frontend/src/features`.
2. Controller menerima request, Service menjalankan business logic, Repository mengakses database.
3. Entity merupakan mapping database, sedangkan DTO untuk request/response API. Mapper mengonversi keduanya.
4. Gunakan **constructor injection**, bukan `new Service()` di Controller.
5. Antar-modul berkomunikasi lewat Service, tidak mengakses Repository modul lain langsung.
6. Proses yang mengubah beberapa tabel dijalankan dalam satu transaksi (`@Transactional`) pada service koordinasi.
7. RBAC disepakati memakai **`roles` + `role_access`** dan `users.role_id`; **tanpa** tabel `pages`.
8. Struktur modul sudah disiapkan, tetapi **entity dan business logic ERD belum dibuat**. Ini sengaja agar tidak menebak aturan bisnis.

## Tahap berikutnya (bukan bagian dari skeleton)

- Hubungkan PostgreSQL, tambah JPA dan migrasi skema.
- Implementasikan Authentication, User Management, Role Master dan Role Access.
- Lanjut modul Event/Kategori, Pendaftaran, Payment, Check-in, Eliminasi dan Cup.
- Setelah web stabil, tambahkan Tauri (server Spring Boot tetap online; tidak dibundel ke desktop).

**Perhatian:** Spring Boot 2.7.x sudah di luar dukungan OSS. Jangan membuka deployment publik tanpa evaluasi keamanan dan rencana upgrade.

Dokumentasi referensi:
- https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/getting-started.html
- https://vite.dev/guide/
