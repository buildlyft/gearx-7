import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISubcategory, NewSubcategory } from '../subcategory.model';

export type PartialUpdateSubcategory = Partial<ISubcategory> & Pick<ISubcategory, 'id'>;

export type EntityResponseType = HttpResponse<ISubcategory>;
export type EntityArrayResponseType = HttpResponse<ISubcategory[]>;

@Injectable({ providedIn: 'root' })
export class SubcategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/subcategories');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(subcategory: NewSubcategory): Observable<EntityResponseType> {
    return this.http
      .post<ApiResponse<ISubcategory>>(this.resourceUrl, subcategory, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(subcategory: ISubcategory): Observable<EntityResponseType> {
    return this.http
      .put<ApiResponse<ISubcategory>>(`${this.resourceUrl}/${this.getSubcategoryIdentifier(subcategory)}`, subcategory, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  createMultipart(formData: FormData) {
    return this.http
      .post<ApiResponse<ISubcategory>>(this.resourceUrl, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  updateMultipart(id: number, formData: FormData) {
    return this.http
      .put<ApiResponse<ISubcategory>>(`${this.resourceUrl}/${id}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  patchMultipart(id: number, formData: FormData) {
    return this.http
      .patch<ApiResponse<ISubcategory>>(`${this.resourceUrl}/${id}`, formData, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  getSubcategoriesByCategory(categoryId: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<ApiResponse<ISubcategory[]>>(`${this.applicationConfigService.getEndpointFor('api/categories')}/${categoryId}/subcategories`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  partialUpdate(subcategory: PartialUpdateSubcategory): Observable<EntityResponseType> {
    return this.http
      .patch<ApiResponse<ISubcategory>>(`${this.resourceUrl}/${this.getSubcategoryIdentifier(subcategory)}`, subcategory, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ApiResponse<ISubcategory>>(`${this.resourceUrl}/${id}`, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ApiResponse<ISubcategory[]>>(this.resourceUrl, {
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

  getSubcategoryIdentifier(subcategory: Pick<ISubcategory, 'id'>): number {
    return subcategory.id;
  }

  compareSubcategory(o1: Pick<ISubcategory, 'id'> | null, o2: Pick<ISubcategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getSubcategoryIdentifier(o1) === this.getSubcategoryIdentifier(o2) : o1 === o2;
  }

  addSubcategoryToCollectionIfMissing<Type extends Pick<ISubcategory, 'id'>>(
    subcategoryCollection: Type[],
    ...subcategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const subcategories: Type[] = subcategoriesToCheck.filter(isPresent);
    if (subcategories.length > 0) {
      const subcategoryCollectionIdentifiers = subcategoryCollection.map(
        subcategoryItem => this.getSubcategoryIdentifier(subcategoryItem)!,
      );
      const subcategoriesToAdd = subcategories.filter(subcategoryItem => {
        const subcategoryIdentifier = this.getSubcategoryIdentifier(subcategoryItem);
        if (subcategoryCollectionIdentifiers.includes(subcategoryIdentifier)) {
          return false;
        }
        subcategoryCollectionIdentifiers.push(subcategoryIdentifier);
        return true;
      });
      return [...subcategoriesToAdd, ...subcategoryCollection];
    }
    return subcategoryCollection;
  }

  protected convertResponseFromServer(res: HttpResponse<ApiResponse<ISubcategory>>): HttpResponse<ISubcategory> {
    return res.clone({
      body: res.body?.data ?? null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<ApiResponse<ISubcategory[]>>): HttpResponse<ISubcategory[]> {
    return res.clone({
      body: res.body?.data ?? [],
    });
  }
}
