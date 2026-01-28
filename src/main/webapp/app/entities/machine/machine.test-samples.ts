import dayjs from 'dayjs/esm';

import { IMachine, NewMachine } from './machine.model';

export const sampleWithRequiredData: IMachine = {
  id: 7267,
  brand: 'harmonise freeload sports',
  type: 'itchy abandon piglet',
  ratePerHour: 5186.38,
  latitude: 8368.4,
  longitude: 23118.09,
  status: 'AVAILABLE',
  createdDate: dayjs('2025-07-14T02:17'),
  categoryId: 10,
  subcategoryId: 20,
};

export const sampleWithPartialData: IMachine = {
  id: 25145,
  brand: 'needily likewise swan',
  type: 'quaintly off',
  vinNumber: 'consequently zowie to',
  chassisNumber: 'unblock scow meanwhile',
  description: 'yuck',
  capacityTon: 19442,
  ratePerHour: 5969.05,
  ratePerDay: 12000,
  minimumUsageHours: 24762,
  latitude: 3984.6,
  longitude: 21238.79,
  driverBatta: 18825.63,
  serviceabilityRangeKm: 11003,
  status: 'NOT_AVAILABLE',
  createdDate: dayjs('2025-07-14T12:32'),
  categoryId: 10,
  subcategoryId: 20,
};

export const sampleWithFullData: IMachine = {
  id: 31319,
  brand: 'lovely',
  type: 'elegant',
  tag: 'where pish',
  model: 'counterpart vacation knotty',
  vinNumber: 'raid justly',
  chassisNumber: 'apropos',
  description: 'however mature which',
  capacityTon: 3122,
  ratePerHour: 15222.65,
  minimumUsageHours: 29925,
  latitude: 27194.92,
  longitude: 8169.97,
  transportationCharge: 16659.24,
  driverBatta: 3733.31,
  serviceabilityRangeKm: 23914,
  status: 'NOT_AVAILABLE',
  createdDate: dayjs('2025-07-13T21:40'),
  categoryId: 10,
  subcategoryId: 20,
};

export const sampleWithNewData: NewMachine = {
  brand: 'um ragged',
  type: 'doc',
  ratePerHour: 19066.5,
  latitude: 788.82,
  longitude: 26098.39,
  status: 'AVAILABLE',
  createdDate: dayjs('2025-07-13T20:24'),
  categoryId: 10,
  subcategoryId: 20,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
