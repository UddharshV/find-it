export type ItemType = 'LOST' | 'FOUND';
export type ItemStatus = 'OPEN' | 'CLAIMED' | 'RETURNED';
export type Category = 'ELECTRONICS' | 'ID_CARD' | 'KEYS' | 'CLOTHING' | 'BAGS' | 'BOTTLES' | 'BOOKS' | 'OTHER';

export interface Item {
  id: number;
  title: string;
  description: string | null;
  type: ItemType;
  status: ItemStatus;
  category: Category;
  location: string;
  eventDate: string;
  imageUrl: string | null;
  reporterId: number;
  createdAt: string;
  updatedAt: string;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}