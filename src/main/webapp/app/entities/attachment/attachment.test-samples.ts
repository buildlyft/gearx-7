import dayjs from 'dayjs/esm';

import { IAttachment, NewAttachment } from './attachment.model';

export const sampleWithRequiredData: IAttachment = {
  id: 10695,
};

export const sampleWithPartialData: IAttachment = {
  id: 18637,
  fileName: 'several sans sour',
};

export const sampleWithFullData: IAttachment = {
  id: 25839,
  fileName: 'that ew',
  fileType: 'ref',
  fileUrl: 'microwave descriptive violet',
  uploadedDate: dayjs('2025-07-14T06:08'),
};

export const sampleWithNewData: NewAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
