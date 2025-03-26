import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom, Observable } from "rxjs";
import { Preference } from "../models";
import { UserStore } from "../user.store";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";

@Injectable()
export class PreferenceService {

    private httpClient = inject(HttpClient)
    private router = inject(Router)
    private messageSvc = inject(MessageService)
    
    getuserPreference(username: string): Observable<any> {
        return this.httpClient.get(`api/${username}/preference`)
    }

    updateUserPreference(username: string, preference: Preference): Promise<any> {
        return firstValueFrom(this.httpClient.put(`api/${username}/preference`, preference))
    }

    promptUserForPreference(username: string) {
        console.info("Prompting user to set preferences.");

        this.messageSvc.add({
            severity: 'info',
            summary: 'Setting Preferences',
            detail: 'You will be redirected to set your preferences.',
            life: 5000  // Message duration in milliseconds
          });
          
        setTimeout(() => {
            this.router.navigate(['/home', username, 'profile']);
        }, 5000)
    }
}
