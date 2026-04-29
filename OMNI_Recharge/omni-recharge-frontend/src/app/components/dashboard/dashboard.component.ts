import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RechargeService } from '../../services/recharge.service';
import { PaymentService } from '../../services/payment.service';
import { OperatorService } from '../../services/operator.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { Recharge } from '../../models/recharge.model';
import { Transaction } from '../../models/payment.model';
import { Operator } from '../../models/operator.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, SpinnerComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  loading = true;
  username = '';
  role = '';
  isAdmin = false;

  recentRecharges: Recharge[] = [];
  recentTransactions: Transaction[] = [];
  operators: Operator[] = [];

  stats = { total: 0, success: 0, failed: 0, pending: 0 };

  constructor(
    private auth: AuthService,
    private rechargeSvc: RechargeService,
    private paymentSvc: PaymentService,
    private operatorSvc: OperatorService
  ) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername();
    this.role     = this.auth.getRole();
    this.isAdmin  = this.auth.isAdmin();

    forkJoin({
      recharges:    this.rechargeSvc.getMyHistory().pipe(catchError(() => of([]))),
      operators:    this.operatorSvc.getOperators().pipe(catchError(() => of([]))),
      transactions: this.isAdmin
        ? this.paymentSvc.getAllTransactions().pipe(catchError(() => of([])))
        : of([])
    }).subscribe(({ recharges, operators, transactions }) => {
      this.operators         = operators;
      this.recentRecharges   = recharges.slice(0, 5);
      this.recentTransactions = (transactions as Transaction[]).slice(0, 5);

      // Compute stats from all recharges
      this.stats.total   = recharges.length;
      this.stats.success = recharges.filter(r => r.status === 'SUCCESS').length;
      this.stats.failed  = recharges.filter(r => r.status === 'FAILED').length;
      this.stats.pending = recharges.filter(r => r.status === 'PENDING').length;

      this.loading = false;
    });
  }

  statusClass(status: string): string {
    return { SUCCESS: 'badge-success', FAILED: 'badge-failed', PENDING: 'badge-pending' }[status] ?? '';
  }

  operatorIcon(name: string): string {
    const icons: Record<string, string> = {
      jio: '📡', airtel: '🔴', vi: '💜', vodafone: '💜',
      bsnl: '🟠', idea: '💜'
    };
    return icons[name?.toLowerCase()] ?? '📶';
  }
}
