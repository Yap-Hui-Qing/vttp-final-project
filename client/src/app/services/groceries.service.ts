import { HttpClient, HttpParams, HttpRequest } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom, Observable } from "rxjs";
import { CartItem, Product, StripeResponse } from "../models";

@Injectable()
export class GroceryService {

    private httpClient = inject(HttpClient)

    getProducts(): Promise<Product[]>{
        return firstValueFrom(this.httpClient.get<Product[]>('/api/products'))
    }

    checkout(cartItems: CartItem[], email: string): Promise<StripeResponse> {
        const productRequests = cartItems.map(item => ({
          name: item.name,
          amount: item.price, 
          quantity: item.quantity,
          currency: 'SGD' 
        }));
    
        return firstValueFrom(
          this.httpClient.post<StripeResponse>('/api/checkout', {
            productRequests: productRequests,
            email: email
          })
        );
    }

    getOrderBySessionId(sessionId: string): Observable<any>{
        const params = new HttpParams()
            .set("session_id", sessionId)
        return this.httpClient.get(`/api/orders`, {params})
    }
}