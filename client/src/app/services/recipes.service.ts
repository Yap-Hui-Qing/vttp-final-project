import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, map, Observable, tap, throwError } from "rxjs";
import { Preference, Recipe } from "../models";

@Injectable()
export class RecipeService {

    private httpClient = inject(HttpClient)

    getRecipeSearch(preference: Preference): Observable<Recipe[]> {
        const params = new HttpParams()
            .set("diet", preference.diet)
            .set("intolerances", preference.allergies)
            .set("maxServings", preference.serving)
        console.info(params)
        return this.httpClient.get<Recipe[]>('api/search', { params }).pipe(
            tap((res) => console.log('Received api response: ', res))
        )
    }
}