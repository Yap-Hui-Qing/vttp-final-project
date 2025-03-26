import { Component, inject, OnInit } from '@angular/core';
import { GroceryService } from '../../../services/groceries.service';
import { CartItem, Product } from '../../../models';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-store',
  standalone: false,
  templateUrl: './store.component.html',
  styleUrl: './store.component.css'
})
export class StoreComponent implements OnInit {

  private grocerySvc = inject(GroceryService)
  private messageSvc = inject(MessageService)

  products$!: Promise<Product[]>
  cart: { cart: CartItem[] } = { cart: [] }

  ngOnInit(): void {
    this.products$ = this.grocerySvc.getProducts()
  }

  addToCart(prod: Product) {
    const existingItem = this.cart.cart.find(item => item.id === prod.id)
    if (existingItem) {
      existingItem.quantity++
    } else {
      this.cart.cart.push({ ...prod, quantity: 1 })
    }
    this.messageSvc.add({
      severity: 'success',
      summary: 'Added to cart',
      detail: `${prod.name} added to cart`
    });
  }

  removeFromCart(item: CartItem): void {
    const index = this.cart.cart.findIndex(cartItem => cartItem.id === item.id);
    if (index !== -1) {
      if (this.cart.cart[index].quantity > 1) {
        this.cart.cart[index].quantity--;
      } else {
        this.cart.cart.splice(index, 1);
      }
    }
  }

  getCartTotal(): number {
    return this.cart.cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  async checkout(): Promise<void> {
    const userEmail = 'qing19772001@gmail.com'
    try {
      const response = await this.grocerySvc.checkout(this.cart.cart, userEmail);
      if (response.status === 'SUCCESS') {
        // Redirect to Stripe checkout page
        window.location.href = response.sessionUrl;
      } else {
        this.messageSvc.add({
          severity: 'error',
          summary: 'Checkout failed',
          detail: response.message || 'Unknown error occurred'
        });
      }
    } catch (error) {
      this.messageSvc.add({
        severity: 'error',
        summary: 'Checkout failed',
        detail: 'There was an error during checkout'
      });
      console.error('Checkout error:', error);
    }
  }
}
