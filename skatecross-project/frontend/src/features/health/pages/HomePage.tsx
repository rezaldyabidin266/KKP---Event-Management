import { ApiStatus } from '@/features/health/components/ApiStatus';

export function HomePage() {
  return (
    <section className="home-content">
      <div className="home-heading">
        <p className="eyebrow">Backend + Frontend</p>
        <h1>Skate Cross Management</h1>
        <p className="intro">
          Skeleton React TypeScript dan Java Spring Boot untuk proyek KKP.
          Modul kompetisi disiapkan, belum diimplementasikan.
        </p>
      </div>

      <div className="status-panel">
        <h2>Status koneksi API</h2>
        <ApiStatus />
      </div>

      <div className="next-panel">
        <h2>Modul yang akan dikembangkan</h2>
        <p>Authentication, User, Role Access, Event, Registration, Payment, Check-in, Race dan Cup.</p>
      </div>
    </section>
  );
}
