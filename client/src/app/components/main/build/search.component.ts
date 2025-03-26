import { Component, inject, OnInit } from '@angular/core';
import { RecipeService } from '../../../services/recipes.service';
import { UserStore } from '../../../user.store';
import { Instruction, Meal, Preference, Recipe } from '../../../models';
import { filter } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {

  private recipeSvc = inject(RecipeService)
  private userStore = inject(UserStore)
  private activatedRoute = inject(ActivatedRoute)
  private router = inject(Router)

  protected preference!: Preference
  protected recipes!: Recipe[]
  protected filteredRecipes!: Recipe[]
  displayDialog: boolean = false;
  selectedRecipe!: Recipe
  mealDate!: string 
  mealTime!: string
  username!: string

  // pagination
  page: number = 1;
  rowsPerPage: number = 10

  cuisines!: any
  selectedCuisine: string = 'All';

  ngOnInit(): void {
    this.userStore.preference$.subscribe({
      next: (result) => {
        this.preference = result;
        console.info('>>> preference params: ', this.preference);
        
        console.log('Sending API request with preferences: ', this.preference);
  
        this.recipeSvc.getRecipeSearch(this.preference).subscribe({
          next: (response) => {
            this.recipes = response;
            this.extractCuisines();
            this.filteredRecipes = this.recipes
            console.info('>>> recipes: ', this.recipes)
          },
          error: (error) => {
            console.error('Error fetching recipes: ', error);
          }
        });
      },
      error: (error) => {
        console.error('Error fetching preferences: ', error);
      }
      
    });

    this.activatedRoute.params.subscribe(
      params => {
        this.mealDate = params['mealDate']
        this.mealTime = params['mealTime']
      }
    )

    this.userStore.username$.subscribe(
      (name) => this.username = name
    )
  }

  extractCuisines(): void {
    const cuisineSet = new Set<string>();
  
    this.recipes.forEach(recipe => {
      recipe.cuisines.forEach(c => {   
        if (c.cuisine.trim()) { 
        cuisineSet.add(c.cuisine);
      }});
    });
    this.cuisines = ['All'].concat([...cuisineSet])
    console.info(cuisineSet)
  }

  applyFilter() {
    if (this.selectedCuisine === 'All'){
      this.filteredRecipes = this.recipes;
    } else {
      this.filteredRecipes = this.recipes.filter(recipe => 
        recipe.cuisines.some(c => c.cuisine === this.selectedCuisine))
    }
  }

  get paginatedRecipes(){
    const startIndex = (this.page - 1) * this.rowsPerPage
    return this.filteredRecipes.slice(startIndex, startIndex + this.rowsPerPage)
  }

  changePage(offset: number) {
    this.page += offset
  }

  get totalPages(): number {
    return Math.ceil(this.filteredRecipes.length / this.rowsPerPage);
  }

  add(recipe: Recipe): void {
    const meal: Meal = {
      mealDate: this.mealDate,
      mealTime: this.mealTime,
      mealDetails: recipe

    }
    console.info('add recipe: ', recipe)
    this.userStore.addMeal(meal)
    this.router.navigate(['/build', this.username])
  }

  viewDetails(recipe: Recipe): void {
    this.selectedRecipe = recipe;
    this.displayDialog = true
  }

  getInstructionKeys(instructions: Instruction): string[] {
    return Object.keys(instructions).sort(); // Ensure steps are in order
  }
  
}
