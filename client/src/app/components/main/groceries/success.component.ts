import { Component, inject, OnInit } from '@angular/core';
import { NonNullableFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GroceryService } from '../../../services/groceries.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-success',
  standalone: false,
  templateUrl: './success.component.html',
  styleUrl: './success.component.css'
})
export class SuccessComponent implements OnInit {

  sessionId: string | null = null
  orderDetails: any = null
  isLoading = true

  private activatedRoute = inject(ActivatedRoute)
  private grocerySvc = inject(GroceryService)
  private messageSvc = inject(MessageService)

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.sessionId = params['session_id'];
      if (this.sessionId) {
        this.fetchOrderDetails(this.sessionId);
      } else {
        this.messageSvc.add({
          severity: 'warn',
          summary: 'No Order Found',
          detail: 'Unable to retrieve order details'
        });
        this.isLoading = false;
      }
    });
  }

  fetchOrderDetails(sessionId: string): void {
    this.grocerySvc.getOrderBySessionId(sessionId).subscribe({
      next: (order) => {
        this.orderDetails = order;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching order:', err);
        this.messageSvc.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load order details'
        });
        this.isLoading = false;
      }
    });
  }

  formatPrice(price: number): string {
    return (price / 100).toFixed(2); // Convert cents to dollars
  }

}
