import { ICategory } from 'app/entities/category/category.model';

export interface IType {
  id: number;
  typeName?: string | null;
  imageUrl?: string | null;
  categories?: ICategory[] | null;
}

export type NewType = Omit<IType, 'id'> & { id: null };
