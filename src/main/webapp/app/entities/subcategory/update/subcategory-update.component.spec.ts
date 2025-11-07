import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { SubcategoryService } from '../service/subcategory.service';
import { ISubcategory } from '../subcategory.model';
import { SubcategoryFormService } from './subcategory-form.service';

import { SubcategoryUpdateComponent } from './subcategory-update.component';

describe('Subcategory Management Update Component', () => {
  let comp: SubcategoryUpdateComponent;
  let fixture: ComponentFixture<SubcategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subcategoryFormService: SubcategoryFormService;
  let subcategoryService: SubcategoryService;
  let categoryService: CategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SubcategoryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SubcategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubcategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subcategoryFormService = TestBed.inject(SubcategoryFormService);
    subcategoryService = TestBed.inject(SubcategoryService);
    categoryService = TestBed.inject(CategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Category query and add missing value', () => {
      const subcategory: ISubcategory = { id: 456 };
      const category: ICategory = { id: 27247 };
      subcategory.category = category;

      const categoryCollection: ICategory[] = [{ id: 23095 }];
      jest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      jest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subcategory });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(expect.objectContaining),
      );
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const subcategory: ISubcategory = { id: 456 };
      const category: ICategory = { id: 6447 };
      subcategory.category = category;

      activatedRoute.data = of({ subcategory });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection).toContain(category);
      expect(comp.subcategory).toEqual(subcategory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubcategory>>();
      const subcategory = { id: 123 };
      jest.spyOn(subcategoryFormService, 'getSubcategory').mockReturnValue(subcategory);
      jest.spyOn(subcategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subcategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subcategory }));
      saveSubject.complete();

      // THEN
      expect(subcategoryFormService.getSubcategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(subcategoryService.update).toHaveBeenCalledWith(expect.objectContaining(subcategory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubcategory>>();
      const subcategory = { id: 123 };
      jest.spyOn(subcategoryFormService, 'getSubcategory').mockReturnValue({ id: null });
      jest.spyOn(subcategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subcategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subcategory }));
      saveSubject.complete();

      // THEN
      expect(subcategoryFormService.getSubcategory).toHaveBeenCalled();
      expect(subcategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubcategory>>();
      const subcategory = { id: 123 };
      jest.spyOn(subcategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subcategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subcategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('Should forward to categoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
