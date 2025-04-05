import { Component, OnInit } from '@angular/core';
import { MusicService, Album } from '../../core/services/music.service';

@Component({
  selector: 'app-new-releases',
  templateUrl: './new-releases.component.html',
  styleUrls: ['./new-releases.component.scss']
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
} 