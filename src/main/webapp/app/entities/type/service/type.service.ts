import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IType, NewType } from '../type.model';
import { ApiResponse } from 'app/core/models/api-response.model';
export type PartialUpdateType = Partial<IType> & Pick<IType, 'id'>;
export type EntityResponseType = HttpResponse<IType>;
export type EntityArrayResponseType = HttpResponse<IType[]>;

@Injectable({ providedIn: 'root' })
export class TypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/types');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(type: NewType, image: File): Observable<EntityResponseType> {
    const formData = new FormData();

    formData.append('type', new Blob([JSON.stringify(type)], { type: 'application/json' }));

    formData.append('image', image);

    return this.http
      .post<ApiResponse<IType>>(this.resourceUrl, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(type: IType, image?: File): Observable<EntityResponseType> {
    const formData = new FormData();

    formData.append('type', new Blob([JSON.stringify(type)], { type: 'application/json' }));

    if (image) {
      formData.append('image', image);
    }

    return this.http
      .put<ApiResponse<IType>>(`${this.resourceUrl}/${type.id}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(type: PartialUpdateType): Observable<EntityResponseType> {
    return this.http
      .patch<ApiResponse<IType>>(`${this.resourceUrl}/${type.id}`, type, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ApiResponse<IType>>(`${this.resourceUrl}/${id}`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(): Observable<HttpResponse<IType[]>> {
    return this.http
      .get<ApiResponse<IType[]>>(this.resourceUrl, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<ApiResponse<null>>> {
    return this.http.delete<ApiResponse<null>>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getTypeIdentifier(type: Pick<IType, 'id'>): number {
    return type.id;
  }

  compareType(o1: Pick<IType, 'id'> | null, o2: Pick<IType, 'id'> | null): boolean {
    return o1 && o2 ? this.getTypeIdentifier(o1) === this.getTypeIdentifier(o2) : o1 === o2;
  }

  addTypeToCollectionIfMissing<Type extends Pick<IType, 'id'>>(
    typeCollection: Type[],
    ...typesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const types: Type[] = typesToCheck.filter(isPresent);
    if (types.length > 0) {
      const collectionIdentifiers = typeCollection.map(item => this.getTypeIdentifier(item));
      const typesToAdd = types.filter(item => {
        const identifier = this.getTypeIdentifier(item);
        if (collectionIdentifiers.includes(identifier)) {
          return false;
        }
        collectionIdentifiers.push(identifier);
        return true;
      });
      return [...typesToAdd, ...typeCollection];
    }
    return typeCollection;
  }

  protected convertResponseFromServer(res: HttpResponse<ApiResponse<IType>>): HttpResponse<IType> {
    return res.clone({
      body: res.body?.data ?? null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<ApiResponse<IType[]>>): HttpResponse<IType[]> {
    return res.clone({
      body: res.body?.data ?? [],
    });
  }
}
