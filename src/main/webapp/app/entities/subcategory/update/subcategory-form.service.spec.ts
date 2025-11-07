import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../subcategory.test-samples';

import { SubcategoryFormService } from './subcategory-form.service';

describe('Subcategory Form Service', () => {
  let service: SubcategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubcategoryFormService);
  });

  describe('Service methods', () => {
    describe('createSubcategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSubcategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            image: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });

      it('passing ISubcategory should create a new form with FormGroup', () => {
        const formGroup = service.createSubcategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            image: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });
    });

    describe('getSubcategory', () => {
      it('should return NewSubcategory for default Subcategory initial value', () => {
        const formGroup = service.createSubcategoryFormGroup(sampleWithNewData);

        const subcategory = service.getSubcategory(formGroup) as any;

        expect(subcategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewSubcategory for empty Subcategory initial value', () => {
        const formGroup = service.createSubcategoryFormGroup();

        const subcategory = service.getSubcategory(formGroup) as any;

        expect(subcategory).toMatchObject({});
      });

      it('should return ISubcategory', () => {
        const formGroup = service.createSubcategoryFormGroup(sampleWithRequiredData);

        const subcategory = service.getSubcategory(formGroup) as any;

        expect(subcategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISubcategory should not enable id FormControl', () => {
        const formGroup = service.createSubcategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSubcategory should disable id FormControl', () => {
        const formGroup = service.createSubcategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
