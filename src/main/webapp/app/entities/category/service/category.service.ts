import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategory, NewCategory } from '../category.model';

export type PartialUpdateCategory = Partial<ICategory> & Pick<ICategory, 'id'>;

export type EntityResponseType = HttpResponse<ICategory>;
export type EntityArrayResponseType = HttpResponse<ICategory[]>;

@Injectable({ providedIn: 'root' })
export class CategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/categories');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  createMultipart(formData: FormData) {
    return this.http
      .post<ApiResponse<ICategory>>(this.resourceUrl, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  updateMultipart(id: number, formData: FormData) {
    return this.http
      .put<ApiResponse<ICategory>>(`${this.resourceUrl}/${id}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  patchMultipart(id: number, formData: FormData) {
    return this.http
      .patch<ApiResponse<ICategory>>(`${this.resourceUrl}/${id}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  getCategoriesByType(typeId: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<ApiResponse<ICategory[]>>(`${this.applicationConfigService.getEndpointFor('api/types')}/${typeId}/categories`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  create(category: NewCategory): Observable<EntityResponseType> {
    return this.http
      .post<ApiResponse<ICategory>>(this.resourceUrl, category, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(category: ICategory): Observable<EntityResponseType> {
    return this.http
      .put<ApiResponse<ICategory>>(`${this.resourceUrl}/${this.getCategoryIdentifier(category)}`, category, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(category: PartialUpdateCategory): Observable<EntityResponseType> {
    return this.http
      .patch<ApiResponse<ICategory>>(`${this.resourceUrl}/${this.getCategoryIdentifier(category)}`, category, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ApiResponse<ICategory>>(`${this.resourceUrl}/${id}`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ApiResponse<ICategory[]>>(this.resourceUrl, {
        params: options,
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<ApiResponse<null>>> {
    return this.http.delete<ApiResponse<null>>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getCategoryIdentifier(category: Pick<ICategory, 'id'>): number {
    return category.id;
  }

  compareCategory(o1: Pick<ICategory, 'id'> | null, o2: Pick<ICategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getCategoryIdentifier(o1) === this.getCategoryIdentifier(o2) : o1 === o2;
  }

  addCategoryToCollectionIfMissing<Type extends Pick<ICategory, 'id'>>(
    categoryCollection: Type[],
    ...categoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const categories: Type[] = categoriesToCheck.filter(isPresent);
    if (categories.length > 0) {
      const categoryCollectionIdentifiers = categoryCollection.map(categoryItem => this.getCategoryIdentifier(categoryItem)!);
      const categoriesToAdd = categories.filter(categoryItem => {
        const categoryIdentifier = this.getCategoryIdentifier(categoryItem);
        if (categoryCollectionIdentifiers.includes(categoryIdentifier)) {
          return false;
        }
        categoryCollectionIdentifiers.push(categoryIdentifier);
        return true;
      });
      return [...categoriesToAdd, ...categoryCollection];
    }
    return categoryCollection;
  }

  protected convertResponseFromServer(res: HttpResponse<ApiResponse<ICategory>>): HttpResponse<ICategory> {
    return res.clone({
      body: res.body?.data ?? null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<ApiResponse<ICategory[]>>): HttpResponse<ICategory[]> {
    return res.clone({
      body: res.body?.data ?? [],
    });
  }
}
