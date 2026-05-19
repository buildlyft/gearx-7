import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';
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

  create(formData: FormData): Observable<EntityResponseType> {
    return this.http
      .post<ApiResponse<IMachineOperator>>(`${this.resourceUrl}/create_and_assign`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  getAllByPartner(): Observable<EntityArrayResponseType> {
    return this.http
      .get<ApiResponse<IMachineOperator[]>>(`${this.resourceUrl}/partner`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ApiResponse<IMachineOperator>>(`${this.resourceUrl}/${id}`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(operatorId: number, formData: FormData): Observable<EntityResponseType> {
    return this.http
      .put<ApiResponse<IMachineOperator>>(`${this.resourceUrl}/${operatorId}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(operatorId: number, formData: FormData): Observable<EntityResponseType> {
    return this.http
      .patch<ApiResponse<IMachineOperator>>(`${this.resourceUrl}/${operatorId}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<ApiResponse<null>>> {
    return this.http.delete<ApiResponse<null>>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getMachineOperatorIdentifier(operator: Pick<IMachineOperator, 'operatorId'>): number {
    return operator.operatorId;
  }

  protected convertResponseFromServer(res: HttpResponse<ApiResponse<IMachineOperator>>): HttpResponse<IMachineOperator> {
    return res.clone({
      body: res.body?.data ?? null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<ApiResponse<IMachineOperator[]>>): HttpResponse<IMachineOperator[]> {
    return res.clone({
      body: res.body?.data ?? [],
    });
  }
}
