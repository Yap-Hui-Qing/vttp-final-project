import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, of, Subject, tap, throwError } from 'rxjs';
import { User } from '../../models';
import { AuthStore } from './auth.store';
import { MessageService } from 'primeng/api';

@Injectable()

export class AuthService {

  private http = inject(HttpClient)
  private router = inject(Router)
  private userStore = inject(AuthStore)
  private messageSvc = inject(MessageService)

  private loggedIn$: Subject<boolean> = new Subject<boolean>();
  public loggedInUserSubject$: Subject<string> = new Subject<string>()
  private authStatusSubject = new Subject<boolean>()
  public authStatus$!: Observable<boolean>

  get isLoggedIn() { return this.loggedIn$ }

  login(user: User) {
    this.userStore.logState()
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    })
    return this.http.post<any>('/api/login', user, { headers, observe: 'response' }).pipe(
      map((res) => {
        const token = res.body?.jwtToken
        const user = res.body?.username
        if (token && user) {
          this.userStore.saveToken(token)
          this.userStore.saveUsername(user)
          this.userStore.logState()
          return true
        }
        return false
      }),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error.error)
      }
      ))
  }

  logout() {
    this.loggedIn$.next(false)
    localStorage.removeItem('token')
    this.router.navigate(['/'])
  }

  register(registerRequest: any): Observable<any> {
    return this.http.post('/api/register', registerRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error.error);
      })
    )
  }

  getGoogleAuthStatus(): Observable<boolean> {
    return this.http.get<{authenticated: boolean}>('/api/calendar/google-status').pipe(
      map(response => {
        this.authStatusSubject.next(response.authenticated);
        return response.authenticated
      }),
      catchError(error => {
        console.error('Error checking Google auth status', error);
        this.authStatusSubject.next(false);
        return of(false);
      })
    );
  }

  disconnectGoogleCalendar(): Observable<boolean> {
    return this.http.post<{status: string, message: string}>('/api/calendar/google-disconnect', {}).pipe(
      map(response => {
        if (response.status === 'success') {
          // Update auth status when disconnected
          this.authStatusSubject.next(false);
          return true;
        }
        return false;
      }),
      catchError(error => {
        console.error('Error disconnecting Google account', error);
        return of(false);
      })
    );
  }

  // Method to check auth status on app initialization or periodic checks
  checkGoogleAuthStatus(){
    this.getGoogleAuthStatus().subscribe();
  }

  // Observable to listen to auth status changes
  watchAuthStatus(): Observable<boolean> {
    return this.authStatus$;
  }
  
}

