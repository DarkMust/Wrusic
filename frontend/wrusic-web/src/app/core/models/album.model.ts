import { Artist } from './artist.model';
import { Track } from './track.model';

export interface Album {
  id: string;
  name: string;
  artists: { id: string; name: string }[];
  images: { url: string }[];
  release_date?: string;
} 