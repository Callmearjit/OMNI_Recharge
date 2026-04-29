import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Transaction } from '../models/payment.model';

/**
 * PaymentService fetches transaction data from the payment-service.
 * Gateway route: /payments/** → payment-service (StripPrefix=1)
 * payment-service has @RequestMapping("/payments") so:
 *   /payments/payments/{id}    → /payments/{id}
 *   /payments/payments/all     → /payments/all
 */
@Injectable({ providedIn: 'root' })
export class PaymentService {
  private base = `${environment.apiBaseUrl}/payments/payments`;

  constructor(private http: HttpClient) {}

  /** GET /payments/payments/{id} — get a single transaction */
  getTransaction(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.base}/${id}`);
  }

  /** GET /payments/payments/recharge/{rechargeId} — all transactions for a recharge */
  getByRechargeId(rechargeId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/recharge/${rechargeId}`);
  }

  /** GET /payments/payments/all — ADMIN: all transactions */
  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/all`);
  }
}
