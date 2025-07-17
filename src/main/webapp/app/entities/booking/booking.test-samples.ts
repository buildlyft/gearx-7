import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 25384,
  startDateTime: dayjs('2025-07-14T03:41'),
  endDateTime: dayjs('2025-07-13T20:29'),
  status: 'COMPLETED',
};

export const sampleWithPartialData: IBooking = {
  id: 19041,
  startDateTime: dayjs('2025-07-14T08:28'),
  endDateTime: dayjs('2025-07-13T21:58'),
  status: 'PENDING',
  additionalDetails: 'wetly apud',
  customerLat: 19260.26,
  createdDate: dayjs('2025-07-14T06:18'),
};

export const sampleWithFullData: IBooking = {
  id: 9458,
  startDateTime: dayjs('2025-07-14T06:30'),
  endDateTime: dayjs('2025-07-14T00:18'),
  status: 'PENDING',
  additionalDetails: 'pish verbally',
  worksiteImageUrl: 'nor set',
  customerLat: 4152.77,
  customerLong: 10988.59,
  createdDate: dayjs('2025-07-14T02:18'),
};

export const sampleWithNewData: NewBooking = {
  startDateTime: dayjs('2025-07-13T20:13'),
  endDateTime: dayjs('2025-07-14T06:06'),
  status: 'CANCELLED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
