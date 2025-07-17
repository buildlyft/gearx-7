import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IPartner } from 'app/entities/partner/partner.model';
import { PartnerService } from 'app/entities/partner/service/partner.service';
import { IMachine } from 'app/entities/machine/machine.model';
import { MachineService } from 'app/entities/machine/service/machine.service';
import { IAttachment } from '../attachment.model';
import { AttachmentService } from '../service/attachment.service';
import { AttachmentFormService } from './attachment-form.service';

import { AttachmentUpdateComponent } from './attachment-update.component';

describe('Attachment Management Update Component', () => {
  let comp: AttachmentUpdateComponent;
  let fixture: ComponentFixture<AttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let attachmentFormService: AttachmentFormService;
  let attachmentService: AttachmentService;
  let partnerService: PartnerService;
  let machineService: MachineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), AttachmentUpdateComponent],
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
      .overrideTemplate(AttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    attachmentFormService = TestBed.inject(AttachmentFormService);
    attachmentService = TestBed.inject(AttachmentService);
    partnerService = TestBed.inject(PartnerService);
    machineService = TestBed.inject(MachineService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Partner query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const partner: IPartner = { id: 20591 };
      attachment.partner = partner;

      const partnerCollection: IPartner[] = [{ id: 9353 }];
      jest.spyOn(partnerService, 'query').mockReturnValue(of(new HttpResponse({ body: partnerCollection })));
      const additionalPartners = [partner];
      const expectedCollection: IPartner[] = [...additionalPartners, ...partnerCollection];
      jest.spyOn(partnerService, 'addPartnerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(partnerService.query).toHaveBeenCalled();
      expect(partnerService.addPartnerToCollectionIfMissing).toHaveBeenCalledWith(
        partnerCollection,
        ...additionalPartners.map(expect.objectContaining),
      );
      expect(comp.partnersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Machine query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const machine: IMachine = { id: 15810 };
      attachment.machine = machine;

      const machineCollection: IMachine[] = [{ id: 25277 }];
      jest.spyOn(machineService, 'query').mockReturnValue(of(new HttpResponse({ body: machineCollection })));
      const additionalMachines = [machine];
      const expectedCollection: IMachine[] = [...additionalMachines, ...machineCollection];
      jest.spyOn(machineService, 'addMachineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(machineService.query).toHaveBeenCalled();
      expect(machineService.addMachineToCollectionIfMissing).toHaveBeenCalledWith(
        machineCollection,
        ...additionalMachines.map(expect.objectContaining),
      );
      expect(comp.machinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const attachment: IAttachment = { id: 456 };
      const partner: IPartner = { id: 18557 };
      attachment.partner = partner;
      const machine: IMachine = { id: 32009 };
      attachment.machine = machine;

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(comp.partnersSharedCollection).toContain(partner);
      expect(comp.machinesSharedCollection).toContain(machine);
      expect(comp.attachment).toEqual(attachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentFormService, 'getAttachment').mockReturnValue(attachment);
      jest.spyOn(attachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: attachment }));
      saveSubject.complete();

      // THEN
      expect(attachmentFormService.getAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(attachmentService.update).toHaveBeenCalledWith(expect.objectContaining(attachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentFormService, 'getAttachment').mockReturnValue({ id: null });
      jest.spyOn(attachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: attachment }));
      saveSubject.complete();

      // THEN
      expect(attachmentFormService.getAttachment).toHaveBeenCalled();
      expect(attachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(attachmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePartner', () => {
      it('Should forward to partnerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partnerService, 'comparePartner');
        comp.comparePartner(entity, entity2);
        expect(partnerService.comparePartner).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMachine', () => {
      it('Should forward to machineService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(machineService, 'compareMachine');
        comp.compareMachine(entity, entity2);
        expect(machineService.compareMachine).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
