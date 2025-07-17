import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PartnerService } from '../service/partner.service';
import { IPartner } from '../partner.model';
import { PartnerFormService } from './partner-form.service';

import { PartnerUpdateComponent } from './partner-update.component';

describe('Partner Management Update Component', () => {
  let comp: PartnerUpdateComponent;
  let fixture: ComponentFixture<PartnerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partnerFormService: PartnerFormService;
  let partnerService: PartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PartnerUpdateComponent],
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
      .overrideTemplate(PartnerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartnerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partnerFormService = TestBed.inject(PartnerFormService);
    partnerService = TestBed.inject(PartnerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const partner: IPartner = { id: 456 };

      activatedRoute.data = of({ partner });
      comp.ngOnInit();

      expect(comp.partner).toEqual(partner);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartner>>();
      const partner = { id: 123 };
      jest.spyOn(partnerFormService, 'getPartner').mockReturnValue(partner);
      jest.spyOn(partnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partner }));
      saveSubject.complete();

      // THEN
      expect(partnerFormService.getPartner).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partnerService.update).toHaveBeenCalledWith(expect.objectContaining(partner));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartner>>();
      const partner = { id: 123 };
      jest.spyOn(partnerFormService, 'getPartner').mockReturnValue({ id: null });
      jest.spyOn(partnerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partner: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partner }));
      saveSubject.complete();

      // THEN
      expect(partnerFormService.getPartner).toHaveBeenCalled();
      expect(partnerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartner>>();
      const partner = { id: 123 };
      jest.spyOn(partnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partnerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
