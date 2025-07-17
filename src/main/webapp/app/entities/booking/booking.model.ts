import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
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
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
