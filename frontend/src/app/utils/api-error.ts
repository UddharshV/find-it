import { HttpErrorResponse } from '@angular/common/http';

export function describeError(err: HttpErrorResponse): string {
  const body = err.error;
  if (body?.errors) {
    return Object.entries(body.errors).map(([field, msg]) => `${field} ${msg}`).join(', ');
  }
  if (body?.detail) {
    return body.detail;
  }
  return 'Something went wrong. Is the backend running?';
}