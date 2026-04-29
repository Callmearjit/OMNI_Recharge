import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * authGuard — protects routes that require the user to be logged in.
 * Redirects to /login if no valid session exists.
 */
export const authGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }
  return router.createUrlTree(['/login']);
};

/**
 * adminGuard — protects routes that require ADMIN role.
 * Redirects to /dashboard if user is not admin.
 */
export const adminGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn() && auth.isAdmin()) {
    return true;
  }
  return router.createUrlTree(['/dashboard']);
};

/**
 * guestGuard — prevents logged-in users from accessing login/register pages.
 */
export const guestGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return true;
  }
  return router.createUrlTree(['/dashboard']);
};
