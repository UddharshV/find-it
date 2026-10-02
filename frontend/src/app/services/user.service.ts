import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/users';
  private storageKey = 'findit.userId';

  users = signal<User[]>([]);
  currentUserId = signal<number | null>(this.loadSavedId());

  loadUsers() {
    this.http.get<User[]>(this.baseUrl).subscribe((users) => {
      this.users.set(users);
      const stillExists = users.some((u) => u.id === this.currentUserId());
      if (!stillExists && users.length > 0) {
        this.setCurrentUser(users[0].id);
      }
    });
  }

  setCurrentUser(id: number) {
    this.currentUserId.set(id);
    localStorage.setItem(this.storageKey, String(id));
  }

  private loadSavedId(): number | null {
    const saved = localStorage.getItem(this.storageKey);
    return saved ? Number(saved) : null;
  }
}