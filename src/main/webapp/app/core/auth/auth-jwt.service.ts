import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';
import { Login } from 'app/login/login.model';
import { ApplicationConfigService } from '../config/application-config.service';
import { StateStorageService } from './state-storage.service';

type JwtToken = {
  id_token: string;
};

@Injectable({ providedIn: 'root' })
export class AuthServerProvider {
  constructor(
    private http: HttpClient,
    private stateStorageService: StateStorageService,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  getToken(): string {
    return this.stateStorageService.getAuthenticationToken() ?? '';
  }

  login(credentials: Login): Observable<void> {
    return this.http
      .post<ApiResponse<JwtToken>>(this.applicationConfigService.getEndpointFor('api/authenticate'), credentials)
      .pipe(map(response => this.authenticateSuccess(response, credentials.rememberMe)));
  }

  verifyOtp(phoneNumber: string, otp: string, appType: string, rememberMe: boolean): Observable<void> {
    return this.http
      .post<ApiResponse<JwtToken>>(this.applicationConfigService.getEndpointFor('api/verify-otp'), {
        phoneNumber,
        otp,
        appType,
      })
      .pipe(map(response => this.authenticateSuccess(response, rememberMe)));
  }

  logout(): Observable<void> {
    return new Observable(observer => {
      this.stateStorageService.clearAuthenticationToken();
      observer.complete();
    });
  }

  private authenticateSuccess(response: ApiResponse<JwtToken>, rememberMe: boolean): void {
    this.stateStorageService.storeAuthenticationToken(response.data.id_token, rememberMe);
  }
}
