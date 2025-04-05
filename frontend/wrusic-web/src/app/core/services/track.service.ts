import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Track } from '../models/track.model';
import { environment } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class TrackService {
  private apiUrl = `${environment.apiUrl}/tracks`;

  constructor(private http: HttpClient) { }

  getAllTracks(): Observable<Track[]> {
    return this.http.get<Track[]>(this.apiUrl);
  }

  getTrackById(id: number): Observable<Track> {
    return this.http.get<Track>(`${this.apiUrl}/${id}`);
  }

  createTrack(track: Track): Observable<Track> {
    return this.http.post<Track>(this.apiUrl, track);
  }

  updateTrack(id: number, track: Track): Observable<Track> {
    return this.http.put<Track>(`${this.apiUrl}/${id}`, track);
  }

  deleteTrack(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getTracksByArtist(artistId: number): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/artist/${artistId}`);
  }

  getTracksByGenre(genre: string): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/genre/${genre}`);
  }

  getTracksByCountry(country: string): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/country/${country}`);
  }

  getLatestReleases(startDate: Date, endDate: Date, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString().split('T')[0])
      .set('endDate', endDate.toISOString().split('T')[0])
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/latest`, { params });
  }

  getGlobalCharts(): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/charts/global`);
  }

  getCountryCharts(country: string): Observable<Track[]> {
    return this.http.get<Track[]>(`${this.apiUrl}/charts/country/${country}`);
  }
} 