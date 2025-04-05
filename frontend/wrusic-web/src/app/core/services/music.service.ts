import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Album {
  id: number;
  title: string;
  coverUrl: string;
  artistName: string;
  releaseDate: string;
}

export interface Track {
  id: number;
  title: string;
  imageUrl: string;
  artistName: string;
  albumName: string;
  previewUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class MusicService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getNewReleases(country: string = 'US', limit: number = 20): Observable<Album[]> {
    return this.http.get<Album[]>(`${this.apiUrl}/music/new-releases`, {
      params: { country, limit: limit.toString() }
    });
  }

  getTopTracks(country: string = 'US', limit: number = 50): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/music/charts/${country}`, {
      params: { limit: limit.toString() }
    });
  }
} 