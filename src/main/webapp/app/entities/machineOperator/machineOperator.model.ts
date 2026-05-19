export interface IMachineOperator {
  operatorId: number;
  machineId: number | null;
  driverName?: string | null;
  operatorContact?: string | null;
  address?: string | null;
  active?: boolean | null;
  licenseIssueDate?: string | null;
  imageUrl?: string | null;
  docUrl?: string | null;
  createdAt?: string | null;
}

export type NewMachineOperator = Omit<IMachineOperator, 'operatorId'> & {
  operatorId: null;
};
