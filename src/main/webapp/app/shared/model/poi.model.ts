export interface IPoi {
  id?: string;
  name?: string;
  lotype?: string;
  country?: string;
  coordinates?: Array<number>;
}

export class Poi implements IPoi {
  constructor(
    public id?: string,
    public name?: string,
    public lotype?: string,
    public country?: string,
    public coordinates?: Array<number>
  ) {}
}
