import dayjs from 'dayjs/esm';
import { IPartner } from 'app/entities/partner/partner.model';
import { IMachine } from 'app/entities/machine/machine.model';

export interface IAttachment {
  id: number;
  fileName?: string | null;
  fileType?: string | null;
  fileUrl?: string | null;
  uploadedDate?: dayjs.Dayjs | null;
  partner?: IPartner | null;
  machine?: IMachine | null;
}

export type NewAttachment = Omit<IAttachment, 'id'> & { id: null };
