import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemService } from '../../services/item.service';
import { CATEGORIES, ITEM_STATUSES, ITEM_TYPES, Item, ItemFilters } from '../../models/item';
import { LabelPipe } from '../../pipes/label.pipe';
import { EventDatePipe } from '../../pipes/event-date.pipe';

const NO_FILTERS: ItemFilters = { q: '', type: '', status: '', category: '' };

@Component({
  selector: 'app-item-list',
  imports: [RouterLink, LabelPipe, EventDatePipe],
  templateUrl: './item-list.html',
  styleUrl: './item-list.css'
})
export class ItemList implements OnInit {
  private itemService = inject(ItemService);

  readonly pageSize = 12;
  readonly types = ITEM_TYPES;
  readonly statuses = ITEM_STATUSES;
  readonly categories = CATEGORIES;

  items = signal<Item[]>([]);
  page = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  loading = signal(true);
  error = signal<string | null>(null);

  filters = signal<ItemFilters>(NO_FILTERS);
  searchText = signal('');   // what's typed, before Search is pressed

  hasPrevious = computed(() => this.page() > 0);
  hasNext = computed(() => this.page() + 1 < this.totalPages());
  hasActiveFilters = computed(() => {
    const f = this.filters();
    return !!(f.q || f.type || f.status || f.category);
  });

  ngOnInit() {
    this.load(0);
  }

  onSearchInput(event: Event) {
    this.searchText.set((event.target as HTMLInputElement).value);
  }

  applySearch(event: Event) {
    event.preventDefault();   // stop the browser from reloading the page
    this.filters.update((f) => ({ ...f, q: this.searchText().trim() }));
    this.load(0);
  }

  setFilter(name: 'type' | 'status' | 'category', event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.filters.update((f) => ({ ...f, [name]: value }));
    this.load(0);
  }

  clearFilters() {
    this.filters.set(NO_FILTERS);
    this.searchText.set('');
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
    this.itemService.list(this.filters(), page, this.pageSize).subscribe({
      next: (result) => {
        this.items.set(result.content);
        this.page.set(result.page);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load items. Is the backend running on port 8080?');
        this.loading.set(false);
      }
    });
  }
}