import { Pipe, PipeTransform } from '@angular/core';

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

@Pipe({ name: 'eventDate' })
export class EventDatePipe implements PipeTransform {
  transform(value: string): string {
    const [year, month, day] = value.split('-');
    if (!day) return value;
    return `${MONTHS[Number(month) - 1]} ${Number(day)}, ${year}`;
  }
}