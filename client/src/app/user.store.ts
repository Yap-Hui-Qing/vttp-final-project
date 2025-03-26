import { inject, Injectable } from "@angular/core";
import { Meal, Preference, UserAccount } from "./models";
import { ComponentStore } from "@ngrx/component-store";
import { AuthStore } from "./components/auth/auth.store";
import { catchError, Observable, of, switchMap, tap, throwError } from "rxjs";
import { PreferenceService } from "./services/preferences.service";

const INIT: UserAccount = {
    username: '',
    preference: {
        diet: '',
        allergies: '',
        serving: 2
    },
    plan: {
        meals: []
    },
    ingredients: {
        ingredients: [],
    },
    cart: []
}

@Injectable()
export class UserStore extends ComponentStore<UserAccount> {

    private preferenceSvc = inject(PreferenceService)
    constructor() { super(INIT) }

    // updators
    readonly setUsername = this.updater(
        (state, username: string) => {
            localStorage.setItem('username', JSON.stringify(username));
            return {
                ...state,
                username
            }
        })

    readonly setPreference = this.updater(
        (state, preference: Preference) => {
            localStorage.setItem('userPreference', JSON.stringify(preference));
            return {
                ...state,
                preference
            }
        }
    )

    readonly addMeal = this.updater<Meal>(
        (store: UserAccount, newMeal: Meal) => {

            const updatedPlan = {
                ...store.plan,
                meals: [...store.plan.meals, newMeal]
            };

            localStorage.setItem('userPlan', JSON.stringify(updatedPlan));

            return {
                ...store,
                plan: updatedPlan
            } as UserAccount
        }
    )

    readonly addMeals = this.updater<Meal[]>(
        (store: UserAccount, newMeals: Meal[]) => {
            const updatedPlan = {
                ...store.plan,
                meals: [...store.plan.meals, ...newMeals]
            }
            localStorage.setItem('userPlan', JSON.stringify(updatedPlan))
            return {
                ...store,
                plan: updatedPlan
            } as UserAccount;
        }
    )

    readonly deleteMeal = this.updater<{ mealDate: string, mealTime: string }>(
        (store: UserAccount, { mealDate, mealTime }) => {

            const updatedPlan = {
                ...store.plan,
                meals: store.plan.meals.filter(m =>
                    !(m.mealDate === mealDate && m.mealTime === mealTime)
                )
            };

            localStorage.setItem('userPlan', JSON.stringify(updatedPlan));

            return {
                ...store,
                plan: updatedPlan
            } as UserAccount
        }
    )

    readonly loadUserPreference = this.effect((username$: Observable<string>) =>
        username$.pipe(
            switchMap(username => {
                console.info('loading user preferences for : ', username)

                const savedPreference = localStorage.getItem('userPreference');
                if (savedPreference) {
                    console.info('Found saved preference in localStorage');
                    return of(JSON.parse(savedPreference) as Preference);
                }

                return this.preferenceSvc.getuserPreference(username).pipe(
                    catchError(error => {
                        if (error.status === 404) {
                            console.info("No preferences found. Prompting user to set preferences.");
                            this.preferenceSvc.promptUserForPreference(username);
                            const defaultPreference: Preference = {
                                diet: '',
                                allergies: '',
                                serving: 2
                            };

                            return of(defaultPreference);
                        }
                        return throwError(() => error)
                    })
                )
            }
            ),
            tap((preference: Preference) => {
                if (preference) {
                    console.info('>>> setting prefernce: ', preference)
                    this.setPreference(preference)
                }
            })
        )
    );

    // selectors
    readonly username$ = this.select((state) => {
        const savedUsername = localStorage.getItem('username');
        if (savedUsername) {
            console.info('Loaded username from localStorage');
            return JSON.parse(savedUsername) as string;
        }

        console.info('Loaded username from state');
        return state.username
    })

    readonly preference$ = this.select((state) => {
        const savedPreference = localStorage.getItem('userPreference');
        if (savedPreference) {
            console.info('Loaded preference from localStorage');
            return JSON.parse(savedPreference) as Preference;
        }

        // Fall back to the state preference
        console.info('Loaded preference from state');
        return state.preference;
    })

    readonly getPlan = this.select<Meal[]>(
        (store: UserAccount) => {
            const savedPlan = localStorage.getItem('userPlan');
            if (savedPlan)
                return JSON.parse(savedPlan).meals as Meal[]
            return store.plan.meals
        }
    )
}