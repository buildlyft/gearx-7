import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from 'app/core/models/api-response.model';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

@Injectable({ providedIn: 'root' })
export class PasswordService {
  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  save(newPassword: string, currentPassword: string): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>(this.applicationConfigService.getEndpointFor('api/account/change-password'), {
      currentPassword,
      newPassword,
    });
  }
}
