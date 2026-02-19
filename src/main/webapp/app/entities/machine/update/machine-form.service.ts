import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMachine, NewMachine } from '../machine.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMachine for edit and NewMachineFormGroupInput for create.
 */
type MachineFormGroupInput = IMachine | PartialWithRequiredKeyOf<NewMachine>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMachine | NewMachine> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type MachineFormRawValue = FormValueOf<IMachine>;

type NewMachineFormRawValue = FormValueOf<NewMachine>;

type MachineFormDefaults = Pick<NewMachine, 'id' | 'createdDate' | 'typeId' | 'categoryId' | 'subcategoryId'>;

type MachineFormGroupContent = {
  id: FormControl<MachineFormRawValue['id'] | NewMachine['id']>;
  brand: FormControl<MachineFormRawValue['brand']>;
  type: FormControl<MachineFormRawValue['type']>;
  tag: FormControl<MachineFormRawValue['tag']>;
  model: FormControl<MachineFormRawValue['model']>;
  vinNumber: FormControl<MachineFormRawValue['vinNumber']>;
  chassisNumber: FormControl<MachineFormRawValue['chassisNumber']>;
  description: FormControl<MachineFormRawValue['description']>;
  capacityTon: FormControl<MachineFormRawValue['capacityTon']>;
  ratePerHour: FormControl<MachineFormRawValue['ratePerHour']>;
  ratePerDay: FormControl<MachineFormRawValue['ratePerDay']>;
  minimumUsageHours: FormControl<MachineFormRawValue['minimumUsageHours']>;
  latitude: FormControl<MachineFormRawValue['latitude']>;
  longitude: FormControl<MachineFormRawValue['longitude']>;
  transportationCharge: FormControl<MachineFormRawValue['transportationCharge']>;
  driverBatta: FormControl<MachineFormRawValue['driverBatta']>;
  serviceabilityRangeKm: FormControl<MachineFormRawValue['serviceabilityRangeKm']>;
  status: FormControl<MachineFormRawValue['status']>;
  createdDate: FormControl<MachineFormRawValue['createdDate']>;
  mfgDate: FormControl<MachineFormRawValue['mfgDate']>;
  typeId: FormControl<MachineFormRawValue['typeId']>;
  categoryId: FormControl<MachineFormRawValue['categoryId']>;
  subcategoryId: FormControl<MachineFormRawValue['subcategoryId']>;
  user: FormControl<MachineFormRawValue['user']>;
};

export type MachineFormGroup = FormGroup<MachineFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MachineFormService {
  createMachineFormGroup(machine: MachineFormGroupInput = { id: null }): MachineFormGroup {
    const machineRawValue = this.convertMachineToMachineRawValue({
      ...this.getFormDefaults(),
      ...machine,
    });
    return new FormGroup<MachineFormGroupContent>({
      id: new FormControl(
        { value: machineRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      brand: new FormControl(machineRawValue.brand, {
        validators: [Validators.required],
      }),
      type: new FormControl(machineRawValue.type, {
        validators: [Validators.required],
      }),
      tag: new FormControl(machineRawValue.tag),
      model: new FormControl(machineRawValue.model),
      vinNumber: new FormControl(machineRawValue.vinNumber),
      chassisNumber: new FormControl(machineRawValue.chassisNumber),
      description: new FormControl(machineRawValue.description),
      capacityTon: new FormControl(machineRawValue.capacityTon),
      ratePerHour: new FormControl(machineRawValue.ratePerHour, {
        validators: [Validators.required],
      }),
      ratePerDay: new FormControl(machineRawValue.ratePerDay ?? null, {
        validators: [Validators.required],
      }),
      minimumUsageHours: new FormControl(machineRawValue.minimumUsageHours),
      latitude: new FormControl(machineRawValue.latitude, {
        validators: [Validators.required],
      }),
      longitude: new FormControl(machineRawValue.longitude, {
        validators: [Validators.required],
      }),
      transportationCharge: new FormControl(machineRawValue.transportationCharge),
      driverBatta: new FormControl(machineRawValue.driverBatta),
      serviceabilityRangeKm: new FormControl(machineRawValue.serviceabilityRangeKm),
      status: new FormControl(machineRawValue.status, {
        validators: [Validators.required],
      }),
      createdDate: new FormControl(machineRawValue.createdDate, {
        validators: [Validators.required],
      }),
      mfgDate: new FormControl(machineRawValue.mfgDate),
      typeId: new FormControl(machineRawValue.typeId, {
        validators: [Validators.required],
      }),
      categoryId: new FormControl(machineRawValue.categoryId, {
        validators: [Validators.required],
      }),
      subcategoryId: new FormControl(machineRawValue.subcategoryId, {
        validators: [Validators.required],
      }),

      user: new FormControl(machineRawValue.user),
    });
  }

  getMachine(form: MachineFormGroup): IMachine | NewMachine {
    return this.convertMachineRawValueToMachine(form.getRawValue() as MachineFormRawValue | NewMachineFormRawValue);
  }

  resetForm(form: MachineFormGroup, machine: MachineFormGroupInput): void {
    const machineRawValue = this.convertMachineToMachineRawValue({ ...this.getFormDefaults(), ...machine });
    form.reset(
      {
        ...machineRawValue,
        id: { value: machineRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MachineFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      typeId: null,
      categoryId: null,
      subcategoryId: null,
    };
  }

  private convertMachineRawValueToMachine(rawMachine: MachineFormRawValue | NewMachineFormRawValue): IMachine | NewMachine {
    return {
      ...rawMachine,
      createdDate: dayjs(rawMachine.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertMachineToMachineRawValue(
    machine: IMachine | (Partial<NewMachine> & MachineFormDefaults),
  ): MachineFormRawValue | PartialWithRequiredKeyOf<NewMachineFormRawValue> {
    return {
      ...machine,
      createdDate: machine.createdDate ? machine.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
