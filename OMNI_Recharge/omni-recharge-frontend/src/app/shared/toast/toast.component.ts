import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ToastService, Toast } from '../../services/toast.service';

/**
 * ToastComponent renders global toast notifications from ToastService.
 * Place this once in app.html so it's always visible.
 */
@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container" aria-live="polite">
      <div
        *ngFor="let toast of toasts"
        class="toast toast--{{ toast.type }}"
        (click)="dismiss(toast.id)"
        role="alert"
      >
        <span class="toast-icon">{{ icons[toast.type] }}</span>
        <span class="toast-message">{{ toast.message }}</span>
        <button class="toast-close" aria-label="Close">✕</button>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 1.5rem;
      right: 1.5rem;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      max-width: 380px;
    }
    .toast {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 1rem 1.25rem;
      border-radius: 12px;
      backdrop-filter: blur(12px);
      cursor: pointer;
      animation: slideIn 0.3s ease;
      font-size: 0.9rem;
      font-weight: 500;
      box-shadow: 0 8px 32px rgba(0,0,0,0.25);
    }
    @keyframes slideIn {
      from { transform: translateX(120%); opacity: 0; }
      to   { transform: translateX(0);   opacity: 1; }
    }
    .toast--success { background: rgba(16,185,129,0.95); color: #fff; }
    .toast--error   { background: rgba(239,68,68,0.95);  color: #fff; }
    .toast--info    { background: rgba(59,130,246,0.95); color: #fff; }
    .toast--warning { background: rgba(245,158,11,0.95); color: #fff; }
    .toast-icon { font-size: 1.1rem; }
    .toast-message { flex: 1; }
    .toast-close {
      background: none; border: none; color: inherit;
      cursor: pointer; font-size: 0.85rem; opacity: 0.75;
    }
    .toast-close:hover { opacity: 1; }
  `]
})
export class ToastComponent implements OnInit, OnDestroy {
  toasts: Toast[] = [];
  icons: Record<Toast['type'], string> = {
    success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️'
  };
  private sub!: Subscription;

  constructor(private toastSvc: ToastService) {}

  ngOnInit(): void {
    this.sub = this.toastSvc.toasts$.subscribe(t => (this.toasts = t));
  }

  dismiss(id: number): void {
    this.toastSvc.remove(id);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
