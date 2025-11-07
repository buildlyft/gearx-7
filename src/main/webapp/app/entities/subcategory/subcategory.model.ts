import { ICategory } from 'app/entities/category/category.model';

export interface ISubcategory {
  id: number;
  name?: string | null;
  description?: string | null;
  image?: string | null;
  imageContentType?: string | null;
  category?: ICategory | null;
}

export type NewSubcategory = Omit<ISubcategory, 'id'> & { id: null };
