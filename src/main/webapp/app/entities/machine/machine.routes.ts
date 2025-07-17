import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { MachineComponent } from './list/machine.component';
import { MachineDetailComponent } from './detail/machine-detail.component';
import { MachineUpdateComponent } from './update/machine-update.component';
import MachineResolve from './route/machine-routing-resolve.service';

const machineRoute: Routes = [
  {
    path: '',
    component: MachineComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MachineDetailComponent,
    resolve: {
      machine: MachineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MachineUpdateComponent,
    resolve: {
      machine: MachineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MachineUpdateComponent,
    resolve: {
      machine: MachineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default machineRoute;
