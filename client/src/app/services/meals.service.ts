import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Ingredient, Meal } from "../models";
import { firstValueFrom, Observable } from "rxjs";

@Injectable()
export class MealService {
    
    private httpClient = inject(HttpClient)

    getMealPlan(username: string, start: string, end: string): Observable<any>{
        const params = new HttpParams()
            .set("start", start)
            .set("end", end)
        return this.httpClient.get(`api/${username}/plan`, {params})
    }

    getTodaysMeals(username: string): Observable<Meal[]> {
        const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD
        return this.httpClient.get<Meal[]>(`api/${username}/plan`, {
          params: new HttpParams()
            .set('start', today)
            .set('end', today)
        });
      }
    
    postMealPlan(username: string, meals: Meal[]): Promise<any>{
        console.info('>>> meals passed to springboot: ', meals)
        return firstValueFrom(this.httpClient.post(`api/${username}/plan`, meals))
    }

    getMealDetails(username: string, recipeId: number): Promise<any>{
        const params = new HttpParams()
            .set("username", username)
        return firstValueFrom(this.httpClient.get(`api/meal/${recipeId}`, {params}))
    }

    getGroceryList(username: string): Observable<any>{
        const params = new HttpParams()
            .set("username", username)
        return this.httpClient.get('api/groceries', {params})
    }
}