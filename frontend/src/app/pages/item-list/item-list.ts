import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemService } from '../../services/item.service';
import { Item } from '../../models/item';
import { LabelPipe } from '../../pipes/label.pipe';
import { EventDatePipe } from '../../pipes/event-date.pipe';
@Component({
  selector: 'app-item-list',
  imports: [RouterLink, LabelPipe, EventDatePipe],
  templateUrl: './item-list.html',
  styleUrl: './item-list.css'
})
export class ItemList implements OnInit {
  private itemService = inject(ItemService);

  items = signal<Item[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.itemService.list().subscribe({
      next: (page) => {
        this.items.set(page.content);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load items. Is the backend running on port 8080?');
        this.loading.set(false);
      }
    });
  }
}