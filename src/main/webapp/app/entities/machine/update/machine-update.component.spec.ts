import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IPartner } from 'app/entities/partner/partner.model';
import { PartnerService } from 'app/entities/partner/service/partner.service';
import { IMachine } from '../machine.model';
import { MachineService } from '../service/machine.service';
import { MachineFormService } from './machine-form.service';

import { MachineUpdateComponent } from './machine-update.component';

describe('Machine Management Update Component', () => {
  let comp: MachineUpdateComponent;
  let fixture: ComponentFixture<MachineUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let machineFormService: MachineFormService;
  let machineService: MachineService;
  let userService: UserService;
  let partnerService: PartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), MachineUpdateComponent],
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
      .overrideTemplate(MachineUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MachineUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    machineFormService = TestBed.inject(MachineFormService);
    machineService = TestBed.inject(MachineService);
    userService = TestBed.inject(UserService);
    partnerService = TestBed.inject(PartnerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const machine: IMachine = { id: 456 };
      const user: IUser = { id: 15004 };
      machine.user = user;

      const userCollection: IUser[] = [{ id: 24504 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ machine });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Partner query and add missing value', () => {
      const machine: IMachine = { id: 456 };
      const partner: IPartner = { id: 18649 };
      machine.partner = partner;

      const partnerCollection: IPartner[] = [{ id: 13655 }];
      jest.spyOn(partnerService, 'query').mockReturnValue(of(new HttpResponse({ body: partnerCollection })));
      const additionalPartners = [partner];
      const expectedCollection: IPartner[] = [...additionalPartners, ...partnerCollection];
      jest.spyOn(partnerService, 'addPartnerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ machine });
      comp.ngOnInit();

      expect(partnerService.query).toHaveBeenCalled();
      expect(partnerService.addPartnerToCollectionIfMissing).toHaveBeenCalledWith(
        partnerCollection,
        ...additionalPartners.map(expect.objectContaining),
      );
      expect(comp.partnersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const machine: IMachine = { id: 456 };
      const user: IUser = { id: 26084 };
      machine.user = user;
      const partner: IPartner = { id: 15816 };
      machine.partner = partner;

      activatedRoute.data = of({ machine });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.partnersSharedCollection).toContain(partner);
      expect(comp.machine).toEqual(machine);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMachine>>();
      const machine = { id: 123 };
      jest.spyOn(machineFormService, 'getMachine').mockReturnValue(machine);
      jest.spyOn(machineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ machine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: machine }));
      saveSubject.complete();

      // THEN
      expect(machineFormService.getMachine).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(machineService.update).toHaveBeenCalledWith(expect.objectContaining(machine));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMachine>>();
      const machine = { id: 123 };
      jest.spyOn(machineFormService, 'getMachine').mockReturnValue({ id: null });
      jest.spyOn(machineService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ machine: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: machine }));
      saveSubject.complete();

      // THEN
      expect(machineFormService.getMachine).toHaveBeenCalled();
      expect(machineService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMachine>>();
      const machine = { id: 123 };
      jest.spyOn(machineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ machine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(machineService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePartner', () => {
      it('Should forward to partnerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partnerService, 'comparePartner');
        comp.comparePartner(entity, entity2);
        expect(partnerService.comparePartner).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
