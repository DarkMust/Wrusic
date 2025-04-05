import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Album } from '../models/album.model';
import { environment } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AlbumService {
  private apiUrl = `${environment.apiUrl}/albums`;

  constructor(private http: HttpClient) { }

  getAllAlbums(): Observable<Album[]> {
    return this.http.get<Album[]>(this.apiUrl);
  }

  getAlbumById(id: number): Observable<Album> {
    return this.http.get<Album>(`${this.apiUrl}/${id}`);
  }

  createAlbum(album: Album): Observable<Album> {
    return this.http.post<Album>(this.apiUrl, album);
  }

  updateAlbum(id: number, album: Album): Observable<Album> {
    return this.http.put<Album>(`${this.apiUrl}/${id}`, album);
  }

  deleteAlbum(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAlbumsByArtist(artistId: number): Observable<Album[]> {
    return this.http.get<Album[]>(`${this.apiUrl}/artist/${artistId}`);
  }

  getAlbumsByGenre(genre: string): Observable<Album[]> {
    return this.http.get<Album[]>(`${this.apiUrl}/genre/${genre}`);
  }

  searchAlbums(title: string): Observable<Album[]> {
    return this.http.get<Album[]>(`${this.apiUrl}/search`, {
      params: { title }
    });
  }

  getLatestReleases(startDate: Date, endDate: Date, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString().split('T')[0])
      .set('endDate', endDate.toISOString().split('T')[0])
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/latest`, { params });
  }
} 