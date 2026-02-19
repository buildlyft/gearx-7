import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IMachineOperator } from '../machineOperator.model';

export type EntityResponseType = HttpResponse<IMachineOperator>;
export type EntityArrayResponseType = HttpResponse<IMachineOperator[]>;

@Injectable({ providedIn: 'root' })
export class MachineOperatorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/machine-operators');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  getAllActive(): Observable<EntityArrayResponseType> {
    return this.http.get<IMachineOperator[]>(`${this.resourceUrl}/active`, {
      observe: 'response',
    });
  }

  getByMachine(machineId: number): Observable<EntityResponseType> {
    return this.http.get<IMachineOperator>(`${this.resourceUrl}/machine/${machineId}`, { observe: 'response' });
  }

  create(formData: FormData): Observable<EntityResponseType> {
    return this.http.post<IMachineOperator>(`${this.resourceUrl}/create_and_assign`, formData, { observe: 'response' });
  }

  reassign(machineId: number, formData: FormData): Observable<EntityResponseType> {
    return this.http.put<IMachineOperator>(`${this.resourceUrl}/machine/${machineId}`, formData, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getMachineOperatorIdentifier(operator: Pick<IMachineOperator, 'operatorId'>): number {
    return operator.operatorId;
  }
}
