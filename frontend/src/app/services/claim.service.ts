import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Claim } from '../models/claim';

@Injectable({ providedIn: 'root' })
export class ClaimService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api';

  forItem(itemId: number): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${this.baseUrl}/items/${itemId}/claims`);
  }

  create(itemId: number, message: string): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrl}/items/${itemId}/claims`, { message });
  }

  approve(claimId: number): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrl}/claims/${claimId}/approve`, null);
  }

  reject(claimId: number): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrl}/claims/${claimId}/reject`, null);
  }
}