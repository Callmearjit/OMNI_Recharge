import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Recharge, RechargeRequest } from '../models/recharge.model';

/**
 * RechargeService handles initiating recharges and fetching history/status.
 * Gateway route: /recharges/** → recharge-service (StripPrefix=1)
 * recharge-service has @RequestMapping("/recharges") so:
 *   /recharges/recharges         → /recharges       (POST create)
 *   /recharges/recharges/history → /recharges/history
 *   /recharges/recharges/all     → /recharges/all
 */
@Injectable({ providedIn: 'root' })
export class RechargeService {
  private base = `${environment.apiBaseUrl}/recharges/recharges`;

  constructor(private http: HttpClient) {}

  /** POST /recharges/recharges — initiate a recharge */
  createRecharge(req: RechargeRequest): Observable<Recharge> {
    const body = {
      mobileNumber: req.mobileNumber,
      planId: req.planId,
      idempotencyKey: req.idempotencyKey ?? this.generateKey()
    };
    return this.http.post<Recharge>(this.base, body);
  }

  /** GET /recharges/recharges/history — logged-in user's recharge history */
  getMyHistory(): Observable<Recharge[]> {
    return this.http.get<Recharge[]>(`${this.base}/history`);
  }

  /** GET /recharges/recharges/{id}/status — track a specific recharge */
  getRechargeStatus(id: number): Observable<Recharge> {
    return this.http.get<Recharge>(`${this.base}/${id}/status`);
  }

  /** GET /recharges/recharges/all — ADMIN: all recharges */
  getAllRecharges(): Observable<Recharge[]> {
    return this.http.get<Recharge[]>(`${this.base}/all`);
  }

  /** Generate a unique idempotency key */
  private generateKey(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
}
