import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBooking, NewBooking } from '../booking.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBooking for edit and NewBookingFormGroupInput for create.
 */
type BookingFormGroupInput = IBooking | PartialWithRequiredKeyOf<NewBooking>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBooking | NewBooking> = Omit<T, 'startDateTime' | 'endDateTime' | 'createdDate'> & {
  startDateTime?: string | null;
  endDateTime?: string | null;
  createdDate?: string | null;
};

type BookingFormRawValue = FormValueOf<IBooking>;

type NewBookingFormRawValue = FormValueOf<NewBooking>;

type BookingFormDefaults = Pick<NewBooking, 'id' | 'startDateTime' | 'endDateTime' | 'createdDate'>;

type BookingFormGroupContent = {
  id: FormControl<BookingFormRawValue['id'] | NewBooking['id']>;
  startDateTime: FormControl<BookingFormRawValue['startDateTime']>;
  endDateTime: FormControl<BookingFormRawValue['endDateTime']>;
  status: FormControl<BookingFormRawValue['status']>;
  additionalDetails: FormControl<BookingFormRawValue['additionalDetails']>;
  worksiteImageUrl: FormControl<BookingFormRawValue['worksiteImageUrl']>;
  customerLat: FormControl<BookingFormRawValue['customerLat']>;
  customerLong: FormControl<BookingFormRawValue['customerLong']>;
  createdDate: FormControl<BookingFormRawValue['createdDate']>;
  user: FormControl<BookingFormRawValue['user']>;
};

export type BookingFormGroup = FormGroup<BookingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingFormService {
  createBookingFormGroup(booking: BookingFormGroupInput = { id: null }): BookingFormGroup {
    const bookingRawValue = this.convertBookingToBookingRawValue({
      ...this.getFormDefaults(),
      ...booking,
    });
    return new FormGroup<BookingFormGroupContent>({
      id: new FormControl(
        { value: bookingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startDateTime: new FormControl(bookingRawValue.startDateTime, {
        validators: [Validators.required],
      }),
      endDateTime: new FormControl(bookingRawValue.endDateTime, {
        validators: [Validators.required],
      }),
      status: new FormControl(bookingRawValue.status, {
        validators: [Validators.required],
      }),
      additionalDetails: new FormControl(bookingRawValue.additionalDetails),
      worksiteImageUrl: new FormControl(bookingRawValue.worksiteImageUrl),
      customerLat: new FormControl(bookingRawValue.customerLat),
      customerLong: new FormControl(bookingRawValue.customerLong),
      createdDate: new FormControl(bookingRawValue.createdDate),
      user: new FormControl(bookingRawValue.user),
    });
  }

  getBooking(form: BookingFormGroup): IBooking | NewBooking {
    return this.convertBookingRawValueToBooking(form.getRawValue() as BookingFormRawValue | NewBookingFormRawValue);
  }

  resetForm(form: BookingFormGroup, booking: BookingFormGroupInput): void {
    const bookingRawValue = this.convertBookingToBookingRawValue({ ...this.getFormDefaults(), ...booking });
    form.reset(
      {
        ...bookingRawValue,
        id: { value: bookingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDateTime: currentTime,
      endDateTime: currentTime,
      createdDate: currentTime,
    };
  }

  private convertBookingRawValueToBooking(rawBooking: BookingFormRawValue | NewBookingFormRawValue): IBooking | NewBooking {
    return {
      ...rawBooking,
      startDateTime: dayjs(rawBooking.startDateTime, DATE_TIME_FORMAT),
      endDateTime: dayjs(rawBooking.endDateTime, DATE_TIME_FORMAT),
      createdDate: dayjs(rawBooking.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertBookingToBookingRawValue(
    booking: IBooking | (Partial<NewBooking> & BookingFormDefaults),
  ): BookingFormRawValue | PartialWithRequiredKeyOf<NewBookingFormRawValue> {
    return {
      ...booking,
      startDateTime: booking.startDateTime ? booking.startDateTime.format(DATE_TIME_FORMAT) : undefined,
      endDateTime: booking.endDateTime ? booking.endDateTime.format(DATE_TIME_FORMAT) : undefined,
      createdDate: booking.createdDate ? booking.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
