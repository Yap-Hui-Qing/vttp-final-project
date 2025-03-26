import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AppComponent } from '../../../app.component';

@Component({
  selector: 'app-landing',
  standalone: false,
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit, OnDestroy{

  private appComponent = inject(AppComponent)

  ngOnInit(): void {
    this.appComponent.toggleMenuBarVisibility(false)
  }

  ngOnDestroy(): void {
    this.appComponent.toggleMenuBarVisibility(true)
  }
}
