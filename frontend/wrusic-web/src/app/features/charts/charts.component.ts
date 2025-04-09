import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MusicService } from '../../core/services/music.service';
import { Track } from '../../core/models/track.model';

@Component({
  selector: 'app-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class ChartsComponent implements OnInit {
  topTracks: Track[] = [];
  loading = false;
  error: string | null = null;
  selectedCountry = 'US';
  private audio: HTMLAudioElement | null = null;

  countries = [
    { code: 'US', name: 'United States' },
    { code: 'GB', name: 'United Kingdom' },
    { code: 'FR', name: 'France' },
    { code: 'DE', name: 'Germany' },
    { code: 'JP', name: 'Japan' }
  ];

  constructor(private musicService: MusicService) {}

  ngOnInit(): void {
    this.loadTopTracks();
  }

  onCountryChange(country: string): void {
    this.selectedCountry = country;
    this.loadTopTracks();
  }

  playPreview(track: Track): void {
    if (this.audio) {
      this.audio.pause();
      this.audio = null;
    }

    if (track.preview_url) {
      this.audio = new Audio(track.preview_url);
      this.audio.play();
    }
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/images/default-album.svg';
  }

  private loadTopTracks(): void {
    this.loading = true;
    this.error = null;
    
    this.musicService.getTopTracks(this.selectedCountry).subscribe({
      next: (tracks) => {
        this.topTracks = tracks;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load top tracks. Please try again later.';
        this.loading = false;
        console.error('Error loading top tracks:', err);
      }
    });
  }
} 