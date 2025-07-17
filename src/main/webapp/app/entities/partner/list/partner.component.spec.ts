import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PartnerService } from '../service/partner.service';

import { PartnerComponent } from './partner.component';

describe('Partner Management Component', () => {
  let comp: PartnerComponent;
  let fixture: ComponentFixture<PartnerComponent>;
  let service: PartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'partner', component: PartnerComponent }]),
        HttpClientTestingModule,
        PartnerComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(PartnerComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartnerComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PartnerService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.partners?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to partnerService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPartnerIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPartnerIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
