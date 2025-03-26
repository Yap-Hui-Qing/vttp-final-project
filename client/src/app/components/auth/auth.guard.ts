import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "./auth.service";
import { map, Observable, take } from "rxjs";
import { AuthStore } from "./auth.store";

export const authenticated: CanActivateFn = 
    (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

        const authStore = inject(AuthStore)
        const router = inject(Router)

        return authStore.isAuthenticated$.pipe(
            take(1),
            map((isAuthenticated: boolean) => {
                console.log('is logged in: ', isAuthenticated)
                if (!isAuthenticated) {
                    // router.navigate(['/login'])
                    // return false
                    return router.parseUrl('/login')
                }
                return true
            })
        )
}




