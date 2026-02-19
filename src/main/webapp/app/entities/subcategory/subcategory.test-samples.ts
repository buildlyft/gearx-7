import { ISubcategory, NewSubcategory } from './subcategory.model';

export const sampleWithRequiredData: ISubcategory = {
  id: 13546,
  name: 'on afterwards regulation',
  categoryId: 1,
};

export const sampleWithPartialData: ISubcategory = {
  id: 4563,
  name: 'fibroblast',
  description: 'miserably',
  /* image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown', */
  categoryId: 1,
  imageUrl: 'https://cloudinary.com/sample.jpg',
};

export const sampleWithFullData: ISubcategory = {
  id: 27915,
  name: 'beneath once',
  description: 'fearless',
  /*  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown', */
  categoryId: 2,
  imageUrl: 'https://cloudinary.com/sample.jpg',
};

export const sampleWithNewData: NewSubcategory = {
  name: 'coaxingly',
  categoryId: 1,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
