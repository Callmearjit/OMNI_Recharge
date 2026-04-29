// ─── Recharge Models ──────────────────────────────────────────────────────────

export interface RechargeRequest {
  mobileNumber: string;
  planId: number;
  idempotencyKey?: string;
}

export interface Recharge {
  id: number;
  mobileNumber: string;
  userId: string;
  planId: number;
  status: 'PENDING' | 'SUCCESS' | 'FAILED';
  idempotencyKey?: string;
}
