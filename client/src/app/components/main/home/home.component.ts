import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { AuthStore } from '../../auth/auth.store';
import { Observable, take } from 'rxjs';
import { OverlayPanel, OverlayPanelModule } from 'primeng/overlaypanel';
import { Router } from '@angular/router';
import { UserStore } from '../../../user.store';
import { MealService } from '../../../services/meals.service';
import { Ingredient, Instruction, Meal } from '../../../models';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  isLoggingOut: boolean = false
  hasMealPlan: boolean = false
  today: Date = new Date()
  meals: Meal[] = []

  private authStore = inject(AuthStore)
  private router = inject(Router)
  private messageSvc = inject(MessageService)
  private userStore = inject(UserStore)
  private mealSvc = inject(MealService)

  username!: string
  groceries$!: Observable<any>

  protected username$ = this.authStore.getUsername$.pipe(
    take(1)
  )

  ngOnInit(): void {
    this.hasMealPlan = true;
    this.username$.pipe(
      take(1)  // Make sure it only subscribes once to get the value
    ).subscribe(username => {
      this.username = username
      console.info('>>> checking if user has already set preference...')
      this.userStore.loadUserPreference(username);
      this.groceries$ = this.mealSvc.getGroceryList(this.username)
      this.loadTodaysMeals(this.username)
    });
  }

  loadTodaysMeals(username: string): void {
    this.mealSvc.getTodaysMeals(this.username).subscribe({
      next: (meals) => {
        this.meals = meals;
      },
      error: (err) => {
        console.error('Error loading meals:', err);
      }
    });
  }

  getInstructionKeys(instructions: Instruction): string[] {
    return Object.keys(instructions).sort(); // Ensure steps are in order
  }

  protected logout() {
    console.log('>>> Logging out...')
    this.isLoggingOut = true
    this.messageSvc.add({ severity: 'success', summary: 'Success', detail: 'Logging out...' })

    setTimeout(() => {
      this.authStore.logout()
      this.isLoggingOut = false
      this.router.navigate(['/'])
    }, 2000);

  }
}
