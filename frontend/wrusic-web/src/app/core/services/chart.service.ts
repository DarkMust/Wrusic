import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartEntry } from '../models/chart-entry.model';
import { environment } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ChartService {
  private apiUrl = `${environment.apiUrl}/charts`;

  constructor(private http: HttpClient) { }

  getGlobalChart(chartType: string, date: Date): Observable<ChartEntry[]> {
    const params = new HttpParams()
      .set('chartType', chartType)
      .set('date', date.toISOString().split('T')[0]);

    return this.http.get<ChartEntry[]>(`${this.apiUrl}/global`, { params });
  }

  getCountryChart(country: string, chartType: string, date: Date): Observable<ChartEntry[]> {
    const params = new HttpParams()
      .set('chartType', chartType)
      .set('date', date.toISOString().split('T')[0]);

    return this.http.get<ChartEntry[]>(`${this.apiUrl}/country/${country}`, { params });
  }

  getAvailableChartDates(chartType: string): Observable<Date[]> {
    return this.http.get<Date[]>(`${this.apiUrl}/dates`, {
      params: { chartType }
    });
  }

  getAvailableChartDatesByCountry(country: string, chartType: string): Observable<Date[]> {
    return this.http.get<Date[]>(`${this.apiUrl}/dates/country/${country}`, {
      params: { chartType }
    });
  }

  createChartEntry(chartEntry: ChartEntry): Observable<ChartEntry> {
    return this.http.post<ChartEntry>(this.apiUrl, chartEntry);
  }

  deleteChartEntry(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
} 