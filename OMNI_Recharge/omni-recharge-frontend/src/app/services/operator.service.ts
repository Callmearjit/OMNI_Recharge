import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Operator, Plan } from '../models/operator.model';

/**
 * OperatorService provides access to operators and plans.
 * Gateway route: /operators/** → operator-service (StripPrefix=1)
 * operator-service has @RequestMapping("/operators") so:
 *   /operators/operators         → /operators       (list all)
 *   /operators/operators/{id}/plans → /operators/{id}/plans
 *   /operators/operators/plans   → /operators/plans  (CRUD)
 */
@Injectable({ providedIn: 'root' })
export class OperatorService {
  private base = `${environment.apiBaseUrl}/operators/operators`;

  constructor(private http: HttpClient) {}

  // ── Public ──────────────────────────────────────────────────────────────────

  /** GET /operators/operators — list all operators */
  getOperators(): Observable<Operator[]> {
    return this.http.get<Operator[]>(this.base);
  }

  /** GET /operators/operators/{id}/plans — list plans for an operator */
  getPlansByOperator(operatorId: number): Observable<Plan[]> {
    return this.http.get<Plan[]>(`${this.base}/${operatorId}/plans`);
  }

  /** GET /operators/operators/plans/{planId} — single plan detail */
  getPlanById(planId: number): Observable<Plan> {
    return this.http.get<Plan>(`${this.base}/plans/${planId}`);
  }

  // ── Admin ────────────────────────────────────────────────────────────────────

  /** POST /operators/operators/plans — ADMIN: add a new plan */
  addPlan(plan: Partial<Plan>): Observable<string> {
    return this.http.post(`${this.base}/plans`, plan, { responseType: 'text' });
  }

  /** PUT /operators/operators/plans/{planId} — ADMIN: update a plan */
  updatePlan(planId: number, plan: Partial<Plan>): Observable<string> {
    return this.http.put(`${this.base}/plans/${planId}`, plan, { responseType: 'text' });
  }

  /** DELETE /operators/operators/plans/{planId} — ADMIN: delete a plan */
  deletePlan(planId: number): Observable<string> {
    return this.http.delete(`${this.base}/plans/${planId}`, { responseType: 'text' });
  }
}
