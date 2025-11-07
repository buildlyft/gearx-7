import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISubcategory, NewSubcategory } from '../subcategory.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISubcategory for edit and NewSubcategoryFormGroupInput for create.
 */
type SubcategoryFormGroupInput = ISubcategory | PartialWithRequiredKeyOf<NewSubcategory>;

type SubcategoryFormDefaults = Pick<NewSubcategory, 'id'>;

type SubcategoryFormGroupContent = {
  id: FormControl<ISubcategory['id'] | NewSubcategory['id']>;
  name: FormControl<ISubcategory['name']>;
  description: FormControl<ISubcategory['description']>;
  image: FormControl<ISubcategory['image']>;
  imageContentType: FormControl<ISubcategory['imageContentType']>;
  category: FormControl<ISubcategory['category']>;
};

export type SubcategoryFormGroup = FormGroup<SubcategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SubcategoryFormService {
  createSubcategoryFormGroup(subcategory: SubcategoryFormGroupInput = { id: null }): SubcategoryFormGroup {
    const subcategoryRawValue = {
      ...this.getFormDefaults(),
      ...subcategory,
    };
    return new FormGroup<SubcategoryFormGroupContent>({
      id: new FormControl(
        { value: subcategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(subcategoryRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(subcategoryRawValue.description),
      image: new FormControl(subcategoryRawValue.image),
      imageContentType: new FormControl(subcategoryRawValue.imageContentType),
      category: new FormControl(subcategoryRawValue.category),
    });
  }

  getSubcategory(form: SubcategoryFormGroup): ISubcategory | NewSubcategory {
    return form.getRawValue() as ISubcategory | NewSubcategory;
  }

  resetForm(form: SubcategoryFormGroup, subcategory: SubcategoryFormGroupInput): void {
    const subcategoryRawValue = { ...this.getFormDefaults(), ...subcategory };
    form.reset(
      {
        ...subcategoryRawValue,
        id: { value: subcategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SubcategoryFormDefaults {
    return {
      id: null,
    };
  }
}
