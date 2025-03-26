import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { UserStore } from '../../../user.store';
import { Preference } from '../../../models';
import { Observable, take } from 'rxjs';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PreferenceService } from '../../../services/preferences.service';
import { ActivatedRoute, Router } from '@angular/router';
import { OverlayPanel } from 'primeng/overlaypanel';
import { MenuItem, MessageService } from 'primeng/api';
import { AuthStore } from '../../auth/auth.store';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  private userStore = inject(UserStore)
  private preferenceSvc = inject(PreferenceService)
  private activatedRoute = inject(ActivatedRoute)
  private router = inject(Router)
  private authStore = inject(AuthStore)
  private messageSvc = inject(MessageService)

  @ViewChild('userMenu') userMenu!: OverlayPanel;
  isMenuVisible = false
  isEditing = false
  protected username!: string
  protected preference$!: Observable<Preference>
  protected editedPreference: Preference = {
    diet: '',
    allergies: '',
    serving: 2,
  }
  protected selectedAllergies: string[] = [''];
  protected isLoggingOut = false

  ngOnInit(): void {
    console.info(this.selectedAllergies)
    this.activatedRoute.params.subscribe(
      params => this.username = params['username']
    )
    this.userStore.loadUserPreference(this.username)
    this.preference$ = this.userStore.preference$
    this.userStore.preference$.subscribe(
      (result) => {
        console.info('>>> preference from component store: ', result)
        this.editedPreference = result
        if (this.editedPreference.allergies) {
          console.info('>>> edited preferences: ', this.editedPreference)
          this.selectedAllergies = this.editedPreference.allergies.split(',') || [''];
        } else {
          this.selectedAllergies = ['']
        }
        this.disableAllergyOptions()
      }
    )
  }

  dietOptions: any[] = [
    { label: 'Casual - No holds barred', value: '' },
    { label: 'Keto - Ultra low carb', value: 'ketogenic' },
    { label: 'Vegetarian', value: 'vegetarian' },
    { label: 'Vegan - Only plants', value: 'vegan' },
    { label: 'Pescetarian - Vegetarian + seafood', value: 'pescetarian' },
    { label: 'Paleo - Eat like a caveman', value: 'paleo' },
  ];

  sizeOptions: any[] = [
    { label: '2 servings - for two, or one with leftovers', value: 2 },
    { label: '4 servings - for four, or two-three with leftover', value: 4 },
    { label: '6 servings - for a family of 5+', value: 6 },
  ];

  allergiesOptions: any[] = [
    { label: 'No Allergies', value: '' },
    { label: 'Dairy', value: 'dairy' },
    { label: 'Egg', value: 'egg' },
    { label: 'Gluten', value: 'gluten' },
    { label: 'Peanut', value: 'peanut' },
    { label: 'Seafood', value: 'seafood' },
    { label: 'Soy', value: 'soy' },
    { label: 'Tree Nut', value: 'tree nut' }
  ]

  protected onAllergiesChange($event: string[]) {
    console.info('>>> event value: ', $event)
    this.selectedAllergies = $event
    console.info('>>> selected allergies: ', this.selectedAllergies)
    this.editedPreference = {
      ...this.editedPreference,
      allergies: this.selectedAllergies.length > 0 ? this.selectedAllergies.join(',') : ''
    };
    console.log('Updated allergies:', this.editedPreference.allergies);

    this.disableAllergyOptions();
  }

  private disableAllergyOptions() {
    this.allergiesOptions = this.allergiesOptions.map(option => {
      // Disable "No Allergies" if any other allergy is selected
      if (this.selectedAllergies.length > 0 && !this.selectedAllergies.includes('') && option.value === '') {
        option.disabled = true;
      }
      // Disable all allergies except "No Allergies" if "No Allergies" is selected
      else if (this.selectedAllergies.includes('')) {
        if (option.value !== '') {
          option.disabled = true;
        }
      }
      // Enable all allergies if neither condition is true
      else {
        option.disabled = false;
      }
      return option;
    });
  }

  protected startEditing() {
    // initialise with the current values
    this.userStore.preference$.pipe(take(1)).subscribe((preference) => {
      this.editedPreference = { ...preference };
      this.selectedAllergies = preference.allergies ? preference.allergies.split(',') : ['']; // Ensure default allergies
    });

    this.isEditing = true
    this.disableAllergyOptions()
  }

  protected savePreferences(): void {
    this.preferenceSvc.updateUserPreference(this.username, this.editedPreference)
    this.userStore.setPreference(this.editedPreference)
    this.isEditing = false
  }

  protected cancelEditing(): void {
    this.isEditing = false
  }

  toggleUserMenu($event: MouseEvent) {
    if (this.isMenuVisible) {
      this.userMenu.hide();
    } else {
      this.userMenu.show(event);
    }
    this.isMenuVisible = !this.isMenuVisible
  }

  menuItems: MenuItem[] = [
    {
      label: 'Home',
      icon: 'pi pi-calendar',
      routerLink: ['/home']
    },
    {
      label: 'Build',
      icon: 'pi pi-hammer',
      routerLink: ['/build']
    },
    {
      label: 'Groceries',
      icon: 'pi pi-shopping-cart',
      routerLink: ['/groceries']
    }, {
      label: 'Favourites',
      icon: 'pi pi-heart',
      routerLink: ['/favourites']
    }
  ]

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
