import { Component, inject, OnInit } from '@angular/core';
import { MealService } from '../../../services/meals.service';
import { ActivatedRoute } from '@angular/router';
import { UserStore } from '../../../user.store';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-information',
  standalone: false,
  templateUrl: './information.component.html',
  styleUrl: './information.component.css'
})
export class InformationComponent implements OnInit {

  private mealSvc = inject(MealService)
  private activatedRoute = inject(ActivatedRoute)
  private userStore = inject(UserStore)

  recipeId!: number
  username: string = ''
  mealDetails$!: Promise<any>

  ngOnInit(): void {
    this.userStore.username$.subscribe(
      (username) => this.username = username
    )
    this.recipeId = this.activatedRoute.snapshot.params['recipeId']
    this.mealDetails$ = this.mealSvc.getMealDetails(this.username, this.recipeId)
  }

}
