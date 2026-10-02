import { Routes } from '@angular/router';
import { ItemList } from './pages/item-list/item-list';
import { ItemDetail } from './pages/item-detail/item-detail';
import { ReportItem } from './pages/report-item/report-item';

export const routes: Routes = [
  { path: '', component: ItemList },
  { path: 'items/:id', component: ItemDetail },
  { path: 'report', component: ReportItem },
  { path: '**', redirectTo: '' }
];