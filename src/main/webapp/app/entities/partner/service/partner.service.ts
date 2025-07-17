import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPartner, NewPartner } from '../partner.model';

export type PartialUpdatePartner = Partial<IPartner> & Pick<IPartner, 'id'>;

export type EntityResponseType = HttpResponse<IPartner>;
export type EntityArrayResponseType = HttpResponse<IPartner[]>;

@Injectable({ providedIn: 'root' })
export class PartnerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/partners');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(partner: NewPartner): Observable<EntityResponseType> {
    return this.http.post<IPartner>(this.resourceUrl, partner, { observe: 'response' });
  }

  update(partner: IPartner): Observable<EntityResponseType> {
    return this.http.put<IPartner>(`${this.resourceUrl}/${this.getPartnerIdentifier(partner)}`, partner, { observe: 'response' });
  }

  partialUpdate(partner: PartialUpdatePartner): Observable<EntityResponseType> {
    return this.http.patch<IPartner>(`${this.resourceUrl}/${this.getPartnerIdentifier(partner)}`, partner, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPartner>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPartner[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPartnerIdentifier(partner: Pick<IPartner, 'id'>): number {
    return partner.id;
  }

  comparePartner(o1: Pick<IPartner, 'id'> | null, o2: Pick<IPartner, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartnerIdentifier(o1) === this.getPartnerIdentifier(o2) : o1 === o2;
  }

  addPartnerToCollectionIfMissing<Type extends Pick<IPartner, 'id'>>(
    partnerCollection: Type[],
    ...partnersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const partners: Type[] = partnersToCheck.filter(isPresent);
    if (partners.length > 0) {
      const partnerCollectionIdentifiers = partnerCollection.map(partnerItem => this.getPartnerIdentifier(partnerItem)!);
      const partnersToAdd = partners.filter(partnerItem => {
        const partnerIdentifier = this.getPartnerIdentifier(partnerItem);
        if (partnerCollectionIdentifiers.includes(partnerIdentifier)) {
          return false;
        }
        partnerCollectionIdentifiers.push(partnerIdentifier);
        return true;
      });
      return [...partnersToAdd, ...partnerCollection];
    }
    return partnerCollection;
  }
}
