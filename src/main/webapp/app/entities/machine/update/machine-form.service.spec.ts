import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../machine.test-samples';

import { MachineFormService } from './machine-form.service';

describe('Machine Form Service', () => {
  let service: MachineFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MachineFormService);
  });

  describe('Service methods', () => {
    describe('createMachineFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMachineFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            brand: expect.any(Object),
            type: expect.any(Object),
            tag: expect.any(Object),
            model: expect.any(Object),
            vinNumber: expect.any(Object),
            chassisNumber: expect.any(Object),
            description: expect.any(Object),
            capacityTon: expect.any(Object),
            ratePerHour: expect.any(Object),
            minimumUsageHours: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            transportationCharge: expect.any(Object),
            driverBatta: expect.any(Object),
            serviceabilityRangeKm: expect.any(Object),
            status: expect.any(Object),
            createdDate: expect.any(Object),
            partner: expect.any(Object),
          }),
        );
      });

      it('passing IMachine should create a new form with FormGroup', () => {
        const formGroup = service.createMachineFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            brand: expect.any(Object),
            type: expect.any(Object),
            tag: expect.any(Object),
            model: expect.any(Object),
            vinNumber: expect.any(Object),
            chassisNumber: expect.any(Object),
            description: expect.any(Object),
            capacityTon: expect.any(Object),
            ratePerHour: expect.any(Object),
            minimumUsageHours: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            transportationCharge: expect.any(Object),
            driverBatta: expect.any(Object),
            serviceabilityRangeKm: expect.any(Object),
            status: expect.any(Object),
            createdDate: expect.any(Object),
            partner: expect.any(Object),
          }),
        );
      });
    });

    describe('getMachine', () => {
      it('should return NewMachine for default Machine initial value', () => {
        const formGroup = service.createMachineFormGroup(sampleWithNewData);

        const machine = service.getMachine(formGroup) as any;

        expect(machine).toMatchObject(sampleWithNewData);
      });

      it('should return NewMachine for empty Machine initial value', () => {
        const formGroup = service.createMachineFormGroup();

        const machine = service.getMachine(formGroup) as any;

        expect(machine).toMatchObject({});
      });

      it('should return IMachine', () => {
        const formGroup = service.createMachineFormGroup(sampleWithRequiredData);

        const machine = service.getMachine(formGroup) as any;

        expect(machine).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMachine should not enable id FormControl', () => {
        const formGroup = service.createMachineFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMachine should disable id FormControl', () => {
        const formGroup = service.createMachineFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
