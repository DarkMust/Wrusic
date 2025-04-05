import { Track } from './track.model';

export interface ChartEntry {
  id?: number;
  track: Track;
  position: number;
  chartType: string;
  country?: string;
  entryDate: Date;
  previousPosition?: number;
  weeksOnChart?: number;
} 