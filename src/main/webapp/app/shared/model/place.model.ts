import { Moment } from 'moment';

export interface IPlace {
  id?: string;
  name?: string;
  summary?: string;
  primaryUrl?: string;
  imageUrl?: string;
  rating?: number;
  lotype?: string;
  country?: string;
  coordinates?: Array<number>;
  notes?: string;
  updatedBy?: string;
  createdBy?: string;
  createdAt?: Moment;
  updatedAt?: Moment;
}

export class Place implements IPlace {
  constructor(
    public id?: string,
    public name?: string,
    public summary?: string,
    public imageUrl?: string,
    public rating?: number,
    public lotype?: string,
    public country?: string,
    public coordinates?: Array<number>,
    public notes?: string,
    public createdBy?: string,
    public createdAt?: Moment,
    public updatedBy?: string,
    public updatedAt?: Moment,
    public primaryUrl?: string
  ) {}
}
