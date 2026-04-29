import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OperatorService } from '../../services/operator.service';
import { RechargeService } from '../../services/recharge.service';
import { ToastService } from '../../services/toast.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { Operator, Plan } from '../../models/operator.model';
import { Recharge } from '../../models/recharge.model';

@Component({
  selector: 'app-recharge',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, NavbarComponent, SpinnerComponent],
  templateUrl: './recharge.component.html',
  styleUrls: ['./recharge.component.css']
})
export class RechargeComponent implements OnInit {
  // Step control: 1=mobile, 2=operator, 3=plan, 4=confirm, 5=result
  step = 1;
  loading = false;
  loadingPlans = false;

  operators: Operator[] = [];
  plans: Plan[] = [];
  selectedOperator: Operator | null = null;
  selectedPlan: Plan | null = null;
  result: Recharge | null = null;
  activeTab = 'Recommended';
  tabs = ['Recommended', 'Data', 'Unlimited', 'Talktime'];

  mobileForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private operatorSvc: OperatorService,
    private rechargeSvc: RechargeService,
    private toast: ToastService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.mobileForm = this.fb.group({
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]]
    });
  }

  ngOnInit(): void {
    this.loading = true;
    this.operatorSvc.getOperators().subscribe({
      next: ops => { this.operators = ops; this.loading = false; },
      error: () => { this.toast.error('Failed to load operators.'); this.loading = false; }
    });
  }

  get mobileCtrl() { return this.mobileForm.controls['mobileNumber']; }

  // Step 1 → 2
  goToOperators(): void {
    if (this.mobileForm.invalid) { this.mobileForm.markAllAsTouched(); return; }
    this.step = 2;
  }

  // Step 2 → 3
  selectOperator(op: Operator): void {
    this.selectedOperator = op;
    this.loadingPlans = true;
    this.operatorSvc.getPlansByOperator(op.id).subscribe({
      next: plans => { this.plans = plans; this.loadingPlans = false; this.step = 3; },
      error: () => { this.toast.error('Failed to load plans.'); this.loadingPlans = false; }
    });
  }

  // Step 3 → 4
  selectPlan(plan: Plan): void {
    this.selectedPlan = plan;
    this.step = 4;
  }

  // Step 4 → submit → 5
  confirmRecharge(): void {
    if (!this.selectedPlan || !this.selectedOperator) return;
    this.loading = true;
    this.rechargeSvc.createRecharge({
      mobileNumber: this.mobileForm.value.mobileNumber,
      planId: this.selectedPlan.id
    }).subscribe({
      next: (res) => {
        this.result = res;
        this.loading = false;
        this.step = 5;
      },
      error: () => {
        this.loading = false;
        this.toast.error('Recharge failed. Please try again.');
        this.step = 5;
        this.result = { id: 0, mobileNumber: '', userId: '', planId: 0, status: 'FAILED' };
      }
    });
  }

  goHome(): void { this.router.navigate(['/dashboard']); }
  retry(): void   { this.step = 1; this.result = null; this.selectedPlan = null; this.selectedOperator = null; }

  filteredPlans(): Plan[] {
    if (this.activeTab === 'Recommended') return this.plans;
    if (this.activeTab === 'Data')       return this.plans.filter(p => p.amount >= 200 && p.amount <= 500);
    if (this.activeTab === 'Unlimited')  return this.plans.filter(p => p.amount > 500);
    if (this.activeTab === 'Talktime')   return this.plans.filter(p => p.amount < 200);
    return this.plans;
  }

  operatorIcon(name: string): string {
    const m: Record<string,string> = { jio:'📡', airtel:'🔴', vi:'💜', bsnl:'🟠' };
    return m[name?.toLowerCase()] ?? '📶';
  }
}
