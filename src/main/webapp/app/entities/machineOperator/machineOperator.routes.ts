import { Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import machineOperatorResolve from './route/machineOperator-routing-resolve.service';

export const machineOperatorRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/machineOperator.component').then(m => m.MachineOperatorComponent),
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/machineOperator-update.component').then(m => m.MachineOperatorUpdateComponent),
    resolve: { machineOperator: machineOperatorResolve },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':machineId/view',
    loadComponent: () => import('./details/machineOperator-detail.component').then(m => m.MachineOperatorDetailComponent),
    resolve: { machineOperator: machineOperatorResolve },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':machineId/edit',
    loadComponent: () => import('./update/machineOperator-update.component').then(m => m.MachineOperatorUpdateComponent),
    resolve: { machineOperator: machineOperatorResolve },
    canActivate: [UserRouteAccessService],
  },
];
