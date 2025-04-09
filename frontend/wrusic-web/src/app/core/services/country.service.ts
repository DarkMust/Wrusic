import { Injectable } from '@angular/core';
import { Country } from '../models/country.model';

@Injectable({
  providedIn: 'root'
})
export class CountryService {
  private countries: Country[] = [
    { code: 'united states', name: 'United States' },
    { code: 'united kingdom', name: 'United Kingdom' },
    { code: 'france', name: 'France' },
    { code: 'germany', name: 'Germany' },
    { code: 'japan', name: 'Japan' },
    { code: 'canada', name: 'Canada' },
    { code: 'australia', name: 'Australia' },
    { code: 'brazil', name: 'Brazil' },
    { code: 'india', name: 'India' },
    { code: 'russia', name: 'Russia' }
  ];

  getCountries(): Country[] {
    return this.countries;
  }
} 