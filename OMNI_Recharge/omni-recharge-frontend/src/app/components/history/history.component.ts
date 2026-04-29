import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RechargeService } from '../../services/recharge.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ToastService } from '../../services/toast.service';
import { Recharge } from '../../models/recharge.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavbarComponent, SpinnerComponent],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  loading = true;
  recharges: Recharge[] = [];
  filtered: Recharge[] = [];
  searchQuery = '';
  filterStatus = '';

  constructor(private rechargeSvc: RechargeService, private toast: ToastService) {}

  ngOnInit(): void {
    this.rechargeSvc.getMyHistory().subscribe({
      next: (data) => {
        this.recharges = data.sort((a, b) => b.id - a.id);
        this.filtered  = [...this.recharges];
        this.loading   = false;
      },
      error: () => {
        this.toast.error('Failed to load history.');
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    this.filtered = this.recharges.filter(r => {
      const matchSearch = !this.searchQuery ||
        r.mobileNumber?.includes(this.searchQuery) ||
        r.id?.toString().includes(this.searchQuery);
      const matchStatus = !this.filterStatus || r.status === this.filterStatus;
      return matchSearch && matchStatus;
    });
  }

  onSearch():   void { this.applyFilter(); }
  onFilter():   void { this.applyFilter(); }
  clearFilter():void { this.searchQuery = ''; this.filterStatus = ''; this.filtered = [...this.recharges]; }

  statusClass(s: string): string {
    return { SUCCESS:'badge-success', FAILED:'badge-failed', PENDING:'badge-pending' }[s] ?? '';
  }

  statusIcon(s: string): string {
    return { SUCCESS:'✅', FAILED:'❌', PENDING:'⏳' }[s] ?? '❓';
  }
}
