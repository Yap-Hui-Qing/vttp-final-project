import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Routes } from '@angular/router';
import { PrimeModule } from './prime.module';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';

import { AppComponent } from './app.component';
import { LandingComponent } from './components/main/landing/landing.component';
import { AuthService } from './components/auth/auth.service';
import { HomeComponent } from './components/main/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { AuthStore } from './components/auth/auth.store';
import { authenticated } from './components/auth/auth.guard';
import { ProfileComponent } from './components/main/user/profile.component';
import { PreferenceService } from './services/preferences.service';
import { UserStore } from './user.store';
import { AuthInterceptor } from './components/auth/auth.interceptor';
import { BuildComponent } from './components/main/build/build.component';
import { SearchComponent } from './components/main/build/search.component';
import { canLeavePreference } from './guards';
import { RecipeService } from './services/recipes.service';
import { GoogleCalendarService } from './services/google.calendar.service';
import { provideToastr } from 'ngx-toastr';
import { StoreComponent } from './components/main/groceries/store.component';
import { GroceryService } from './services/groceries.service';
import { MealService } from './services/meals.service';
import { InformationComponent } from './components/main/recipe/information.component';
import { SuccessComponent } from './components/main/groceries/success.component';
import { CancelComponent } from './components/main/groceries/cancel.component';

const appRoutes: Routes = [
  { path: '', component: LandingComponent},
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent},
  { path: 'home', component: HomeComponent,
    canActivate: [authenticated]
  },
  { path: 'home/:username/profile', component: ProfileComponent,
    canActivate: [authenticated], canDeactivate: [canLeavePreference]
  },
  { path: 'build/:username', component: BuildComponent,
    canActivate: [authenticated]
  },
  { path: 'build/:mealDate/:mealTime/search', component: SearchComponent,
    canActivate: [authenticated]
  },
  { path: 'groceries', component: StoreComponent,
    canActivate: [authenticated]
  },
  { path: 'meal-info/:recipeId', component: InformationComponent,
    canActivate: [authenticated]
  },
  { path: 'success', component: SuccessComponent,
    canActivate: [authenticated]
  },
  { path: 'cancel', component: CancelComponent, 
    canActivate: [authenticated]
  },
  { path: '**', redirectTo:'/', pathMatch:'full'}

]

@NgModule({
  declarations: [
    AppComponent,
    LandingComponent,
    LoginComponent,
    HomeComponent,
    RegisterComponent,
    ProfileComponent,
    BuildComponent,
    SearchComponent,
    StoreComponent,
    InformationComponent,
    SuccessComponent,
    CancelComponent,

  ],
  imports: [
    BrowserModule, 
    RouterModule.forRoot(appRoutes, {useHash: true}), 
    PrimeModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    provideToastr(),
    AuthService, 
    provideHttpClient(withInterceptors([AuthInterceptor])),
    MessageService,
    AuthStore,
    UserStore,
    PreferenceService,
    RecipeService,
    GoogleCalendarService,
    GroceryService,
    MealService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
