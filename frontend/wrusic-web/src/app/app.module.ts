import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppComponent } from './app.component';
import { ChartsComponent } from './features/charts/charts.component';
import { NewReleasesComponent } from './features/new-releases/new-releases.component';
import { ArtistsComponent } from './features/artists/artists.component';
import { AlbumsComponent } from './features/albums/albums.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: '', redirectTo: '/new-releases', pathMatch: 'full' },
      { path: 'new-releases', component: NewReleasesComponent },
      { path: 'charts', component: ChartsComponent },
      { path: 'artists', component: ArtistsComponent },
      { path: 'albums', component: AlbumsComponent }
    ]),
    provideAnimations(),
    provideHttpClient(
      withInterceptors([
        (req, next) => {
          // Add CORS headers to all requests
          const corsReq = req.clone({
            setHeaders: {
              'Access-Control-Allow-Origin': '*',
              'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
              'Access-Control-Allow-Headers': 'Origin, Content-Type, Accept, Authorization'
            }
          });
          return next(corsReq);
        }
      ])
    )
  ]
});
