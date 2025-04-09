import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MusicService } from '../../core/services/music.service';
import { Artist } from '../../core/models/artist.model';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrls: ['./artists.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class ArtistsComponent implements OnInit {
  artists: Artist[] = [];
  loading = false;
  error: string | null = null;

  constructor(private musicService: MusicService) {}

  ngOnInit(): void {
    this.loadArtists();
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/images/default-artist.svg';
  }

  private loadArtists(): void {
    this.loading = true;
    this.error = null;
    
    this.musicService.getTopArtists().subscribe({
      next: (artists: Artist[]) => {
        this.artists = artists;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load artists. Please try again later.';
        this.loading = false;
        console.error('Error loading artists:', err);
      }
    });
  }
} 