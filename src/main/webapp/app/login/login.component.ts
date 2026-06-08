import { Component, ViewChild, OnInit, AfterViewInit, ElementRef } from '@angular/core';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-login',
  standalone: true,
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  @ViewChild('username', { static: false })
  username!: ElementRef;

  otpSent = false;
  otpSending = false;

  authenticationError = false;

  selectedAppType: 'CUSTOMER' | 'PARTNER' | null = null;

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    otp: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true }),
  });

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // if already authenticated then navigate to home page
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username.nativeElement.focus();
  }

  login(): void {
    const form = this.loginForm.getRawValue();

    this.loginService.verifyOtp(form.username, form.otp, this.selectedAppType!, form.rememberMe).subscribe({
      next: () => {
        this.authenticationError = false;
        this.router.navigate(['']);
      },
      error: () => {
        this.authenticationError = true;
      },
    });
  }

  sendOtp(): void {
    const username = this.loginForm.get('username')?.value;

    if (!username) {
      return;
    }

    this.loginService.sendOtp(username).subscribe({
      next: response => {
        if (response.status) {
          this.otpSent = true;
          this.authenticationError = false;
        } else {
          this.authenticationError = true;
        }
      },
      error: () => {
        this.authenticationError = true;
      },
    });
  }

  customerLogin(): void {
    this.selectedAppType = 'CUSTOMER';
    this.sendOtp();
  }

  partnerLogin(): void {
    this.selectedAppType = 'PARTNER';
    this.sendOtp();
  }
}
