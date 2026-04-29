import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { OperatorService } from '../../services/operator.service';
import { RechargeService } from '../../services/recharge.service';
import { PaymentService } from '../../services/payment.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ToastService } from '../../services/toast.service';
import { UserResponse } from '../../models/user.model';
import { Operator, Plan } from '../../models/operator.model';
import { Recharge } from '../../models/recharge.model';
import { Transaction } from '../../models/payment.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

type AdminTab = 'users' | 'operators' | 'recharges' | 'transactions';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, NavbarComponent, SpinnerComponent],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  loading    = true;
  activeTab: AdminTab = 'users';

  users:        UserResponse[] = [];
  operators:    Operator[]     = [];
  allPlans:     Plan[]         = [];
  recharges:    Recharge[]     = [];
  transactions: Transaction[]  = [];

  // Plan management
  showPlanForm   = false;
  editingPlan:   Plan | null   = null;
  planForm:      FormGroup;
  planLoading    = false;

  constructor(
    private fb: FormBuilder,
    private userSvc: UserService,
    private operatorSvc: OperatorService,
    private rechargeSvc: RechargeService,
    private paymentSvc: PaymentService,
    private toast: ToastService
  ) {
    this.planForm = this.fb.group({
      amount:     ['', [Validators.required, Validators.min(1)]],
      validity:   ['', Validators.required],
      operatorId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    forkJoin({
      users:        this.userSvc.getAllUsers().pipe(catchError(() => of([]))),
      operators:    this.operatorSvc.getOperators().pipe(catchError(() => of([]))),
      recharges:    this.rechargeSvc.getAllRecharges().pipe(catchError(() => of([]))),
      transactions: this.paymentSvc.getAllTransactions().pipe(catchError(() => of([])))
    }).subscribe(({ users, operators, recharges, transactions }) => {
      this.users        = users as UserResponse[];
      this.operators    = operators as Operator[];
      this.recharges    = recharges as Recharge[];
      this.transactions = transactions as Transaction[];
      // Load plans for all operators
      this.loadAllPlans();
      this.loading = false;
    });
  }

  loadAllPlans(): void {
    if (!this.operators.length) return;
    const planCalls = this.operators.map(op =>
      this.operatorSvc.getPlansByOperator(op.id).pipe(catchError(() => of([])))
    );
    forkJoin(planCalls).subscribe(results => {
      this.allPlans = (results as Plan[][]).flat();
    });
  }

  setTab(tab: AdminTab): void { this.activeTab = tab; }

  // ── Plan CRUD ────────────────────────────────────────────────────────────────
  openAddPlan(): void { this.editingPlan = null; this.planForm.reset(); this.showPlanForm = true; }

  openEditPlan(plan: Plan): void {
    this.editingPlan = plan;
    this.planForm.patchValue({
      amount:     plan.amount,
      validity:   plan.validity,
      operatorId: plan.operator?.id
    });
    this.showPlanForm = true;
  }

  savePlan(): void {
    if (this.planForm.invalid) { this.planForm.markAllAsTouched(); return; }
    this.planLoading = true;
    const val = this.planForm.value;
    const payload: Partial<Plan> = {
      amount:   val.amount,
      validity: val.validity,
      operator: { id: val.operatorId, name: '' }
    };

    const action$ = this.editingPlan
      ? this.operatorSvc.updatePlan(this.editingPlan.id, payload)
      : this.operatorSvc.addPlan(payload);

    action$.subscribe({
      next: () => {
        this.planLoading  = false;
        this.showPlanForm = false;
        this.toast.success(this.editingPlan ? 'Plan updated!' : 'Plan added!');
        this.loadAllPlans();
      },
      error: () => { this.planLoading = false; this.toast.error('Failed to save plan.'); }
    });
  }

  deletePlan(plan: Plan): void {
    if (!confirm(`Delete plan ₹${plan.amount}?`)) return;
    this.operatorSvc.deletePlan(plan.id).subscribe({
      next: () => { this.toast.success('Plan deleted!'); this.loadAllPlans(); },
      error: () => this.toast.error('Failed to delete plan.')
    });
  }

  closePlanForm(): void { this.showPlanForm = false; }

  // ── Helpers ──────────────────────────────────────────────────────────────────
  statusClass(s: string): string {
    return { SUCCESS:'badge-success', FAILED:'badge-failed', PENDING:'badge-pending' }[s] ?? '';
  }
  get pf() { return this.planForm.controls; }
}
