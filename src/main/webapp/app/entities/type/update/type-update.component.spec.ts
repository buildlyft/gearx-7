import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TypeUpdateComponent } from './type-update.component';
import { TypeFormService } from './type-form.service';
import { TypeService } from '../service/type.service';

describe('Type Management Update Component', () => {
  let comp: TypeUpdateComponent;
  let fixture: ComponentFixture<TypeUpdateComponent>;
  let service: TypeService;
  let formService: TypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TypeUpdateComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ type: { id: 123 } }) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TypeUpdateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TypeService);
    formService = TestBed.inject(TypeFormService);
  });

  it('Should update editForm on init', () => {
    comp.ngOnInit();
    expect(comp.type).toEqual(expect.objectContaining({ id: 123 }));
  });

  it('Should call update service on save for existing entity', () => {
    const saveSubject = new Subject<HttpResponse<any>>();
    jest.spyOn(formService, 'getType').mockReturnValue({ id: 123 });
    jest.spyOn(service, 'update').mockReturnValue(saveSubject);

    comp.save();
    expect(comp.isSaving).toEqual(true);

    saveSubject.next(new HttpResponse({ body: { id: 123 } }));
    saveSubject.complete();

    expect(service.update).toHaveBeenCalled();
  });
});
