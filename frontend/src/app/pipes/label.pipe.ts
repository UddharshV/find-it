import { Pipe, PipeTransform } from '@angular/core';

const LABELS: Record<string, string> = {
  LOST: 'Lost', FOUND: 'Found',
  OPEN: 'Open', CLAIMED: 'Claimed', RETURNED: 'Returned',
  PENDING: 'Pending', APPROVED: 'Approved', REJECTED: 'Rejected',
  ELECTRONICS: 'Electronics', ID_CARD: 'ID card', KEYS: 'Keys', CLOTHING: 'Clothing',
  BAGS: 'Bags', BOTTLES: 'Bottles', BOOKS: 'Books', OTHER: 'Other'
};

@Pipe({ name: 'label' })
export class LabelPipe implements PipeTransform {
  transform(value: string): string {
    return LABELS[value] ?? value;
  }
}