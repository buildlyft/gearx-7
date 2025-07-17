import { IMachine } from 'app/entities/machine/machine.model';

export interface IPartner {
  id: number;
  name?: string | null;
  companyName?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  preferredContactTime?: string | null;
  gstNumber?: string | null;
  panNumber?: string | null;
  machines?: IMachine[] | null;
}

export type NewPartner = Omit<IPartner, 'id'> & { id: null };
