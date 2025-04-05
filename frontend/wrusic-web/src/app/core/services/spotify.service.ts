import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Artist } from '../models/artist.model';
import { Album } from '../models/album.model';
import { Track } from '../models/track.model';

@Injectable({
  providedIn: 'root'
})
export class SpotifyService {
  private apiUrl = `${environment.apiUrl}/spotify`;

  constructor(private http: HttpClient) { }

  /**
   * Get the Spotify authorization URL
   */
  getAuthorizationUrl(): Observable<{ authorizationUrl: string, state: string }> {
    return this.http.get<{ authorizationUrl: string, state: string }>(`${this.apiUrl}/auth/spotify/authorize`);
  }

  /**
   * Get the current user's Spotify profile
   */
  getProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/spotify/profile`);
  }

  /**
   * Get the current user's Spotify access token
   */
  getToken(): Observable<{ accessToken: string }> {
    return this.http.get<{ accessToken: string }>(`${this.apiUrl}/auth/spotify/token`);
  }

  /**
   * Search for artists
   */
  searchArtists(query: string): Observable<Artist[]> {
    return this.http.get<Artist[]>(`${this.apiUrl}/search/artists`, {
      params: { query }
    });
  }

  /**
   * Get artist details
   */
  getArtist(artistId: string): Observable<Artist> {
    return this.http.get<Artist>(`${this.apiUrl}/artists/${artistId}`);
  }

  /**
   * Get artist's albums
   */
  getArtistAlbums(artistId: string): Observable<Album[]> {
    return this.http.get<Album[]>(`${this.apiUrl}/artists/${artistId}/albums`);
  }

  /**
   * Get album tracks
   */
  getAlbumTracks(albumId: string): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/albums/${albumId}/tracks`);
  }

  /**
   * Get artist's top tracks
   */
  getArtistTopTracks(artistId: string): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/artists/${artistId}/top-tracks`);
  }
} 