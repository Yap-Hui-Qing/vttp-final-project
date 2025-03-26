import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AppComponent } from '../../../app.component';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit, OnDestroy {

  private fb = inject(FormBuilder)
  private authSvc = inject(AuthService)
  private router = inject(Router)
  private messageSvc = inject(MessageService)
  private appComponent = inject(AppComponent)

  protected signupForm!: FormGroup
  protected hidePassword = true;
  protected loading = false;

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      username: this.fb.control<string>('', [ Validators.required, Validators.minLength(3)]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(3)]),
      confirmPassword: this.fb.control<string>('', [ Validators.required ])
    },
    { validators: this.matchPasswords }
    );

    this.signupForm.get('password')?.valueChanges.subscribe(() => {
      this.signupForm.updateValueAndValidity();
    });

    this.signupForm.get('confirmPassword')?.valueChanges.subscribe(() => {
      this.signupForm.updateValueAndValidity();
    });
    this.appComponent.toggleMenuBarVisibility(false)
  }

  protected togglePasswordVisibility(id: string){
    this.hidePassword = !this.hidePassword
    const passwordInput = document.getElementById(id) as HTMLInputElement;
    passwordInput.type = this.hidePassword ? 'password' : 'text';
  }

  // check if password matches
  protected matchPasswords(control: AbstractControl): ValidationErrors | null {

    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (!password || !confirmPassword) return null;

    if (password === confirmPassword) {
      return null;
    }
    return { matchPasswords: true } as ValidationErrors;
  }

  protected register() {

    this.loading = true;

    console.info('registration: ', this.signupForm.value)
    this.authSvc.register(this.signupForm.value).subscribe({
      next: (response) => {
        this.messageSvc.add({ severity: 'success', summary: 'Registration Successful', detail: 'Redirecting to login...' });

        setTimeout(() => {
          this.loading = false
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false
        this.messageSvc.add({ severity: 'error', summary: 'Registration Failed', detail: err.message || 'An error occurred' });
      }
    })
  }

  ngOnDestroy(): void {
    this.appComponent.toggleMenuBarVisibility(true)
  }
}
