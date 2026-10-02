import { Routes } from '@angular/router';
import { ItemList } from './pages/item-list/item-list';
import { ReportItem } from './pages/report-item/report-item';

export const routes: Routes = [
  { path: '', component: ItemList },          // localhost:4200/
  { path: 'report', component: ReportItem },  // localhost:4200/report
  { path: '**', redirectTo: '' }              // any unknown URL → home
];