import { Artist } from './artist.model';
import { Album } from './album.model';

export interface Track {
  id: string;
  name: string;
  artists: { id: string; name: string }[];
  album: {
    id: string;
    name: string;
    images: { url: string }[];
  };
  preview_url?: string;
} 