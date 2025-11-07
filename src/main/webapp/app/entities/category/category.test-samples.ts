import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 29542,
  name: 'whether',
};

export const sampleWithPartialData: ICategory = {
  id: 3254,
  name: 'link',
  description: 'fit',
};

export const sampleWithFullData: ICategory = {
  id: 9107,
  name: 'yawning helpfully atop',
  description: 'blah brr instead',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithNewData: NewCategory = {
  name: 'dependable mailbox',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
