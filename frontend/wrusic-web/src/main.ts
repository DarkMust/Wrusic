import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { ChartsComponent } from './app/features/charts/charts.component';
import { NewReleasesComponent } from './app/features/new-releases/new-releases.component';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: '', redirectTo: '/new-releases', pathMatch: 'full' },
      { path: 'new-releases', component: NewReleasesComponent },
      { path: 'charts', component: ChartsComponent }
    ]),
    provideAnimations(),
    provideHttpClient()
  ]
});
