import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Pagination } from 'app/core/request/request.model';
import { IUser } from '../user-management.model';
import { ApiResponse } from 'app/core/models/api-response.model';
@Injectable({ providedIn: 'root' })
export class UserManagementService {
  private resourceUrl = this.applicationConfigService.getEndpointFor('api/admin/users');

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  create(user: IUser): Observable<ApiResponse<IUser>> {
    return this.http.post<ApiResponse<IUser>>(this.resourceUrl, user);
  }

  update(user: IUser): Observable<ApiResponse<IUser>> {
    return this.http.put<ApiResponse<IUser>>(this.resourceUrl, user);
  }

  find(login: string): Observable<ApiResponse<IUser>> {
    return this.http.get<ApiResponse<IUser>>(`${this.resourceUrl}/${login}`);
  }

  query(req?: Pagination): Observable<HttpResponse<ApiResponse<IUser[]>>> {
    const options = createRequestOption(req);
    return this.http.get<ApiResponse<IUser[]>>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(login: string): Observable<{}> {
    return this.http.delete(`${this.resourceUrl}/${login}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>(this.applicationConfigService.getEndpointFor('api/authorities'));
  }
}
