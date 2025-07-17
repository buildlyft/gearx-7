import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IMachine } from 'app/entities/machine/machine.model';
import { MachineService } from 'app/entities/machine/service/machine.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IBooking } from '../booking.model';
import { BookingService } from '../service/booking.service';
import { BookingFormService } from './booking-form.service';

import { BookingUpdateComponent } from './booking-update.component';

describe('Booking Management Update Component', () => {
  let comp: BookingUpdateComponent;
  let fixture: ComponentFixture<BookingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookingFormService: BookingFormService;
  let bookingService: BookingService;
  let machineService: MachineService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), BookingUpdateComponent],
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
      .overrideTemplate(BookingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookingFormService = TestBed.inject(BookingFormService);
    bookingService = TestBed.inject(BookingService);
    machineService = TestBed.inject(MachineService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Machine query and add missing value', () => {
      const booking: IBooking = { id: 456 };
      const machine: IMachine = { id: 24792 };
      booking.machine = machine;

      const machineCollection: IMachine[] = [{ id: 9755 }];
      jest.spyOn(machineService, 'query').mockReturnValue(of(new HttpResponse({ body: machineCollection })));
      const additionalMachines = [machine];
      const expectedCollection: IMachine[] = [...additionalMachines, ...machineCollection];
      jest.spyOn(machineService, 'addMachineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ booking });
      comp.ngOnInit();

      expect(machineService.query).toHaveBeenCalled();
      expect(machineService.addMachineToCollectionIfMissing).toHaveBeenCalledWith(
        machineCollection,
        ...additionalMachines.map(expect.objectContaining),
      );
      expect(comp.machinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const booking: IBooking = { id: 456 };
      const customer: ICustomer = { id: 28675 };
      booking.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 25591 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ booking });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const booking: IBooking = { id: 456 };
      const machine: IMachine = { id: 18833 };
      booking.machine = machine;
      const customer: ICustomer = { id: 18315 };
      booking.customer = customer;

      activatedRoute.data = of({ booking });
      comp.ngOnInit();

      expect(comp.machinesSharedCollection).toContain(machine);
      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.booking).toEqual(booking);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBooking>>();
      const booking = { id: 123 };
      jest.spyOn(bookingFormService, 'getBooking').mockReturnValue(booking);
      jest.spyOn(bookingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ booking });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: booking }));
      saveSubject.complete();

      // THEN
      expect(bookingFormService.getBooking).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookingService.update).toHaveBeenCalledWith(expect.objectContaining(booking));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBooking>>();
      const booking = { id: 123 };
      jest.spyOn(bookingFormService, 'getBooking').mockReturnValue({ id: null });
      jest.spyOn(bookingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ booking: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: booking }));
      saveSubject.complete();

      // THEN
      expect(bookingFormService.getBooking).toHaveBeenCalled();
      expect(bookingService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBooking>>();
      const booking = { id: 123 };
      jest.spyOn(bookingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ booking });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookingService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMachine', () => {
      it('Should forward to machineService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(machineService, 'compareMachine');
        comp.compareMachine(entity, entity2);
        expect(machineService.compareMachine).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
