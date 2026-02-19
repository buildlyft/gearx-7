import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CategoryService } from '../service/category.service';
import { ICategory } from '../category.model';
import { CategoryFormService } from './category-form.service';
import { CategoryUpdateComponent } from './category-update.component';
import { TypeService } from 'app/entities/type/service/type.service';

describe('Category Management Update Component', () => {
  let comp: CategoryUpdateComponent;
  let fixture: ComponentFixture<CategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let categoryFormService: CategoryFormService;
  let categoryService: CategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CategoryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
        {
          provide: TypeService,
          useValue: {
            query: jest.fn(() => of({ body: [] })),
          },
        },
      ],
    })
      .overrideTemplate(CategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoryFormService = TestBed.inject(CategoryFormService);
    categoryService = TestBed.inject(CategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const category: ICategory = { id: 456, typeId: 1 };

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(comp.category).toEqual(category);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      const saveSubject = new Subject<HttpResponse<ICategory>>();
      const category = { id: 123, typeId: 1 };

      jest.spyOn(categoryFormService, 'getCategory').mockReturnValue(category as any);
      jest.spyOn(categoryService, 'updateMultipart').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      comp.save();
      expect(comp.isSaving).toEqual(true);

      saveSubject.next(new HttpResponse({ body: category }));
      saveSubject.complete();

      expect(categoryService.updateMultipart).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      const saveSubject = new Subject<HttpResponse<ICategory>>();
      const category = { id: 123, typeId: 1 };

      jest.spyOn(categoryFormService, 'getCategory').mockReturnValue({ id: null, typeId: 1 } as any);
      jest.spyOn(categoryService, 'createMultipart').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');

      activatedRoute.data = of({ category: null });
      comp.ngOnInit();

      comp.save();
      expect(comp.isSaving).toEqual(true);

      saveSubject.next(new HttpResponse({ body: category }));
      saveSubject.complete();

      expect(categoryService.createMultipart).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
    });

    it('Should set isSaving to false on error', () => {
      const saveSubject = new Subject<HttpResponse<ICategory>>();

      jest.spyOn(categoryFormService, 'getCategory').mockReturnValue({ id: 123, typeId: 1 } as any);
      jest.spyOn(categoryService, 'updateMultipart').mockReturnValue(saveSubject);

      activatedRoute.data = of({ category: { id: 123, typeId: 1 } });
      comp.ngOnInit();

      comp.save();
      expect(comp.isSaving).toEqual(true);

      saveSubject.error('error');

      expect(categoryService.updateMultipart).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
    });
  });
});
