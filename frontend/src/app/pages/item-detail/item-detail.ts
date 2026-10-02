import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ItemService } from '../../services/item.service';
import { ClaimService } from '../../services/claim.service';
import { UserService } from '../../services/user.service';
import { Item } from '../../models/item';
import { Claim } from '../../models/claim';
import { describeError } from '../../utils/api-error';
import { LabelPipe } from '../../pipes/label.pipe';
import { EventDatePipe } from '../../pipes/event-date.pipe';


@Component({
  selector: 'app-item-detail',
  imports: [RouterLink, LabelPipe, EventDatePipe],
  templateUrl: './item-detail.html',
  styleUrl: './item-detail.css'
})
export class ItemDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private itemService = inject(ItemService);
  private claimService = inject(ClaimService);
  private userService = inject(UserService);
  private router = inject(Router);

  item = signal<Item | null>(null);
  loadError = signal<string | null>(null);
  claims = signal<Claim[]>([]);
  message = signal('');
  claimSent = signal(false);
  actionError = signal<string | null>(null);

  isReporter = computed(() => this.item()?.reporterId === this.userService.currentUserId());

  constructor() {
    // Runs again whenever the item or the current user changes
    effect(() => {
      const item = this.item();
      const isReporter = this.isReporter();
      this.claimSent.set(false);
      this.actionError.set(null);
      if (item && isReporter) {
        this.loadClaims(item.id);
      } else {
        this.claims.set([]);
      }
    });
  }

  ngOnInit() {
    this.loadItem();
  }

  userName(id: number): string {
    return this.userService.users().find((u) => u.id === id)?.name ?? `User #${id}`;
  }

  onMessageInput(event: Event) {
    this.message.set((event.target as HTMLTextAreaElement).value);
  }

  submitClaim() {
    const item = this.item();
    if (!item) return;
    this.claimService.create(item.id, this.message()).subscribe({
      next: () => {
        this.claimSent.set(true);
        this.message.set('');
        this.actionError.set(null);
      },
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }

  approve(claimId: number) {
    this.claimService.approve(claimId).subscribe({
      next: () => this.loadItem(),
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }

  reject(claimId: number) {
    this.claimService.reject(claimId).subscribe({
      next: () => this.loadItem(),
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }

  markReturned() {
    const item = this.item();
    if (!item) return;
    this.itemService.markReturned(item.id).subscribe({
      next: (updated) => this.item.set(updated),
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }

  deleteItem() {
    const item = this.item();
    if (!item || !confirm('Delete this item? This cannot be undone.')) return;
    this.itemService.delete(item.id).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }

  private loadItem() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.itemService.get(id).subscribe({
      next: (item) => this.item.set(item),
      error: (err: HttpErrorResponse) => this.loadError.set(describeError(err))
    });
  }

  private loadClaims(itemId: number) {
    this.claimService.forItem(itemId).subscribe({
      next: (claims) => this.claims.set(claims),
      error: (err: HttpErrorResponse) => this.actionError.set(describeError(err))
    });
  }
}