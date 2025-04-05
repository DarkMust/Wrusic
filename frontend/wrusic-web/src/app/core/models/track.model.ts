import { Artist } from './artist.model';
import { Album } from './album.model';

export interface Track {
  id?: number;
  title: string;
  description?: string;
  coverImageUrl?: string;
  audioUrl?: string;
  releaseDate?: Date;
  duration?: number;
  genre?: string;
  chartPosition?: number;
  country?: string;
  artist?: Artist;
  albums?: Album[];
  chartEntries?: any[];
} 