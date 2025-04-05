import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  constructor(private router: Router) { }

  navigateToHome(): void {
    this.router.navigate(['/']);
  }

  navigateToCharts(): void {
    this.router.navigate(['/charts']);
  }

  navigateToReleases(): void {
    this.router.navigate(['/releases']);
  }

  navigateToArtists(): void {
    this.router.navigate(['/artists']);
  }

  navigateToAlbums(): void {
    this.router.navigate(['/albums']);
  }
} 