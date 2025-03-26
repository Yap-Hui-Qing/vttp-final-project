import { Component, ComponentFactoryResolver, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { addDays, startOfWeek, format, isBefore } from 'date-fns';
import { AuthService } from '../../auth/auth.service';
import { GoogleCalendarService } from '../../../services/google.calendar.service';
import { Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Day, Meal, Recipe } from '../../../models';
import { UserStore } from '../../../user.store';
import { MealService } from '../../../services/meals.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-build',
  standalone: false,
  templateUrl: './build.component.html',
  styleUrl: './build.component.css'
})
export class BuildComponent implements OnInit {

  private router = inject(Router)
  private activatedRoute = inject(ActivatedRoute)
  private authSvc = inject(AuthService)
  private googleSvc = inject(GoogleCalendarService)
  private toastr = inject(ToastrService)
  private userStore = inject(UserStore)
  private mealSvc = inject(MealService)
  private messageSvc = inject(MessageService)

  currentWeek: string = '';
  today: Date = new Date();
  weekStart: Date = new Date();
  days: Day[] = [];
  mealTimes: string[] = ['Morning', 'Afternoon', 'Evening'];
  meals!: Meal[];
  username !: string
  isAuthenticated$ = this.authSvc.watchAuthStatus()
  isSyncing = false
  syncMessage: { status: string, message: string } | null = null

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => this.username = params['username']
    )
    this.setWeek(new Date());
    this.authSvc.checkGoogleAuthStatus()
    this.userStore.getPlan.subscribe(
      (result) => this.meals = result
    )
  }

  connectGoogleCalendar() {
    this.googleSvc.initiateGoogleAuth(this.username);
  }

  setWeek(startDate: Date) {
    this.weekStart = startOfWeek(startDate, { weekStartsOn: 1 });
    this.currentWeek = `${format(this.weekStart, 'MMM d')} - ${format(addDays(this.weekStart, 6), 'MMM d')}`;

    this.days = Array.from({ length: 7 }).map((_, i) => {
      let date = addDays(this.weekStart, i);
      return { label: format(date, 'EEEE'), date: format(date, 'yyyy-MM-dd') };
    });
    this.loadMealPlan();
  }

  loadMealPlan() {
    this.mealSvc.getMealPlan(this.username, format(this.weekStart, 'yyyy-MM-dd'), format(addDays(this.weekStart, 6), 'yyyy-MM-dd'))
      .subscribe({
        next: (response) => {
          this.meals = response
          console.info('>>> meal plan from database: ', this.meals)
          this.userStore.addMeals([...this.meals]); // Update store
        },
        error: () => {
          this.meals = [];
          this.userStore.addMeals([]);
        }
      });
  }

  changeWeek(offset: number) {
    this.setWeek(addDays(this.weekStart, offset * 7));
    this.loadMealPlan();
  }

  isFutureDate(date: string): boolean {
    return !isBefore(new Date(date), this.today);
  }

  getMealForDayAndTime(date: string, time: string) {
    const meal = this.meals.find(m => m.mealDate === date && m.mealTime === time);
    if (meal) {
      // Parse mealDetails only if it's a string
      if (typeof meal.mealDetails === 'string') {
        try {
          meal.mealDetails = JSON.parse(meal.mealDetails);
        } catch (error) {
          console.error("Error parsing mealDetails:", error);
          return null;
        }
      }
      return meal;
    }
    return null;
  }

  addMeal(date: string, time: string) {
    this.router.navigate(['/build', date, time, 'search'])
  }

  deleteMeal({mealDate, mealTime}: {mealDate: string, mealTime: string}) {
    console.info('deleting...')
    this.userStore.deleteMeal({
      mealDate, 
      mealTime})
  }


  syncMealPlans() {
    this.isSyncing = true;
    this.googleSvc.syncMealPlans(this.username).subscribe({
      next: (response) => {
        this.syncMessage = {
          status: response.status,
          message: response.message
        }
        this.isSyncing = false;

        // Optional: Show toast notification
        if (response.status === 'success') {
          this.toastr.success(response.message);
        } else {
          this.toastr.error(response.message);
        }
      },
      error: (error) => {
        this.syncMessage = {
          status: 'error',
          message: error.message || 'Sync failed'
        };
        this.isSyncing = false;
        this.toastr.error('Failed to sync meal plans');
      }
    });
  }

  disconnectGoogleCalendar() {
    this.authSvc.disconnectGoogleCalendar().subscribe(
      (success) => {
        if (success) {
          this.syncMessage = {
            status: 'success',
            message: 'Disconnected from Google Calendar'
          };
          this.toastr.success('Disconnected from Google Calendar');
        } else {
          this.syncMessage = {
            status: 'error',
            message: 'Failed to disconnect'
          };
          this.toastr.error('Failed to disconnect from Google Calendar');
        }
      }
    );
  }

  savePlan() {
    this.meals.forEach((meal) => {
      // Check if mealDetails is a string and parse it
      if (typeof meal.mealDetails === 'string') {
        try {
          meal.mealDetails = JSON.parse(meal.mealDetails);
        } catch (error) {
          console.error('Error parsing mealDetails:', error);
          this.messageSvc.add({
            severity: 'error',
            summary: 'Error',
            detail: `Failed to parse meal details for ${meal.mealTime} on ${meal.mealDate}.`
          });
        }
      }
    });
    this.mealSvc.postMealPlan(this.username, this.meals)
      .then(() => {
        this.messageSvc.add({ severity: 'success', summary: 'Success', detail: 'Meal plan saved successfully!' });
        this.loadMealPlan()
      })
      .catch(() => {
        this.messageSvc.add({ severity: 'error', summary: 'Error', detail: 'Failed to save meal plan. Try again.' });
      });
  }

}
