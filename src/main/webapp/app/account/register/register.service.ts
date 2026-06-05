import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from 'app/core/models/api-response.model';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Registration } from './register.model';

@Injectable({ providedIn: 'root' })
export class RegisterService {
  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  save(registration: Registration): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(this.applicationConfigService.getEndpointFor('api/register'), registration);
  }

  verifyOtp(phoneNumber: string, otp: string) {
    return this.http.post(this.applicationConfigService.getEndpointFor('api/verify-otp'), {
      phoneNumber,
      otp,
    });
  }
}
