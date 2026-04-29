import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * SpinnerComponent — reusable loading overlay/inline spinner.
 */
@Component({
  selector: 'app-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="show" [class]="overlay ? 'spinner-overlay' : 'spinner-inline'">
      <div class="spinner-ring">
        <div></div><div></div><div></div><div></div>
      </div>
      <p *ngIf="message" class="spinner-msg">{{ message }}</p>
    </div>
  `,
  styles: [`
    .spinner-overlay {
      position: fixed; inset: 0;
      background: rgba(15,12,41,0.8);
      backdrop-filter: blur(4px);
      display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      z-index: 9998;
    }
    .spinner-inline {
      display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      padding: 2rem;
    }
    .spinner-ring {
      width: 48px; height: 48px;
      position: relative;
    }
    .spinner-ring div {
      box-sizing: border-box;
      display: block; position: absolute;
      width: 44px; height: 44px;
      margin: 2px;
      border: 4px solid transparent;
      border-top-color: #7c3aed;
      border-radius: 50%;
      animation: spin 1s cubic-bezier(0.5,0,0.5,1) infinite;
    }
    .spinner-ring div:nth-child(1) { animation-delay: -0.45s; border-top-color: #7c3aed; }
    .spinner-ring div:nth-child(2) { animation-delay: -0.3s;  border-top-color: #a78bfa; }
    .spinner-ring div:nth-child(3) { animation-delay: -0.15s; border-top-color: #c4b5fd; }
    @keyframes spin { 0% { transform: rotate(0); } 100% { transform: rotate(360deg); } }
    .spinner-msg { color: #c4b5fd; margin-top: 0.85rem; font-size: 0.85rem; }
  `]
})
export class SpinnerComponent {
  @Input() show = false;
  @Input() overlay = true;
  @Input() message = '';
}
