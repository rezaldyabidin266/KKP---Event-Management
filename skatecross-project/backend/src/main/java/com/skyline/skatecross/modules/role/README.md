# Modul Role / RBAC (placeholder)

Rancangan disepakati:
- `roles`: master role.
- `role_access`: permission per `page_key` (`can_view`, `can_create`, `can_edit`, `can_delete`, `can_approve`).
- `users.role_id`: satu role utama per pengguna.
- Tidak memakai tabel `pages`.
- `event_assignments` terpisah untuk kewenangan pada event / kategori.

Kelas `Role`, `RoleAccess`, Repository, Controller, Service, DTO, dan Mapper belum diimplementasikan.
Buat setelah migration database selesai agar atribut konsisten dengan ERD revisi.
