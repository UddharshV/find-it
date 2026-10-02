import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ItemService } from '../../services/item.service';
import { Item } from '../../models/item';

@Component({
  selector: 'app-item-detail',
  imports: [RouterLink],
  templateUrl: './item-detail.html',
  styleUrl: './item-detail.css'
})
export class ItemDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private itemService = inject(ItemService);

  item = signal<Item | null>(null);
  error = signal<string | null>(null);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.itemService.get(id).subscribe({
      next: (item) => this.item.set(item),
      error: (err) => this.error.set(err.error?.detail ?? 'Could not load this item.')
    });
  }
}