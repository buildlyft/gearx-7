import dayjs from 'dayjs/esm';
import { IMachine } from 'app/entities/machine/machine.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';

export interface IBooking {
  id: number;
  startDateTime?: dayjs.Dayjs | null;
  endDateTime?: dayjs.Dayjs | null;
  status?: keyof typeof BookingStatus | null;
  additionalDetails?: string | null;
  worksiteImageUrl?: string | null;
  customerLat?: number | null;
  customerLong?: number | null;
  createdDate?: dayjs.Dayjs | null;
  machine?: IMachine | null;
  customer?: ICustomer | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
