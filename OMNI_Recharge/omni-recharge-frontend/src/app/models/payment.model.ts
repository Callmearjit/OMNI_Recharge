// ─── Payment / Transaction Models ────────────────────────────────────────────

export type PaymentStatus = 'SUCCESS' | 'FAILED' | 'PENDING';

export interface Transaction {
  id: number;
  rechargeId: number;
  userId: string;
  amount: number;
  transactionRef: string;
  status: PaymentStatus;
  createdAt: string; // ISO datetime string
  idempotencyKey?: string;
}
