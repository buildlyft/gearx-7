import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 25012,
  startDateTime: dayjs('2025-07-13T19:37'),
  endDateTime: dayjs('2025-07-13T23:05'),
  status: 'CANCELLED',
};

export const sampleWithPartialData: IBooking = {
  id: 11548,
  startDateTime: dayjs('2025-07-14T04:20'),
  endDateTime: dayjs('2025-07-13T19:24'),
  status: 'COMPLETED',
  additionalDetails: 'yahoo empowerment container',
  worksiteImageUrl: 'internal indeed',
  customerLat: 10661.34,
  createdDate: dayjs('2025-07-13T23:14'),
};

export const sampleWithFullData: IBooking = {
  id: 9804,
  startDateTime: dayjs('2025-07-13T22:29'),
  endDateTime: dayjs('2025-07-13T16:50'),
  status: 'COMPLETED',
  additionalDetails: 'when kneejerk although',
  worksiteImageUrl: 'panic godfather',
  customerLat: 17911.83,
  customerLong: 30065.53,
  createdDate: dayjs('2025-07-13T22:04'),
};

export const sampleWithNewData: NewBooking = {
  startDateTime: dayjs('2025-07-13T19:42'),
  endDateTime: dayjs('2025-07-14T06:26'),
  status: 'CANCELLED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
