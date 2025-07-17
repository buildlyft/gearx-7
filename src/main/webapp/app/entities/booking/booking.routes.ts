import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { BookingComponent } from './list/booking.component';
import { BookingDetailComponent } from './detail/booking-detail.component';
import { BookingUpdateComponent } from './update/booking-update.component';
import BookingResolve from './route/booking-routing-resolve.service';

const bookingRoute: Routes = [
  {
    path: '',
    component: BookingComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookingDetailComponent,
    resolve: {
      booking: BookingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookingUpdateComponent,
    resolve: {
      booking: BookingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookingUpdateComponent,
    resolve: {
      booking: BookingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookingRoute;
