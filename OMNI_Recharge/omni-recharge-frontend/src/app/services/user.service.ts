import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserResponse } from '../models/user.model';
import { AuthService } from './auth.service';

/**
 * UserService talks to the user-service via the API Gateway.
 * Gateway route: /users/** → user-service (StripPrefix=1)
 * user-service has @RequestMapping("/") so:
 *   /users/profile → /profile on user-service ✅
 *   /users/all     → /all     on user-service ✅
 */
@Injectable({ providedIn: 'root' })
export class UserService {
  private base = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  /** GET /users/profile — returns logged-in user profile */
  getProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/profile`);
  }

  /** GET /users/all — ADMIN only: returns list of all users */
  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.base}/all`);
  }
}
