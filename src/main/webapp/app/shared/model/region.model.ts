export interface IRegion {
  id?: number;
  code?: string;
  name?: string;
}

export class Region implements IRegion {
  constructor(public id?: number, public code?: string, public name?: string) {}
}
