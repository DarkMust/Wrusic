import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MusicService } from '../../core/services/music.service';
import { Album } from '../../core/models/album.model';

@Component({
  selector: 'app-albums',
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class AlbumsComponent implements OnInit {
  albums: Album[] = [];
  loading = false;
  error: string | null = null;

  constructor(private musicService: MusicService) {}

  ngOnInit(): void {
    this.loadAlbums();
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/images/default-album.svg';
  }

  private loadAlbums(): void {
    this.loading = true;
    this.error = null;
    
    this.musicService.getTopAlbums().subscribe({
      next: (albums) => {
        this.albums = albums;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load albums. Please try again later.';
        this.loading = false;
        console.error('Error loading albums:', err);
      }
    });
  }
} 