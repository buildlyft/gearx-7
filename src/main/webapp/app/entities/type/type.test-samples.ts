import { IType, NewType } from './type.model';

export const sampleWithRequiredData: IType = {
  id: 1,
  typeName: 'Heavy Machinery',
  imageUrl: 'https://example.com/image.jpg',
};

export const sampleWithPartialData: IType = {
  id: 2,
  typeName: 'Construction',
  imageUrl: 'https://example.com/construction.jpg',
};

export const sampleWithFullData: IType = {
  id: 3,
  typeName: 'Agriculture',
  imageUrl: 'https://example.com/agriculture.jpg',
  categories: [],
};

export const sampleWithNewData: NewType = {
  id: null,
  typeName: 'Transport',
  imageUrl: 'https://example.com/transport.jpg',
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
Object.freeze(sampleWithNewData);
