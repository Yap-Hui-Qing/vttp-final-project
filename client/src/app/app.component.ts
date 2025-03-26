import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { OverlayPanel } from 'primeng/overlaypanel';
import { AuthStore } from './components/auth/auth.store';
import { take } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './components/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  private authStore = inject(AuthStore)
  private messageSvc = inject(MessageService)
  private router = inject(Router)
  private authSvc = inject(AuthService)

  @ViewChild('userMenu') userMenu!: OverlayPanel;
  isMenuVisible = false
  isLoggingOut: boolean = false
  showMenuBar: boolean = true
  username!: string
  menuItems: MenuItem[] = []
  id!: number 

  ngOnInit(): void {
    this.authStore.getUsername$.subscribe(
      (result) => {
        this.username = result
        console.info('>>> username: ', this.username)
        this.updateMenuItems()
      }
    )
    console.info('>>> username: ', this.username)
  }

  updateMenuItems() {
    this.menuItems = [
      {
        label: 'Home',
        icon: 'pi pi-calendar',
        routerLink: ['/home']
      },
      {
        label: 'Build',
        icon: 'pi pi-hammer',
        routerLink: ['/build', this.username]
      },
      {
        label: 'Groceries',
        icon: 'pi pi-shopping-cart',
        routerLink: ['/groceries']
      }
    ]
  }

  protected username$ = this.authStore.getUsername$.pipe(
    take(1)
  )

  toggleMenuBarVisibility(visible: boolean) {
    this.showMenuBar = visible;
  }

  toggleUserMenu($event: MouseEvent) {
    if (this.isMenuVisible) {
      this.userMenu.hide();
    } else {
      this.userMenu.show(event);
    }
    this.isMenuVisible = !this.isMenuVisible
  }

  protected logout() {
    console.log('>>> Logging out...')

    // clear local storage
    localStorage.removeItem('username')
    localStorage.removeItem('userPreference')
    localStorage.removeItem('userPlan')

    this.isLoggingOut = true
    this.messageSvc.add({ severity: 'success', summary: 'Success', detail: 'Logging out...' })

    setTimeout(() => {
      this.authStore.logout()
      this.isLoggingOut = false
    }, 2000);

    this.router.navigate(['/'])
  }
}
