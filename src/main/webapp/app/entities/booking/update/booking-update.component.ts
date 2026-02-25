import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IMachine } from 'app/entities/machine/machine.model';
import { MachineService } from 'app/entities/machine/service/machine.service';
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
  customerLat?: number;
  customerLong?: number;
  bookingStatusValues = Object.keys(BookingStatus);

  usersSharedCollection: IUser[] = [];
  machinesSharedCollection: IMachine[] = [];

  editForm: BookingFormGroup = this.bookingFormService.createBookingFormGroup();

  constructor(
    protected bookingService: BookingService,
    protected bookingFormService: BookingFormService,
    protected userService: UserService,
    protected machineService: MachineService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareMachine = (o1: IMachine | null, o2: IMachine | null): boolean => this.machineService.compareMachine(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      this.booking = booking;
      if (booking) {
        this.updateForm(booking);
      }

      this.loadRelationshipsOptions();
    });
    this.getCurrentLocation();
  }

  previousState(): void {
    window.history.back();
  }

  getCurrentLocation(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        position => {
          this.customerLat = position.coords.latitude;
          this.customerLong = position.coords.longitude;

          this.editForm.patchValue({
            customerLat: this.customerLat,
            customerLong: this.customerLong,
          });
        },
        error => {
          console.error('Error getting location:', error);
        },
      );
    } else {
      console.warn('Geolocation is not supported by this browser.');
    }
  }

  save(): void {
    this.isSaving = true;
    const booking = this.bookingFormService.getBooking(this.editForm);

    if (!booking.customerLat && this.customerLat) {
      booking.customerLat = this.customerLat;
    }

    if (!booking.customerLong && this.customerLong) {
      booking.customerLong = this.customerLong;
    }

    console.log('Final Booking Payload:', booking);

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

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, booking.user);
    this.machinesSharedCollection = this.machineService.addMachineToCollectionIfMissing<IMachine>(
      this.machinesSharedCollection,
      booking.machine,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.booking?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.machineService
      .query()
      .pipe(map((res: HttpResponse<IMachine[]>) => res.body ?? []))
      .pipe(map((machines: IMachine[]) => this.machineService.addMachineToCollectionIfMissing<IMachine>(machines, this.booking?.machine)))
      .subscribe((machines: IMachine[]) => (this.machinesSharedCollection = machines));
  }
}
