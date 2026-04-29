import { Routes } from '@angular/router';
import { authGuard, adminGuard, guestGuard } from './guards/auth.guard';

export const routes: Routes = [
  // ── Default redirect ──────────────────────────────────────────────────────
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  // ── Auth pages (guests only) ──────────────────────────────────────────────
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/login/login.component').then(m => m.LoginComponent),
    title: 'Sign In | OMNI Recharge'
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/register/register.component').then(m => m.RegisterComponent),
    title: 'Create Account | OMNI Recharge'
  },

  // ── Protected pages ───────────────────────────────────────────────────────
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    title: 'Dashboard | OMNI Recharge'
  },
  {
    path: 'operators',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/operators/operators.component').then(m => m.OperatorsComponent),
    title: 'Operators | OMNI Recharge'
  },
  {
    path: 'recharge/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/recharge/recharge.component').then(m => m.RechargeComponent),
    title: 'New Recharge | OMNI Recharge'
  },
  {
    path: 'history',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/history/history.component').then(m => m.HistoryComponent),
    title: 'Recharge History | OMNI Recharge'
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/profile/profile.component').then(m => m.ProfileComponent),
    title: 'My Profile | OMNI Recharge'
  },

  // ── Admin-only pages ──────────────────────────────────────────────────────
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./components/admin/admin.component').then(m => m.AdminComponent),
    title: 'Admin Panel | OMNI Recharge'
  },

  // ── Wildcard ──────────────────────────────────────────────────────────────
  { path: '**', redirectTo: 'dashboard' }
];
