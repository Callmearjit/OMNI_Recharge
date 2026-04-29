import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, timeout } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, RegisterRequest, AuthUser } from '../models/auth.model';

/**
 * AuthService handles login, register, JWT token storage,
 * and session management across the application.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = environment.apiBaseUrl;

  /** Keys used in localStorage */
  private TOKEN_KEY = 'omni_token';
  private USER_KEY  = 'omni_user';

  constructor(private http: HttpClient, private router: Router) {}

  // ── Login ────────────────────────────────────────────────────────────────────
  /** POST /auth/login → returns raw JWT token string */
  login(payload: LoginRequest): Observable<string> {
    return this.http.post(`${this.base}/auth/login`, payload, { responseType: 'text' }).pipe(
      timeout(10000),
      tap((token: string) => {
        localStorage.setItem(this.TOKEN_KEY, token);
        // Decode JWT payload to extract username & role
        const decoded = this.decodeJwt(token);
        const user: AuthUser = {
          username: decoded?.sub ?? payload.username,
          role:     decoded?.role ?? 'USER',
          token
        };
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      })
    );
  }

  // ── Register ─────────────────────────────────────────────────────────────────
  /** POST /auth/register → returns success message string */
  register(payload: RegisterRequest): Observable<string> {
    return this.http.post(`${this.base}/auth/register`, payload, { responseType: 'text' }).pipe(
      timeout(10000)
    );
  }

  // ── Logout ───────────────────────────────────────────────────────────────────
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  // ── Token helpers ────────────────────────────────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    // Check token expiry
    const decoded = this.decodeJwt(token);
    if (!decoded?.exp) return true;
    return decoded.exp * 1000 > Date.now();
  }

  // ── Current user helpers ─────────────────────────────────────────────────────
  getCurrentUser(): AuthUser | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  getUsername(): string {
    return this.getCurrentUser()?.username ?? '';
  }

  getRole(): string {
    return this.getCurrentUser()?.role ?? 'USER';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  // ── JWT decoder ──────────────────────────────────────────────────────────────
  /** Decode JWT payload without a library — base64url → JSON */
  private decodeJwt(token: string): any {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }
}
