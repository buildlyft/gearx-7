import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMachineOperator, NewMachineOperator } from '../machineOperator.model';

/**
 * Partial type with required key is used for form input.
 */
type PartialWithRequiredKeyOf<T extends { operatorId: unknown }> = Partial<Omit<T, 'operatorId'>> & { operatorId: T['operatorId'] };

/**
 * Input type for create/reset.
 */
type MachineOperatorFormGroupInput = IMachineOperator | PartialWithRequiredKeyOf<NewMachineOperator>;

/**
 * Default values for new form.
 */
type MachineOperatorFormDefaults = Pick<NewMachineOperator, 'operatorId' | 'active'>;

/**
 * Form group content type.
 */
type MachineOperatorFormGroupContent = {
  operatorId: FormControl<IMachineOperator['operatorId'] | NewMachineOperator['operatorId']>;
  machineId: FormControl<number | null>;
  driverName: FormControl<IMachineOperator['driverName']>;
  operatorContact: FormControl<IMachineOperator['operatorContact']>;
  address: FormControl<IMachineOperator['address']>;
  active: FormControl<IMachineOperator['active']>;
  licenseIssueDate: FormControl<IMachineOperator['licenseIssueDate']>;
  docUrl: FormControl<IMachineOperator['docUrl']>;
  createdAt: FormControl<IMachineOperator['createdAt']>;
};

export type MachineOperatorFormGroup = FormGroup<MachineOperatorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MachineOperatorFormService {
  createMachineOperatorFormGroup(machineOperator: MachineOperatorFormGroupInput = { operatorId: null }): MachineOperatorFormGroup {
    const machineOperatorRawValue = {
      ...this.getFormDefaults(),
      ...machineOperator,
    };

    return new FormGroup<MachineOperatorFormGroupContent>({
      operatorId: new FormControl(
        { value: machineOperatorRawValue.operatorId, disabled: true },
        {
          nonNullable: true,
          validators: [],
        },
      ),
      machineId: new FormControl<number | null>(machineOperatorRawValue.machineId ?? null, {
        validators: [Validators.required],
      }),
      driverName: new FormControl(machineOperatorRawValue.driverName, {
        validators: [Validators.required, Validators.minLength(3)],
      }),
      operatorContact: new FormControl(machineOperatorRawValue.operatorContact, {
        validators: [Validators.required],
      }),
      address: new FormControl(machineOperatorRawValue.address),
      active: new FormControl(machineOperatorRawValue.active ?? true),
      licenseIssueDate: new FormControl(machineOperatorRawValue.licenseIssueDate, {
        validators: [Validators.required],
      }),
      docUrl: new FormControl(machineOperatorRawValue.docUrl),
      createdAt: new FormControl(machineOperatorRawValue.createdAt),
    });
  }

  /**
   * Extract entity from form
   */
  getMachineOperator(form: MachineOperatorFormGroup): IMachineOperator | NewMachineOperator {
    return form.getRawValue() as IMachineOperator | NewMachineOperator;
  }

  /**
   * Reset form with entity
   */
  resetForm(form: MachineOperatorFormGroup, machineOperator: MachineOperatorFormGroupInput): void {
    const machineOperatorRawValue = {
      ...this.getFormDefaults(),
      ...machineOperator,
    };

    form.reset({
      ...machineOperatorRawValue,
      operatorId: {
        value: machineOperatorRawValue.operatorId,
        disabled: true,
      },
    } as any);
  }

  /**
   * Default values
   */
  private getFormDefaults(): MachineOperatorFormDefaults {
    return {
      operatorId: null,
      active: true,
    };
  }
}
