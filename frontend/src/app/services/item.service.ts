import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Item, ItemFilters, ItemRequest, Page } from '../models/item';

@Injectable({ providedIn: 'root' })
export class ItemService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/items';

list(filters: ItemFilters, page = 0, size = 20): Observable<Page<Item>> {
    const params: Record<string, string | number> = { page, size };
    if (filters.q) params['q'] = filters.q;
    if (filters.type) params['type'] = filters.type;
    if (filters.status) params['status'] = filters.status;
    if (filters.category) params['category'] = filters.category;
    return this.http.get<Page<Item>>(this.baseUrl, { params });
  }

  get(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.baseUrl}/${id}`);
  }

  create(request: ItemRequest): Observable<Item> {
    return this.http.post<Item>(this.baseUrl, request);
  }

  update(id: number, request: ItemRequest): Observable<Item> {
    return this.http.put<Item>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  markReturned(id: number): Observable<Item> {
    return this.http.post<Item>(`${this.baseUrl}/${id}/return`, null);
  }
}