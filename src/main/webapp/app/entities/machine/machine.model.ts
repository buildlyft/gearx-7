import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
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
  ratePerDay?: number | null;
  minimumUsageHours?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  transportationCharge?: number | null;
  driverBatta?: number | null;
  serviceabilityRangeKm?: number | null;
  status?: keyof typeof MachineStatus | null;
  createdDate?: dayjs.Dayjs | null;
  categoryId?: number | null;
  subcategoryId?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewMachine = Omit<IMachine, 'id'> & { id: null };
