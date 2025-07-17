import dayjs from 'dayjs/esm';

import { IMachine, NewMachine } from './machine.model';

export const sampleWithRequiredData: IMachine = {
  id: 25423,
  brand: 'bootie playfully unequaled',
  type: 'carry',
  ratePerHour: 26332.11,
  latitude: 23200.04,
  longitude: 22675.8,
  status: 'NOT_AVAILABLE',
  createdDate: dayjs('2025-07-13T22:49'),
};

export const sampleWithPartialData: IMachine = {
  id: 27275,
  brand: 'aggressive male boastfully',
  type: 'idle likewise',
  model: 'blah diligently cocoa',
  vinNumber: 'geez liquor fishery',
  chassisNumber: 'darn nervously pish',
  capacityTon: 8813,
  ratePerHour: 13702.91,
  latitude: 31407.95,
  longitude: 31154.07,
  serviceabilityRangeKm: 5869,
  status: 'AVAILABLE',
  createdDate: dayjs('2025-07-14T08:27'),
};

export const sampleWithFullData: IMachine = {
  id: 7312,
  brand: 'quarrelsomely lift',
  type: 'complain expostulate',
  tag: 'outside hence properly',
  model: 'medical hmph adduce',
  vinNumber: 'yuck afore lest',
  chassisNumber: 'meh because',
  description: 'woot wary',
  capacityTon: 7294,
  ratePerHour: 19874.18,
  minimumUsageHours: 7947,
  latitude: 2316.94,
  longitude: 31214.3,
  transportationCharge: 3282.36,
  driverBatta: 23038.38,
  serviceabilityRangeKm: 15914,
  status: 'AVAILABLE',
  createdDate: dayjs('2025-07-14T00:14'),
};

export const sampleWithNewData: NewMachine = {
  brand: 'boastfully',
  type: 'authentic incidentally lest',
  ratePerHour: 14734.66,
  latitude: 3299.72,
  longitude: 21013.48,
  status: 'AVAILABLE',
  createdDate: dayjs('2025-07-14T12:01'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
