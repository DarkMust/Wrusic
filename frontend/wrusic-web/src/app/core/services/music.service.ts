import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Album } from '../models/album.model';
import { Track } from '../models/track.model';
import { Artist } from '../models/artist.model';

@Injectable({
  providedIn: 'root'
})
export class MusicService {
  private apiUrl = environment.apiUrl;
  private defaultAlbumImage = 'assets/images/default-album.svg';
  private defaultArtistImage = 'assets/images/default-artist.svg';

  constructor(private http: HttpClient) {}

  private getImageUrl(imageData: any, isArtist: boolean = false): string {
    const defaultImage = isArtist ? this.defaultArtistImage : this.defaultAlbumImage;

    if (!imageData) return defaultImage;
    
    if (Array.isArray(imageData)) {
      // Sort images by size if available
      const sortedImages = [...imageData].sort((a, b) => {
        const sizeA = a.width || 0;
        const sizeB = b.width || 0;
        return sizeB - sizeA;
      });

      // Try to find the best image
      const bestImage = sortedImages[0];
      if (bestImage && bestImage.url) {
        return bestImage.url;
      }
    } else if (typeof imageData === 'object') {
      if (imageData.url) {
        return imageData.url;
      }
      if (imageData['#text']) {
        return imageData['#text'];
      }
    } else if (typeof imageData === 'string' && imageData.trim() !== '') {
      return imageData;
    }
    
    return defaultImage;
  }

  getNewReleases(country: string = 'US', limit: number = 20): Observable<Album[]> {
    return this.http.get<any>(`${this.apiUrl}/music/new-releases`, {
      params: { country, limit: limit.toString() }
    }).pipe(
      map(response => {
        if (response && response.albums && response.albums.album) {
          return response.albums.album.map((album: any) => ({
            id: album.mbid || album.name,
            name: album.name || 'Unknown Album',
            images: [{ url: this.getImageUrl(album.image || album.images) }],
            artists: [{
              id: album.artist?.mbid || album.artist?.name || 'unknown',
              name: album.artist?.name || 'Unknown Artist'
            }],
            release_date: album.releasedate || album.release_date || 'Unknown'
          }));
        }
        return [];
      }),
      catchError(error => {
        console.error('Error fetching new releases:', error);
        throw error;
      })
    );
  }

  getTopTracks(country: string = 'US', limit: number = 50): Observable<Track[]> {
    return this.http.get<any>(`${this.apiUrl}/music/charts`, {
      params: { country, limit: limit.toString() }
    }).pipe(
      map(response => {
        if (response && response.toptracks && response.toptracks.track) {
          return response.toptracks.track.map((track: any) => ({
            id: track.mbid || track.name,
            name: track.name || 'Unknown Track',
            artists: [{
              id: track.artist?.mbid || track.artist?.name || 'unknown',
              name: track.artist?.name || 'Unknown Artist'
            }],
            album: {
              id: track.album?.mbid || track.album?.name || 'unknown',
              name: track.album?.name || 'Unknown Album',
              images: [{ url: this.getImageUrl(track.album?.image || track.album?.images) }]
            }
          }));
        }
        return [];
      }),
      catchError(error => {
        console.error('Error fetching top tracks:', error);
        throw error;
      })
    );
  }

  getTopAlbums(country: string = 'US', limit: number = 50): Observable<Album[]> {
    return this.http.get<any>(`${this.apiUrl}/music/top-albums`, {
      params: { country, limit: limit.toString() }
    }).pipe(
      map(response => {
        if (response && response.topalbums && response.topalbums.album) {
          return response.topalbums.album.map((album: any) => ({
            id: album.mbid || album.name,
            name: album.name || 'Unknown Album',
            images: [{ url: this.getImageUrl(album.image || album.images) }],
            artists: [{
              id: album.artist?.mbid || album.artist?.name || 'unknown',
              name: album.artist?.name || 'Unknown Artist'
            }],
            release_date: album.releasedate || album.release_date || 'Unknown'
          }));
        }
        return [];
      }),
      catchError(error => {
        console.error('Error fetching top albums:', error);
        throw error;
      })
    );
  }

  getTopArtists(country: string = 'US', limit: number = 50): Observable<Artist[]> {
    return this.http.get<any>(`${this.apiUrl}/music/top-artists`, {
      params: { country, limit: limit.toString() }
    }).pipe(
      map(response => {
        if (response && response.topartists && response.topartists.artist) {
          return response.topartists.artist.map((artist: any) => ({
            id: artist.mbid || artist.name,
            name: artist.name || 'Unknown Artist',
            images: [{ url: this.getImageUrl(artist.image || artist.images, true) }]
          }));
        }
        return [];
      }),
      catchError(error => {
        console.error('Error fetching top artists:', error);
        throw error;
      })
    );
  }
} 