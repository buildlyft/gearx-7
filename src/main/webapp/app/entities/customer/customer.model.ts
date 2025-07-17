export interface ICustomer {
  id: number;
  name?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  pincode?: string | null;
  location?: string | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
