import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPartner, NewPartner } from '../partner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPartner for edit and NewPartnerFormGroupInput for create.
 */
type PartnerFormGroupInput = IPartner | PartialWithRequiredKeyOf<NewPartner>;

type PartnerFormDefaults = Pick<NewPartner, 'id'>;

type PartnerFormGroupContent = {
  id: FormControl<IPartner['id'] | NewPartner['id']>;
  name: FormControl<IPartner['name']>;
  companyName: FormControl<IPartner['companyName']>;
  email: FormControl<IPartner['email']>;
  phone: FormControl<IPartner['phone']>;
  address: FormControl<IPartner['address']>;
  preferredContactTime: FormControl<IPartner['preferredContactTime']>;
  gstNumber: FormControl<IPartner['gstNumber']>;
  panNumber: FormControl<IPartner['panNumber']>;
};

export type PartnerFormGroup = FormGroup<PartnerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartnerFormService {
  createPartnerFormGroup(partner: PartnerFormGroupInput = { id: null }): PartnerFormGroup {
    const partnerRawValue = {
      ...this.getFormDefaults(),
      ...partner,
    };
    return new FormGroup<PartnerFormGroupContent>({
      id: new FormControl(
        { value: partnerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(partnerRawValue.name, {
        validators: [Validators.required],
      }),
      companyName: new FormControl(partnerRawValue.companyName),
      email: new FormControl(partnerRawValue.email),
      phone: new FormControl(partnerRawValue.phone, {
        validators: [Validators.required],
      }),
      address: new FormControl(partnerRawValue.address),
      preferredContactTime: new FormControl(partnerRawValue.preferredContactTime),
      gstNumber: new FormControl(partnerRawValue.gstNumber),
      panNumber: new FormControl(partnerRawValue.panNumber),
    });
  }

  getPartner(form: PartnerFormGroup): IPartner | NewPartner {
    return form.getRawValue() as IPartner | NewPartner;
  }

  resetForm(form: PartnerFormGroup, partner: PartnerFormGroupInput): void {
    const partnerRawValue = { ...this.getFormDefaults(), ...partner };
    form.reset(
      {
        ...partnerRawValue,
        id: { value: partnerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PartnerFormDefaults {
    return {
      id: null,
    };
  }
}
