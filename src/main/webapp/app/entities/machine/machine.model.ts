import dayjs from 'dayjs/esm';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { IPartner } from 'app/entities/partner/partner.model';
import { MachineStatus } from 'app/entities/enumerations/machine-status.model';

export interface IMachine {
  id: number;
  brand?: string | null;
  type?: string | null;
  tag?: string | null;
  model?: string | null;
  vinNumber?: string | null;
  chassisNumber?: string | null;
  description?: string | null;
  capacityTon?: number | null;
  ratePerHour?: number | null;
  minimumUsageHours?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  transportationCharge?: number | null;
  driverBatta?: number | null;
  serviceabilityRangeKm?: number | null;
  status?: keyof typeof MachineStatus | null;
  createdDate?: dayjs.Dayjs | null;
  attachments?: IAttachment[] | null;
  partner?: IPartner | null;
}

export type NewMachine = Omit<IMachine, 'id'> & { id: null };
