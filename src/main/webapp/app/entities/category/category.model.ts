import { ISubcategory } from 'app/entities/subcategory/subcategory.model';

export interface ICategory {
  id: number;
  name?: string | null;
  description?: string | null;
  //   image?: string | null;
  //   imageContentType?: string | null;
  subcategories?: ISubcategory[] | null;
  typeId: number | null;
  imageUrl?: string | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
