import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { User } from '../../../models';
import { AuthService } from '../auth.service';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { UserStore } from '../../../user.store';
import { AppComponent } from '../../../app.component';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, OnDestroy {

  protected login!: User[]
  protected form!: FormGroup
  protected hidePassword = true
  protected loading = false

  private fb = inject(FormBuilder)
  private authSvc = inject(AuthService)
  private messageSvc = inject(MessageService)
  private router = inject(Router)
  private cdr = inject(ChangeDetectorRef)
  private userStore = inject(UserStore)
  private appComponent = inject(AppComponent)

  ngOnInit(): void {
    this.form = this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required])
    })
    this.appComponent.toggleMenuBarVisibility(false)
  }

  protected togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    passwordInput.type = this.hidePassword ? 'password' : 'text';
  }

  protected isCtrlInvalid(ctrlName: string): boolean {
    return !!this.form.get(ctrlName)?.invalid
  }

  protected onLogin() {
    // if (this.form.valid) {
    //   const user: User = this.form.value
    //   console.info('user info: ', user)
    //   this.authSvc.login(user)
    // }
    // this.formSubmitAttempt = true
    console.info('>> should start loading...', this.loading)
    this.loading = true
    this.cdr.detectChanges()
    console.info('Logging in:', this.form.value);

    this.authSvc.login(this.form.value).subscribe({
      next: (response) => {
        this.userStore.setUsername(this.form.value.username)
        this.messageSvc.add({ severity: 'success', summary: 'Login Successful', detail: 'Redirecting to home...' });

        setTimeout(() => {
          console.info('>>>loading should stop...')
          this.loading = false
          this.router.navigate(['/home']);
        }, 3000);
      },
      error: (error) => {
        console.info('error: ', error.message)
        this.loading = false
        if (error.message === 'Incorrect username or password') {
          this.messageSvc.add({ severity: 'error', summary: 'Login Failed', detail: error.message || 'Invalid credentials' });
        } else if (error.message === 'Username not found') {
          this.messageSvc.add({ severity: 'error', summary: 'Login Failed', detail: error.message || 'Please create an account' });
          setTimeout(() => {
            this.router.navigate(['/register']);
          }, 2000);
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.appComponent.toggleMenuBarVisibility(true)
  }
}



