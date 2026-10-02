import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Item, Page } from '../models/item';

@Injectable({ providedIn: 'root' })
export class ItemService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/items';

  list(page = 0, size = 20): Observable<Page<Item>> {
    return this.http.get<Page<Item>>(this.baseUrl, { params: { page, size } });
  }

  get(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.baseUrl}/${id}`);
  }

  markReturned(id: number): Observable<Item> {
    return this.http.post<Item>(`${this.baseUrl}/${id}/return`, null);
  }
}