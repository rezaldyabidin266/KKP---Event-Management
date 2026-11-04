import { useEffect, useState } from 'react';
import { getHealth } from '@/features/health/api/healthApi';

type Status = 'loading' | 'connected' | 'disconnected';

export function ApiStatus() {
  const [status, setStatus] = useState<Status>('loading');
  const [application, setApplication] = useState<string>('');

  useEffect(() => {
    const controller = new AbortController();
    getHealth(controller.signal)
      .then((data) => {
        setApplication(data.application);
        setStatus(data.status === 'UP' ? 'connected' : 'disconnected');
      })
      .catch((error: unknown) => {
        if (error instanceof Error && error.name === 'AbortError') return;
        setStatus('disconnected');
      });
    return () => controller.abort();
  }, []);

  const message = {
    loading: 'Memeriksa koneksi Spring Boot...',
    connected: `Spring Boot terhubung (${application})`,
    disconnected: 'Spring Boot belum terhubung. Jalankan backend di port 8080.',
  }[status];

  return <p className={`api-status api-status--${status}`}>{message}</p>;
}
