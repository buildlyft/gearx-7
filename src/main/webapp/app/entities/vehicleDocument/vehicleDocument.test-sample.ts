import dayjs from 'dayjs/esm';
import { IVehicleDocument, NewVehicleDocument } from './vehicleDocument.model';

export const sampleWithRequiredData: IVehicleDocument = {
  id: 1,
};

export const sampleWithPartialData: IVehicleDocument = {
  id: 2,
  docType: 'RC',
  fileName: 'rc.pdf',
};

export const sampleWithFullData: IVehicleDocument = {
  id: 3,
  docType: 'INSURANCE',
  fileName: 'insurance.pdf',
  fileUrl: 'https://cloudinary.com/sample/insurance.pdf',
  contentType: 'application/pdf',
  size: 2048,
  uploadedAt: dayjs('2026-01-01T10:00:00Z'),
  uploadedBy: 'admin',
};

export const sampleWithNewData: NewVehicleDocument = {
  id: null,
  docType: 'PERMIT',
  fileName: 'permit.pdf',
  fileUrl: 'https://cloudinary.com/sample/permit.pdf',
  contentType: 'application/pdf',
  size: 1024,
  uploadedAt: dayjs('2026-01-02T12:00:00Z'),
  uploadedBy: 'partner',
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
Object.freeze(sampleWithNewData);
