import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable, from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Only add the token to requests to our API
    if (request.url.includes('/api/spotify')) {
      return from(this.authService.token$).pipe(
        switchMap(token => {
          if (token) {
            // Clone the request and add the authorization header
            const authReq = request.clone({
              headers: request.headers.set('Authorization', `Bearer ${token}`)
            });
            return next.handle(authReq);
          }
          return next.handle(request);
        })
      );
    }
    
    return next.handle(request);
  }
} 