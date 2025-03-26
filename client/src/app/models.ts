export interface User {
    username: string
    password: string
    email: string
}

export interface Auth {
    username: string
    token: string
}

export interface Preference {
    diet: string,
    allergies: string,
    serving: number
}

export interface Day {
    label: string // Monday
    date: string // 2025-03-25
}

export interface Meal {
    mealDate: string,
    mealTime: string
    mealDetails: Recipe
}

export interface MealPlan {
    meals: Meal[]
}

export interface ingredient {
    item: string,
    quantity: number
}

export interface IngredientList {
    ingredients: ingredient[]
}

export interface Product {
    id: number,
    name: string,
    price: number,
    image: string,
    category: string,
    description: string
}

export interface CartItem extends Product {
    quantity: number;
}
  
export interface StripeResponse {
    status: string;
    message: string;
    sessionId: string;
    sessionUrl: string;
}

export interface UserAccount {
    username: string,
    preference: Preference
    plan: MealPlan
    ingredients: IngredientList
    cart: CartItem[]
}

export interface Recipe {
    id: number,
    title: string,
    readyInMinutes: number,
    servings: number,
    image: string,
    ingredients: Ingredient[],
    instructions: Instruction[],
    cuisines: Cuisine[]
}

export interface Ingredient {
    name: string;
    quantity: number,
    unit: string,
    status: string
}

export interface Instruction {
    [stepKey: string] : string;
}

export interface Cuisine {
    cuisine: string;
}

export interface StripeResponse {
    status: string,
    message: string,
    sessionId: string,
    sessionUrl: string
}
