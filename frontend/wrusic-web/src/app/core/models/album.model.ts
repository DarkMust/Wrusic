import { Artist } from './artist.model';
import { Track } from './track.model';

export interface Album {
  id?: number;
  title: string;
  description?: string;
  coverImageUrl?: string;
  releaseDate?: Date;
  genre?: string;
  artists?: Artist[];
  tracks?: Track[];
} 