import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { BehaviorSubject, catchError, map, Observable, of, tap, throwError } from "rxjs";
import { AuthStore } from "../components/auth/auth.store";

@Injectable()
export class GoogleCalendarService {
    

    private httpClient = inject(HttpClient)
    private router = inject(Router)

    username!: string

    getAuthorizationUrl(username: string): Observable<string> {
        return this.httpClient.get<{authUrl: string}>(
            `/api/calendar/auth-url?username=${encodeURIComponent(username)}`
        ).pipe(
            map(response => response.authUrl)
        );
    }

    handleOAuthCallback(username: string, code: string): Observable<boolean> {
        return this.httpClient.get<any>(
            `/api/calendar/oauth-callback?username=${encodeURIComponent(username)}&code=${encodeURIComponent(code)}`
        ).pipe(
            tap(response => console.log('Raw API response:', response)), 
            map(response => {
                console.log('Processing response:', response);
                if (response?.status === 'success') {
                    console.info('>> navigating to build page')
                    this.router.navigateByUrl(`/build/${username}`);
                    return true;
                }
                return false;
            }),
            catchError(error => {
                console.error('OAuth callback failed:', error);
                return of(false);
            })
        );
    }
    
    // initiate google oauth flow
    initiateGoogleAuth(username: string) {
        this.getAuthorizationUrl(username).subscribe({
          next: (authUrl) => {
            const urlWithUsername = `{authUrl}&username=${username}`
            window.location.href = authUrl;
          },
          error: (error) => {
            console.error('Failed to get authorization URL', error);
          }
        });
    }

    syncMealPlans(username: string): Observable<any> {
        const params = new HttpParams()
            .set("username", username)
        return this.httpClient.get(`/api/calendar/sync-meal-plans`, {params})
    }
}