import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ItemService } from '../../services/item.service';
import { CATEGORIES, Category, ItemRequest, ItemType } from '../../models/item';
import { describeError } from '../../utils/api-error';
import { LabelPipe } from '../../pipes/label.pipe';

type FieldName = 'type' | 'title' | 'category' | 'location' | 'eventDate';

function today(): string {
  const d = new Date();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${month}-${day}`;
}

@Component({
  selector: 'app-report-item',
  imports: [ReactiveFormsModule, RouterLink, LabelPipe],
  templateUrl: './report-item.html',
  styleUrl: './report-item.css'
})
export class ReportItem implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private itemService = inject(ItemService);

  categories = CATEGORIES;
  maxDate = today();
  editingId = signal<number | null>(null);
  serverErrors = signal<Record<string, string>>({});
  generalError = signal<string | null>(null);
  submitting = signal(false);

  form = this.fb.nonNullable.group({
    type: ['LOST' as ItemType, Validators.required],
    title: ['', Validators.required],
    description: [''],
    category: ['ELECTRONICS' as Category, Validators.required],
    location: ['', Validators.required],
    eventDate: [today(), Validators.required],
    imageUrl: ['']
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.editingId.set(id);
      this.form.controls.type.disable();   // lost/found can't change after reporting
      this.itemService.get(id).subscribe({
        next: (item) => this.form.patchValue({
          type: item.type,
          title: item.title,
          description: item.description ?? '',
          category: item.category,
          location: item.location,
          eventDate: item.eventDate,
          imageUrl: item.imageUrl ?? ''
        }),
        error: (err: HttpErrorResponse) => this.generalError.set(describeError(err))
      });
    }
  }

  errorFor(field: FieldName): string | null {
    const serverMessage = this.serverErrors()[field];
    if (serverMessage) {
      return `This field ${serverMessage}.`;
    }
    const control = this.form.controls[field];
    if (control.touched && control.hasError('required')) {
      return 'This field is required.';
    }
    return null;
  }

  submit() {
    this.serverErrors.set({});
    this.generalError.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const request: ItemRequest = this.form.getRawValue();
    const id = this.editingId();
    const call = id ? this.itemService.update(id, request) : this.itemService.create(request);

    this.submitting.set(true);
    call.subscribe({
      next: (item) => this.router.navigate(['/items', item.id]),
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        if (err.error?.errors) {
          this.serverErrors.set(err.error.errors);
        } else {
          this.generalError.set(describeError(err));
        }
      }
    });
  }
}