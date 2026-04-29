import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OperatorService } from '../../services/operator.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ToastService } from '../../services/toast.service';
import { Operator, Plan } from '../../models/operator.model';

/**
 * OperatorsComponent — displays all operators and their plans.
 * Clicking an operator expands its plan list.
 */
@Component({
  selector: 'app-operators',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, SpinnerComponent],
  templateUrl: './operators.component.html',
  styleUrls: ['./operators.component.css']
})
export class OperatorsComponent implements OnInit {
  loading = true;
  operators: Operator[] = [];
  planMap: Record<number, Plan[]>   = {};
  loadingMap: Record<number, boolean> = {};
  expandedId: number | null = null;

  constructor(
    private operatorSvc: OperatorService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.operatorSvc.getOperators().subscribe({
      next: ops => { this.operators = ops; this.loading = false; },
      error: () => { this.toast.error('Failed to load operators.'); this.loading = false; }
    });
  }

  toggleOperator(op: Operator): void {
    if (this.expandedId === op.id) {
      this.expandedId = null;
      return;
    }
    this.expandedId = op.id;
    // Load plans only if not already loaded
    if (!this.planMap[op.id]) {
      this.loadingMap[op.id] = true;
      this.operatorSvc.getPlansByOperator(op.id).subscribe({
        next: plans => {
          this.planMap[op.id]    = plans;
          this.loadingMap[op.id] = false;
        },
        error: () => {
          this.toast.error('Failed to load plans.');
          this.loadingMap[op.id] = false;
        }
      });
    }
  }

  selectPlan(plan: Plan): void {
    // Navigate to recharge with this plan pre-selected (future enhancement)
    this.toast.info(`Plan ₹${plan.amount} selected — go to Recharge to proceed.`);
  }

  operatorIcon(name: string): string {
    const m: Record<string,string> = { jio:'📡', airtel:'🔴', vi:'💜', vodafone:'💜', bsnl:'🟠', idea:'💜' };
    return m[name?.toLowerCase()] ?? '📶';
  }

  operatorColor(name: string): string {
    const m: Record<string,string> = {
      jio: 'linear-gradient(135deg,#2563eb,#1d4ed8)',
      airtel: 'linear-gradient(135deg,#dc2626,#b91c1c)',
      vi: 'linear-gradient(135deg,#7c3aed,#6d28d9)',
      bsnl: 'linear-gradient(135deg,#d97706,#b45309)'
    };
    return m[name?.toLowerCase()] ?? 'linear-gradient(135deg,#6366f1,#4f46e5)';
  }
}
