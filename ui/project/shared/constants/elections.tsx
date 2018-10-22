import { allStates, State } from "./states";

export class Election {

  id: string;
  name: string;
  states: Array<State>;

  constructor(id: string, name: string, states: Array<State>) {
    this.id = id;
    this.name = name;
    this.states = states;
  }
}

export const election2016: Election = new Election("2016", "2016 Election", allStates);

export function lookupElectionById(id: String): null | Election {
  if (id === election2016.id) {
    return election2016;
  } else {
    return null;
  }
}
