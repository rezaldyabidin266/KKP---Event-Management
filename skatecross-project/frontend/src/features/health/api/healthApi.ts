import { httpGet } from '@/shared/api/httpClient';
import type { HealthResponse } from '@/features/health/types/health.types';

export function getHealth(signal?: AbortSignal): Promise<HealthResponse> {
  return httpGet<HealthResponse>('/api/health', signal);
}
