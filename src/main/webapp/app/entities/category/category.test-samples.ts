import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 29542,
  name: 'whether',
  typeId: 1,
  imageUrl: 'https://example.com/sample.jpg',
};

export const sampleWithPartialData: ICategory = {
  id: 3254,
  name: 'link',
  description: 'fit',
  typeId: 1,
  imageUrl: 'https://example.com/sample.jpg',
};

export const sampleWithFullData: ICategory = {
  id: 9107,
  name: 'yawning helpfully atop',
  description: 'blah brr instead',
  //   image: '../fake-data/blob/hipster.png',
  //   imageContentType: 'unknown',
  typeId: 1,
  imageUrl: 'https://example.com/sample.jpg',
};

export const sampleWithNewData: NewCategory = {
  name: 'dependable mailbox',
  id: null,
  typeId: 1,
  imageUrl: 'https://example.com/sample.jpg',
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
