import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TypeComponent } from './type.component';
import { TypeService } from '../service/type.service';

describe('Type Management Component', () => {
  let comp: TypeComponent;
  let fixture: ComponentFixture<TypeComponent>;
  let service: TypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([]), TypeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({ defaultSort: 'id,asc' }),
            queryParamMap: of(
              new Map([
                ['page', '1'],
                ['size', '1'],
                ['sort', 'id,desc'],
              ]),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TypeService);

    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers: new HttpHeaders(),
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    comp.ngOnInit();
    expect(service.query).toHaveBeenCalled();
    expect(comp.types?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
