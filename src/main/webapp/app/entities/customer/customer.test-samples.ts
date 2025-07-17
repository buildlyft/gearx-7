import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 14512,
  name: 'graffiti',
  phone: '(494) 652-4291 x1868',
};

export const sampleWithPartialData: ICustomer = {
  id: 19121,
  name: 'likely athwart',
  phone: '(390) 606-9080 x740',
  address: 'swarm',
  pincode: 'towards',
};

export const sampleWithFullData: ICustomer = {
  id: 32658,
  name: 'about sane like',
  email: 'Sigrid.DAmore@hotmail.com',
  phone: '237-809-4993 x75881',
  address: 'anti era reserve',
  pincode: 'minus',
  location: 'behind',
};

export const sampleWithNewData: NewCustomer = {
  name: 'why',
  phone: '1-864-871-6886 x781',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
