import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMachine } from 'app/entities/machine/machine.model';
import { MachineService } from 'app/entities/machine/service/machine.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';
import { BookingService } from '../service/booking.service';
import { IBooking } from '../booking.model';
import { BookingFormService, BookingFormGroup } from './booking-form.service';

@Component({
  standalone: true,
  selector: 'jhi-booking-update',
  templateUrl: './booking-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookingUpdateComponent implements OnInit {
  isSaving = false;
  booking: IBooking | null = null;
  bookingStatusValues = Object.keys(BookingStatus);

  machinesSharedCollection: IMachine[] = [];
  customersSharedCollection: ICustomer[] = [];

  editForm: BookingFormGroup = this.bookingFormService.createBookingFormGroup();

  constructor(
    protected bookingService: BookingService,
    protected bookingFormService: BookingFormService,
    protected machineService: MachineService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareMachine = (o1: IMachine | null, o2: IMachine | null): boolean => this.machineService.compareMachine(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      this.booking = booking;
      if (booking) {
        this.updateForm(booking);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const booking = this.bookingFormService.getBooking(this.editForm);
    if (booking.id !== null) {
      this.subscribeToSaveResponse(this.bookingService.update(booking));
    } else {
      this.subscribeToSaveResponse(this.bookingService.create(booking));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooking>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(booking: IBooking): void {
    this.booking = booking;
    this.bookingFormService.resetForm(this.editForm, booking);

    this.machinesSharedCollection = this.machineService.addMachineToCollectionIfMissing<IMachine>(
      this.machinesSharedCollection,
      booking.machine,
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      booking.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.machineService
      .query()
      .pipe(map((res: HttpResponse<IMachine[]>) => res.body ?? []))
      .pipe(map((machines: IMachine[]) => this.machineService.addMachineToCollectionIfMissing<IMachine>(machines, this.booking?.machine)))
      .subscribe((machines: IMachine[]) => (this.machinesSharedCollection = machines));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.booking?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
