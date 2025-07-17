import { IPartner, NewPartner } from './partner.model';

export const sampleWithRequiredData: IPartner = {
  id: 20560,
  name: 'uselessly',
  phone: '(526) 756-5179 x16431',
};

export const sampleWithPartialData: IPartner = {
  id: 23934,
  name: 'furthermore',
  companyName: 'chart',
  email: 'Bartholome73@gmail.com',
  phone: '1-407-534-6003 x28449',
  address: 'amusing',
  preferredContactTime: 'focused out pfft',
  gstNumber: 'whether break aside',
  panNumber: 'by honestly',
};

export const sampleWithFullData: IPartner = {
  id: 5999,
  name: 'loosely characteristic',
  companyName: 'fooey ambitious whoever',
  email: 'Dorris_Abernathy51@gmail.com',
  phone: '(337) 983-2251 x72404',
  address: 'gadzooks sweetly into',
  preferredContactTime: 'via duh foolishly',
  gstNumber: 'qua meh',
  panNumber: 'save phew how',
};

export const sampleWithNewData: NewPartner = {
  name: 'frayed',
  phone: '1-271-910-9429',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
