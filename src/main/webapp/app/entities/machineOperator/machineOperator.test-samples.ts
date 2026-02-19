import { IMachineOperator, NewMachineOperator } from './machineOperator.model';

export const sampleWithRequiredData: IMachineOperator = {
  operatorId: 1,
  machineId: 101,
  driverName: 'Ravi Kumar',
  operatorContact: '9876543210',
  address: 'Hyderabad',
  active: true,
  licenseIssueDate: '2024-01-01',
  docUrl: 'http://localhost/docs/license.pdf',
  createdAt: '2024-01-01T10:00:00Z',
};

export const sampleWithPartialData: IMachineOperator = {
  operatorId: 2,
  machineId: 102,
  driverName: 'Suresh',
  operatorContact: '9123456780',
  active: false,
  licenseIssueDate: '2024-02-15',
};

export const sampleWithFullData: IMachineOperator = {
  operatorId: 3,
  machineId: 103,
  driverName: 'Mahesh',
  operatorContact: '9988776655',
  address: 'Vijayawada',
  active: true,
  licenseIssueDate: '2023-12-01',
  docUrl: 'http://localhost/docs/mahesh-license.pdf',
  createdAt: '2024-01-10T08:30:00Z',
};

export const sampleWithNewData: NewMachineOperator = {
  operatorId: null,
  machineId: 104,
  driverName: 'Ramesh',
  operatorContact: '9090909090',
  address: 'Guntur',
  active: true,
  licenseIssueDate: '2024-03-01',
  docUrl: null,
  createdAt: null,
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
Object.freeze(sampleWithNewData);
