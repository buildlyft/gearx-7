import { Routes } from '@angular/router';

import { VehicleDocumentComponent } from './list/vehicleDocument.component';
import { VehicleDocumentDetailComponent } from './detail/vehicleDocument-detail.component';
import { VehicleDocumentUpdateComponent } from './update/vehicleDocument-update.component';
import VehicleDocumentRoutingResolveService from './route/vehicleDocument-routing-resolve.service';

const routes: Routes = [
  {
    path: '',
    component: VehicleDocumentComponent,
    data: {
      pageTitle: 'Vehicle Documents',
      defaultSort: 'id,asc',
    },
  },
  {
    path: 'new',
    component: VehicleDocumentUpdateComponent,
    data: {
      pageTitle: 'Upload Vehicle Documents',
    },
  },
  {
    path: ':id/view',
    component: VehicleDocumentDetailComponent,
    resolve: {
      vehicleDocument: VehicleDocumentRoutingResolveService,
    },
    data: {
      pageTitle: 'Vehicle Document Details',
    },
  },
  {
    path: ':id/edit',
    component: VehicleDocumentUpdateComponent,
    resolve: {
      vehicleDocument: VehicleDocumentRoutingResolveService,
    },
    data: {
      pageTitle: 'Edit Vehicle Document',
    },
  },
];

export default routes;
