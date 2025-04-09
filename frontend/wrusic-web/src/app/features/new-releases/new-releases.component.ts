import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MusicService } from '../../core/services/music.service';
import { Album } from '../../core/models/album.model';

@Component({
  selector: 'app-new-releases',
  templateUrl: './new-releases.component.html',
  styleUrls: ['./new-releases.component.scss'],
  standalone: true,
  imports: [CommonModule, DatePipe]
})
export class NewReleasesComponent implements OnInit {
  newReleases: Album[] = [];
  loading = false;
  error: string | null = null;

  constructor(private musicService: MusicService) {}

  ngOnInit(): void {
    this.loadNewReleases();
  }

  loadNewReleases(): void {
    this.loading = true;
    this.error = null;
    
    this.musicService.getNewReleases().subscribe({
      next: (albums) => {
        this.newReleases = albums;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load new releases. Please try again later.';
        this.loading = false;
        console.error('Error loading new releases:', err);
      }
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/images/default-album.svg';
  }
} 