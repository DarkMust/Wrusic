import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { SpotifyService } from './spotify.service';

export interface SpotifyProfile {
  id: string;
  displayName: string;
  email: string;
  imageUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private profileSubject = new BehaviorSubject<SpotifyProfile | null>(null);
  private tokenSubject = new BehaviorSubject<string | null>(null);

  constructor(
    private http: HttpClient,
    private spotifyService: SpotifyService
  ) {
    // Check if we have a stored profile and token
    const storedProfile = localStorage.getItem('spotifyProfile');
    const storedToken = localStorage.getItem('spotifyToken');
    
    if (storedProfile) {
      this.profileSubject.next(JSON.parse(storedProfile));
    }
    
    if (storedToken) {
      this.tokenSubject.next(storedToken);
    }
  }

  /**
   * Get the current user's Spotify profile
   */
  get profile$(): Observable<SpotifyProfile | null> {
    return this.profileSubject.asObservable();
  }

  /**
   * Get the current user's Spotify access token
   */
  get token$(): Observable<string | null> {
    return this.tokenSubject.asObservable();
  }

  /**
   * Check if the user is authenticated
   */
  get isAuthenticated(): boolean {
    return !!this.tokenSubject.value;
  }

  /**
   * Initiate Spotify login
   */
  loginWithSpotify(): void {
    this.spotifyService.getAuthorizationUrl().subscribe(
      response => {
        // Store the state in localStorage to verify it when the user returns
        localStorage.setItem('spotifyState', response.state);
        
        // Redirect to Spotify authorization page
        window.location.href = response.authorizationUrl;
      },
      error => {
        console.error('Error getting authorization URL:', error);
      }
    );
  }

  /**
   * Handle the OAuth callback
   */
  handleCallback(code: string, state: string): Observable<boolean> {
    // Verify the state to prevent CSRF attacks
    const storedState = localStorage.getItem('spotifyState');
    if (state !== storedState) {
      console.error('State mismatch, possible CSRF attack');
      return of(false);
    }
    
    // Clear the stored state
    localStorage.removeItem('spotifyState');
    
    // Get the user's profile and token
    return this.spotifyService.getProfile().pipe(
      tap(profile => {
        this.profileSubject.next(profile);
        localStorage.setItem('spotifyProfile', JSON.stringify(profile));
      }),
      tap(() => this.spotifyService.getToken().subscribe(
        response => {
          this.tokenSubject.next(response.accessToken);
          localStorage.setItem('spotifyToken', response.accessToken);
        }
      )),
      tap(() => of(true)),
      catchError(error => {
        console.error('Error handling callback:', error);
        return of(false);
      })
    );
  }

  /**
   * Logout the user
   */
  logout(): void {
    this.profileSubject.next(null);
    this.tokenSubject.next(null);
    localStorage.removeItem('spotifyProfile');
    localStorage.removeItem('spotifyToken');
  }
} 