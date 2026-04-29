import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

/**
 * ToastService manages app-wide toast notifications.
 * Components subscribe to toasts$ and the ToastComponent renders them.
 */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private _toasts = new BehaviorSubject<Toast[]>([]);
  toasts$ = this._toasts.asObservable();
  private nextId = 1;

  show(message: string, type: Toast['type'] = 'info', duration = 3500): void {
    const id = this.nextId++;
    const current = this._toasts.getValue();
    this._toasts.next([...current, { id, message, type }]);

    // Auto-remove after duration
    setTimeout(() => this.remove(id), duration);
  }

  success(msg: string): void { this.show(msg, 'success'); }
  error(msg: string):   void { this.show(msg, 'error', 5000); }
  info(msg: string):    void { this.show(msg, 'info'); }
  warning(msg: string): void { this.show(msg, 'warning'); }

  remove(id: number): void {
    this._toasts.next(this._toasts.getValue().filter(t => t.id !== id));
  }
}
