import { Component, OnInit, computed, inject, signal } from '@angular/core';
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

  readonly pageSize = 16;

  items = signal<Item[]>([]);
  page = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  loading = signal(true);
  error = signal<string | null>(null);

  hasPrevious = computed(() => this.page() > 0);
  hasNext = computed(() => this.page() + 1 < this.totalPages());

  ngOnInit() {
    this.load(0);
  }

  previous() {
    if (this.hasPrevious()) this.load(this.page() - 1);
  }

  next() {
    if (this.hasNext()) this.load(this.page() + 1);
  }

  private load(page: number) {
    this.loading.set(true);
    this.error.set(null);
    this.itemService.list(page, this.pageSize).subscribe({
      next: (result) => {
        this.items.set(result.content);
        this.page.set(result.page);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
        window.scrollTo({ top: 0 });
      },
      error: () => {
        this.error.set('Could not load items. Is the backend running on port 8080?');
        this.loading.set(false);
      }
    });
  }
}