import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { Router } from '@angular/router';

/**
 * authInterceptor — functional HTTP interceptor that automatically
 * attaches the JWT Bearer token to every outgoing request, and
 * handles 401/403 globally.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth   = inject(AuthService);
  const toast  = inject(ToastService);
  const router = inject(Router);

  const token = auth.getToken();

  // Clone request and attach Authorization header if token exists
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          'X-User-Id': auth.getUsername(),
          'X-Role':    auth.getRole()
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        toast.error('Session expired. Please log in again.');
        auth.logout();
      } else if (err.status === 403) {
        toast.error('Access Denied: You do not have permission.');
      } else if (err.status === 409) {
        toast.error('Conflict: Resource already exists.');
      } else if (err.status === 0) {
        toast.error('Cannot reach server. Check your connection.');
      }
      return throwError(() => err);
    })
  );
};
