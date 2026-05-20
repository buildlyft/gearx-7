import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBooking, NewBooking } from '../booking.model';

export type PartialUpdateBooking = Partial<IBooking> & Pick<IBooking, 'id'>;

type RestOf<T extends IBooking | NewBooking> = Omit<T, 'startDateTime' | 'endDateTime' | 'createdDate'> & {
  startDateTime?: string | null;
  endDateTime?: string | null;
  createdDate?: string | null;
};

export type RestBooking = RestOf<IBooking>;

export type NewRestBooking = RestOf<NewBooking>;

export type PartialUpdateRestBooking = RestOf<PartialUpdateBooking>;

export type EntityResponseType = HttpResponse<IBooking>;
export type EntityArrayResponseType = HttpResponse<IBooking[]>;

@Injectable({ providedIn: 'root' })
export class BookingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bookings');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(booking: NewBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .post<ApiResponse<RestBooking>>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(booking: IBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .put<ApiResponse<RestBooking>>(`${this.resourceUrl}/${this.getBookingIdentifier(booking)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(booking: PartialUpdateBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .patch<ApiResponse<RestBooking>>(`${this.resourceUrl}/${this.getBookingIdentifier(booking)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ApiResponse<RestBooking>>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ApiResponse<RestBooking[]>>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<ApiResponse<null>>> {
    return this.http.delete<ApiResponse<null>>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getBookingIdentifier(booking: Pick<IBooking, 'id'>): number {
    return booking.id;
  }

  getBookingsByOwner(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);

    return this.http
      .get<ApiResponse<RestBooking[]>>(`${this.resourceUrl}/by-owner`, {
        params: options,
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  compareBooking(o1: Pick<IBooking, 'id'> | null, o2: Pick<IBooking, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookingIdentifier(o1) === this.getBookingIdentifier(o2) : o1 === o2;
  }

  addBookingToCollectionIfMissing<Type extends Pick<IBooking, 'id'>>(
    bookingCollection: Type[],
    ...bookingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookings: Type[] = bookingsToCheck.filter(isPresent);
    if (bookings.length > 0) {
      const bookingCollectionIdentifiers = bookingCollection.map(bookingItem => this.getBookingIdentifier(bookingItem)!);
      const bookingsToAdd = bookings.filter(bookingItem => {
        const bookingIdentifier = this.getBookingIdentifier(bookingItem);
        if (bookingCollectionIdentifiers.includes(bookingIdentifier)) {
          return false;
        }
        bookingCollectionIdentifiers.push(bookingIdentifier);
        return true;
      });
      return [...bookingsToAdd, ...bookingCollection];
    }
    return bookingCollection;
  }

  protected convertDateFromClient<T extends IBooking | NewBooking | PartialUpdateBooking>(booking: T): RestOf<T> {
    return {
      ...booking,
      startDateTime: booking.startDateTime ? booking.startDateTime.toISOString() : null,

      endDateTime: booking.endDateTime ? booking.endDateTime.toISOString() : null,
      createdDate: booking.createdDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBooking: RestBooking): IBooking {
    return {
      ...restBooking,
      startDateTime: restBooking.startDateTime ? dayjs(restBooking.startDateTime) : undefined,
      endDateTime: restBooking.endDateTime ? dayjs(restBooking.endDateTime) : undefined,
      createdDate: restBooking.createdDate ? dayjs(restBooking.createdDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<ApiResponse<RestBooking>>): HttpResponse<IBooking> {
    return res.clone({
      body: res.body?.data ? this.convertDateFromServer(res.body.data) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<ApiResponse<RestBooking[]>>): HttpResponse<IBooking[]> {
    return res.clone({
      body: res.body?.data ? res.body.data.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
