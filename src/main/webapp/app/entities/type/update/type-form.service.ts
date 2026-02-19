import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IType, NewType } from '../type.model';

@Injectable({ providedIn: 'root' })
export class TypeFormService {
  createTypeFormGroup(type: IType | NewType = { id: null }): FormGroup {
    return new FormGroup({
      id: new FormControl({ value: type.id, disabled: true }),
      typeName: new FormControl(type.typeName, [Validators.required]),
      imageUrl: new FormControl(null),
    });
  }

  getType(form: FormGroup): IType | NewType {
    return form.getRawValue() as IType | NewType;
  }

  resetForm(form: FormGroup, type: IType | NewType): void {
    form.reset({
      ...type,
      id: { value: type.id, disabled: true },
    });
  }
}
