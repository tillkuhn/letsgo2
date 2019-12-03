import { Moment } from 'moment';

export interface IPlace {
  id?: number;
  name?: string;
  summary?: string;
  imageUrl?: string;
  rating?: number;
  lotype?: string;
  country?: string;
  updatedBy?: string;
  coordinates?: string;
  notes?: string;
  updatedAt?: Moment;
  primaryUrl?: string;
}

export class Place implements IPlace {
  constructor(
    public id?: number,
    public name?: string,
    public summary?: string,
    public imageUrl?: string,
    public rating?: number,
    public lotype?: string,
    public country?: string,
    public updatedBy?: string,
    public coordinates?: string,
    public notes?: string,
    public updatedAt?: Moment,
    public primaryUrl?: string
  ) {}
}
