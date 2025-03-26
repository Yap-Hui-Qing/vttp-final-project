import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { Auth, User } from "../../models";
import { tap } from "rxjs";

@Injectable()
export class AuthStore extends ComponentStore<Auth> {

    constructor() {
        super({
            token: sessionStorage.getItem('token') || '',
            username: sessionStorage.getItem('username') || ''
        })
    }

    // selectors
    readonly getToken$ = this.select<string>(
        (slice: Auth) => slice.token
    )
    readonly getUsername$ = this.select<string>(
        (slice: Auth) => slice.username
    )
    readonly isAuthenticated$ = this.select<boolean>(
        (slice: Auth) => !!slice.token && !!slice.username
    )

    // updaters
    readonly saveToken = this.updater<string>(
        (slice: Auth, token: string) => {
            sessionStorage.setItem('token', token)
            return { ...slice, token}
        }
    )

    readonly saveUsername = this.updater<string>(
        (slice: Auth, username: string) => {
            sessionStorage.setItem('username', username)
            return { ...slice, username}
        }
    )

    readonly logoutToken = this.updater(
        (slice: Auth) => {
            return {
                ...slice,
                token: ''
            }
        }
    )

    readonly logoutUser = this.updater(
        (slice: Auth) => {
            return {
                ...slice,
                username: ''
            }
        }
    )

    // Logout
    readonly logout = this.effect((trigger$) =>
        trigger$.pipe(
            tap(() => {
                this.logoutToken();
                this.logoutUser();
                sessionStorage.removeItem('token')
                sessionStorage.removeItem('username')
            }),
    ))

    logState() {
        console.log('>>> component store: ', this.get());
    }
}