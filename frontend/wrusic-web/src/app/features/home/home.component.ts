import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MusicService } from '../../core/services/music.service';
import { CountryService } from '../../core/services/country.service';
import { Country } from '../../core/models/country.model';
import { Album } from '../../core/models/album.model';
import { Track } from '../../core/models/track.model';
import { Artist } from '../../core/models/artist.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class HomeComponent implements OnInit {
  countries: Country[] = [];
  selectedCountry: string = 'united states';
  newReleases: Album[] = [];
  topTracks: Track[] = [];
  topAlbums: Album[] = [];
  topArtists: Artist[] = [];
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private musicService: MusicService,
    private countryService: CountryService
  ) {}

  ngOnInit(): void {
    this.loadCountries();
    this.loadMusicData();
  }

  private loadCountries(): void {
    this.countries = this.countryService.getCountries();
  }

  onCountryChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedCountry = select.value;
    this.loadMusicData();
  }

  retryLoading(): void {
    this.loadMusicData();
  }

  private loadMusicData(): void {
    this.isLoading = true;
    this.error = null;

    forkJoin({
      newReleases: this.musicService.getNewReleases(this.selectedCountry),
      topTracks: this.musicService.getTopTracks(this.selectedCountry),
      topAlbums: this.musicService.getTopAlbums(this.selectedCountry),
      topArtists: this.musicService.getTopArtists(this.selectedCountry)
    }).subscribe({
      next: (data) => {
        this.newReleases = data.newReleases;
        this.topTracks = data.topTracks;
        this.topAlbums = data.topAlbums;
        this.topArtists = data.topArtists;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load music data. Please try again.';
        this.isLoading = false;
        console.error('Error loading music data:', error);
      }
    });
  }
} 