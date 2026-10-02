import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserService } from '../services/user.service';

export const userIdInterceptor: HttpInterceptorFn = (req, next) => {
  const userId = inject(UserService).currentUserId();
  if (userId === null) {
    return next(req);
  }
  return next(req.clone({ setHeaders: { 'X-User-Id': String(userId) } }));
};