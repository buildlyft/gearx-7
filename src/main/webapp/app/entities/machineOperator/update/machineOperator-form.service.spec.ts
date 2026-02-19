import { TestBed } from '@angular/core/testing';
import { FormGroup } from '@angular/forms';

import { MachineOperatorFormService } from './machineOperator-form.service';
import { IMachineOperator } from '../machineOperator.model';

describe('MachineOperator Form Service', () => {
  let service: MachineOperatorFormService;

  const sampleWithRequiredData: IMachineOperator = {
    operatorId: 1,
    machineId: 123,
    driverName: 'Ravi',
    operatorContact: '9876543210',
    address: 'Hyderabad',
    licenseIssueDate: '2024-01-01',
    docUrl: 'test-url',
    createdAt: '2024-01-01T00:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MachineOperatorFormService);
  });

  // ---------------------------------------------------
  // CREATE FORM GROUP
  // ---------------------------------------------------
  describe('createMachineOperatorFormGroup', () => {
    it('should create a form with all controls', () => {
      const formGroup = service.createMachineOperatorFormGroup();

      expect(formGroup.controls).toEqual(
        jasmine.objectContaining({
          operatorId: jasmine.any(Object),
          machineId: jasmine.any(Object),
          driverName: jasmine.any(Object),
          operatorContact: jasmine.any(Object),
          address: jasmine.any(Object),
          licenseIssueDate: jasmine.any(Object),
          docUrl: jasmine.any(Object),
          createdAt: jasmine.any(Object),
        }),
      );
    });
  });

  // ---------------------------------------------------
  // GET MACHINE OPERATOR
  // ---------------------------------------------------
  describe('getMachineOperator', () => {
    it('should return NewMachineOperator for default form', () => {
      const formGroup = service.createMachineOperatorFormGroup();

      const operator = service.getMachineOperator(formGroup);

      expect(operator).toMatchObject({});
    });

    it('should return IMachineOperator when form initialized with data', () => {
      const formGroup = service.createMachineOperatorFormGroup(sampleWithRequiredData);

      const operator = service.getMachineOperator(formGroup);

      expect(operator).toMatchObject(sampleWithRequiredData);
    });
  });

  // ---------------------------------------------------
  // RESET FORM
  // ---------------------------------------------------
  describe('resetForm', () => {
    it('should keep operatorId disabled when resetting with existing entity', () => {
      const formGroup = service.createMachineOperatorFormGroup();

      service.resetForm(formGroup, sampleWithRequiredData);

      expect(formGroup.controls.operatorId.disabled).toBeTrue();
    });

    it('should keep operatorId disabled when resetting with new entity', () => {
      const formGroup = service.createMachineOperatorFormGroup(sampleWithRequiredData);

      service.resetForm(formGroup, { operatorId: null });

      expect(formGroup.controls.operatorId.disabled).toBeTrue();
    });
  });
});
