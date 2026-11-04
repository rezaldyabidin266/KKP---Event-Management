# Arsitektur skeleton

## 1. Frontend dan backend

```text
Browser React / Desktop React+Tauri (nanti)
               |
          HTTPS / JSON
               |
       Spring Boot REST API
          (satu aplikasi)
               |
      PostgreSQL di server
```

Backend berarsitektur **Modular Monolith (package by feature)**: semua fitur satu deployable JAR,
tetapi paket `modules/auth`, `modules/role`, `modules/event`, dan seterusnya terpisah.

## 2. Lapisan di modul

```text
React -> Controller -> Service -> Repository -> Database
                             |           |
                            DTO        Entity
                             \--- Mapper --/
```

- `controller`: endpoint HTTP, validasi request, HTTP status.
- `service`: orkestrasi business logic, aturan domain dan transaksi.
- `repository`: query database melalui Spring Data JPA atau query yang diperlukan.
- `entity`: JPA model tabel database (`javax.persistence.*` pada Spring Boot 2.7).
- `dto`: model request dan response, berbeda dari Entity.
- `mapper`: konversi DTO ke Entity atau sebaliknya.

Jangan expose Entity langsung ke API, jangan menaruh business logic di Controller,
dan jangan membiarkan modul lain mengakses Repository milik modul tertentu.

## 3. Komunikasi dan transaksi

Modul yang membutuhkan fitur modul lain memanggil Service antarmodul.
Untuk menulis beberapa tabel dalam satu operasi, buat service orkestrasi dengan `@Transactional`.

## 4. Keputusan RBAC

- `roles`: nama/kode role.
- `role_access`: `role_id`, `page_key`, `can_view`, `can_create`, `can_edit`, `can_delete`, `can_approve`.
- `users.role_id`: per user satu role utama pada desain awal.
- Tidak membuat tabel `pages`.
- Permission halaman tidak menggantikan pengecekan `event_assignments` untuk batas kewenangan pada event tertentu.
- Validasi otorisasi wajib dilakukan di backend Spring Security, bukan hanya sembunyikan menu frontend.

## 5. Batas skeleton

- Belum dibuat 34 tabel ERD awal + 2 tabel RBAC secara fisik.
- Belum ada Entity JPA / Repository nyata, login, token, ataupun Rule Access yang aktif.
- Health API merupakan contoh nyata Controller, Service, dan DTO agar layering dapat dilihat dan dijalankan.
- Endpoint lain baru dibuat setelah migrasi database dan keputusan akses disepakati.
- Tauri belum dipasang, karena frontend React digunakan bersama oleh versi web dan desktop online.
