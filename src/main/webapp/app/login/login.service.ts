import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { Login } from './login.model';

@Injectable({ providedIn: 'root' })
export class LoginService {
  constructor(
    private accountService: AccountService,
    private authServerProvider: AuthServerProvider,
    private http: HttpClient,
  ) {}

  login(credentials: Login): Observable<Account | null> {
    return this.authServerProvider.login(credentials).pipe(mergeMap(() => this.accountService.identity(true)));
  }

  logout(): void {
    this.authServerProvider.logout().subscribe({ complete: () => this.accountService.authenticate(null) });
  }

  sendOtp(username: string) {
    return this.http.post<any>('api/authenticate', {
      username,
    });
  }

  verifyOtp(phoneNumber: string, otp: string, appType: string, rememberMe: boolean): Observable<Account | null> {
    return this.authServerProvider
      .verifyOtp(phoneNumber, otp, appType, rememberMe)
      .pipe(mergeMap(() => this.accountService.identity(true)));
  }
}
