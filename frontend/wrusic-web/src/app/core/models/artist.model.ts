export interface Artist {
  id: string;
  name: string;
  images: { url: string }[];
  listeners?: number;
} 