// ─── Operator Models ──────────────────────────────────────────────────────────

export interface Operator {
  id: number;
  name: string; // e.g. Jio, Airtel, Vi, BSNL
}

export interface Plan {
  id: number;
  amount: number;
  validity: string;
  data?: string;
  description?: string;
  operator: Operator;
}
