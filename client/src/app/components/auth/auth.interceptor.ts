import { HttpEvent, HttpHandler, HttpInterceptor, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
    const token = sessionStorage.getItem('token')
        console.log('token from storage: ', token)
        if (token) {
            const authReq = req.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            })
            return next(authReq)
        }
        return next(req)
}

