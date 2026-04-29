// ─── Auth Models ─────────────────────────────────────────────────────────────

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  role?: string;
}

export interface AuthUser {
  username: string;
  role: string;
  token: string;
}
