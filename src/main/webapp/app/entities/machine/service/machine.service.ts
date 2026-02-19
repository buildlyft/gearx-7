import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMachine, NewMachine } from '../machine.model';

export type PartialUpdateMachine = Partial<IMachine> & Pick<IMachine, 'id'>;

type RestOf<T extends IMachine | NewMachine> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

export type RestMachine = RestOf<IMachine>;

export type NewRestMachine = RestOf<NewMachine>;

export type PartialUpdateRestMachine = RestOf<PartialUpdateMachine>;

export type EntityResponseType = HttpResponse<IMachine>;
export type EntityArrayResponseType = HttpResponse<IMachine[]>;

@Injectable({ providedIn: 'root' })
export class MachineService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/machines');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(machine: NewMachine): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(machine);
    return this.http
      .post<RestMachine>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(machine: IMachine): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(machine);
    return this.http
      .put<RestMachine>(`${this.resourceUrl}/${this.getMachineIdentifier(machine)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(machine: PartialUpdateMachine): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(machine);
    return this.http
      .patch<RestMachine>(`${this.resourceUrl}/${this.getMachineIdentifier(machine)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMachine>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMachine[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMachineIdentifier(machine: Pick<IMachine, 'id'>): number {
    return machine.id;
  }

  compareMachine(o1: Pick<IMachine, 'id'> | null, o2: Pick<IMachine, 'id'> | null): boolean {
    return o1 && o2 ? this.getMachineIdentifier(o1) === this.getMachineIdentifier(o2) : o1 === o2;
  }

  queryWithoutOperator(): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestMachine[]>(`${this.resourceUrl}/without-operator`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  addMachineToCollectionIfMissing<Type extends Pick<IMachine, 'id'>>(
    machineCollection: Type[],
    ...machinesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const machines: Type[] = machinesToCheck.filter(isPresent);
    if (machines.length > 0) {
      const machineCollectionIdentifiers = machineCollection.map(machineItem => this.getMachineIdentifier(machineItem)!);
      const machinesToAdd = machines.filter(machineItem => {
        const machineIdentifier = this.getMachineIdentifier(machineItem);
        if (machineCollectionIdentifiers.includes(machineIdentifier)) {
          return false;
        }
        machineCollectionIdentifiers.push(machineIdentifier);
        return true;
      });
      return [...machinesToAdd, ...machineCollection];
    }
    return machineCollection;
  }

  protected convertDateFromClient<T extends IMachine | NewMachine | PartialUpdateMachine>(machine: T): RestOf<T> {
    return {
      ...machine,
      createdDate: machine.createdDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMachine: RestMachine): IMachine {
    return {
      ...restMachine,
      createdDate: restMachine.createdDate ? dayjs(restMachine.createdDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMachine>): HttpResponse<IMachine> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMachine[]>): HttpResponse<IMachine[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
