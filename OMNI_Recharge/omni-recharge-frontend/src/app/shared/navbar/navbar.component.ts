import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

/**
 * NavbarComponent — top navigation bar shown on all authenticated pages.
 */
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <div class="navbar-brand" routerLink="/dashboard">
        <span class="brand-emoji">⚡</span>
        <span class="brand-name">OMNI Recharge</span>
      </div>

      <div class="navbar-links">
        <a routerLink="/dashboard"     routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">🏠 Dashboard</a>
        <a routerLink="/operators"     routerLinkActive="active">📡 Operators</a>
        <a routerLink="/recharge/new"  routerLinkActive="active">💳 Recharge</a>
        <a routerLink="/history"       routerLinkActive="active">📜 History</a>
        <a routerLink="/profile"       routerLinkActive="active">👤 Profile</a>
        <a *ngIf="isAdmin" routerLink="/admin" routerLinkActive="active" class="admin-link">🛡️ Admin</a>
      </div>

      <div class="navbar-right">
        <span class="user-chip">
          <span class="user-avatar">{{ initials }}</span>
          <span class="user-name">{{ username }}</span>
          <span class="user-role" [class.admin]="isAdmin">{{ role }}</span>
        </span>
        <button class="logout-btn" (click)="logout()">Logout</button>
      </div>

      <!-- Mobile hamburger -->
      <button class="hamburger" (click)="menuOpen = !menuOpen" aria-label="Toggle menu">
        <span></span><span></span><span></span>
      </button>
    </nav>

    <!-- Mobile drawer -->
    <div class="mobile-drawer" [class.open]="menuOpen" (click)="menuOpen=false">
      <a routerLink="/dashboard">🏠 Dashboard</a>
      <a routerLink="/operators">📡 Operators</a>
      <a routerLink="/recharge/new">💳 Recharge</a>
      <a routerLink="/history">📜 History</a>
      <a routerLink="/profile">👤 Profile</a>
      <a *ngIf="isAdmin" routerLink="/admin">🛡️ Admin</a>
      <button (click)="logout()">🚪 Logout</button>
    </div>
  `,
  styles: [`
    .navbar {
      position: fixed; top: 0; left: 0; right: 0; z-index: 100;
      height: 60px;
      display: flex; align-items: center;
      padding: 0 1.5rem;
      background: rgba(26, 26, 46, 0.95);
      backdrop-filter: blur(10px);
      border-bottom: 2px solid #7c3aed;
      box-shadow: 0 2px 15px rgba(124,58,237,0.3);
    }

    .navbar-brand {
      display: flex; align-items: center; gap: 0.5rem;
      cursor: pointer; text-decoration: none; flex-shrink: 0;
      margin-right: 2rem;
    }
    .brand-emoji { font-size: 1.4rem; }
    .brand-name {
      font-size: 1.1rem; font-weight: 700; color: #fff;
      letter-spacing: -0.3px;
    }

    .navbar-links {
      display: flex; align-items: center; gap: 0.3rem; flex: 1;
    }
    .navbar-links a {
      padding: 0.45rem 0.75rem; border-radius: 8px;
      color: #b0b0c0; font-size: 0.85rem; font-weight: 500;
      text-decoration: none; transition: all 0.2s;
      white-space: nowrap;
    }
    .navbar-links a:hover { color: #fff; background: rgba(124,58,237,0.2); }
    .navbar-links a.active { color: #c4b5fd; background: rgba(124,58,237,0.25); font-weight: 600; }
    .admin-link { color: #fbbf24 !important; }

    .navbar-right { display: flex; align-items: center; gap: 0.75rem; margin-left: auto; }
    .user-chip {
      display: flex; align-items: center; gap: 0.5rem;
      padding: 0.3rem 0.6rem; border-radius: 8px;
      background: rgba(124,58,237,0.1);
    }
    .user-avatar {
      width: 30px; height: 30px; border-radius: 50%;
      background: linear-gradient(135deg, #7c3aed, #a78bfa);
      display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 0.75rem; color: #fff;
    }
    .user-name { font-size: 0.82rem; font-weight: 600; color: #e0e0e0; }
    .user-role {
      font-size: 0.6rem; font-weight: 700; color: #a78bfa;
      text-transform: uppercase; letter-spacing: 0.5px;
      background: rgba(124,58,237,0.15); padding: 0.1rem 0.4rem;
      border-radius: 4px;
    }
    .user-role.admin { color: #fbbf24; background: rgba(251,191,36,0.15); }

    .logout-btn {
      padding: 0.4rem 0.85rem; border-radius: 6px;
      background: #dc2626; border: none;
      color: #fff; font-size: 0.8rem; font-weight: 600;
      cursor: pointer; transition: all 0.2s;
    }
    .logout-btn:hover { background: #ef4444; transform: translateY(-1px); }

    .hamburger {
      display: none; flex-direction: column; gap: 4px;
      background: none; border: none; cursor: pointer; padding: 6px; margin-left: auto;
    }
    .hamburger span {
      display: block; width: 20px; height: 2px;
      background: #b0b0c0; border-radius: 2px; transition: 0.2s;
    }

    .mobile-drawer {
      position: fixed; top: 60px; left: 0; right: 0;
      background: rgba(26,26,46,0.98); backdrop-filter: blur(10px);
      display: flex; flex-direction: column; gap: 0.25rem;
      padding: 0.75rem; z-index: 99;
      transform: translateY(-120%); transition: transform 0.3s ease;
      border-bottom: 2px solid #7c3aed;
    }
    .mobile-drawer.open { transform: translateY(0); }
    .mobile-drawer a, .mobile-drawer button {
      padding: 0.65rem 0.85rem; border-radius: 8px;
      color: #b0b0c0; font-size: 0.9rem; font-weight: 500;
      text-decoration: none; background: none; border: none;
      cursor: pointer; text-align: left; transition: all 0.2s;
    }
    .mobile-drawer a:hover, .mobile-drawer button:hover {
      color: #fff; background: rgba(124,58,237,0.2);
    }

    @media (max-width: 768px) {
      .navbar-links, .navbar-right { display: none; }
      .hamburger { display: flex; }
    }
  `]
})
export class NavbarComponent {
  menuOpen = false;

  constructor(private auth: AuthService, private router: Router) {}

  get username(): string  { return this.auth.getUsername(); }
  get role(): string      { return this.auth.getRole(); }
  get isAdmin(): boolean  { return this.auth.isAdmin(); }
  get initials(): string  {
    const u = this.auth.getUsername();
    return u ? u.substring(0, 2).toUpperCase() : 'U';
  }

  logout(): void { this.auth.logout(); }
}
