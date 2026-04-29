import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { RechargeService } from '../../services/recharge.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ToastService } from '../../services/toast.service';
import { UserResponse } from '../../models/user.model';
import { Recharge } from '../../models/recharge.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, SpinnerComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  loading = true;
  profile: UserResponse | null = null;
  recharges: Recharge[] = [];
  stats = { total: 0, success: 0, failed: 0 };

  constructor(
    private userSvc: UserService,
    private rechargeSvc: RechargeService,
    private auth: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    forkJoin({
      profile:   this.userSvc.getProfile().pipe(catchError(() => of(null))),
      recharges: this.rechargeSvc.getMyHistory().pipe(catchError(() => of([])))
    }).subscribe(({ profile, recharges }) => {
      this.profile  = profile as UserResponse;
      this.recharges = (recharges as Recharge[]).slice(0, 5);
      this.stats.total   = (recharges as Recharge[]).length;
      this.stats.success = (recharges as Recharge[]).filter(r => r.status === 'SUCCESS').length;
      this.stats.failed  = (recharges as Recharge[]).filter(r => r.status === 'FAILED').length;
      this.loading = false;
    });
  }

  get initials(): string {
    const u = this.auth.getUsername();
    return u ? u.substring(0, 2).toUpperCase() : 'U';
  }

  get role(): string { return this.auth.getRole(); }
  get username(): string { return this.auth.getUsername(); }

  logout(): void { this.auth.logout(); }

  statusClass(s: string): string {
    return { SUCCESS:'badge-success', FAILED:'badge-failed', PENDING:'badge-pending' }[s] ?? '';
  }
}
