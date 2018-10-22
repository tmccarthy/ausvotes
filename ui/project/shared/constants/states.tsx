export enum State {
  SA = "SA",
  NSW = "NSW",
  VIC = "VIC",
  QLD = "QLD",
  WA = "WA",
  TAS = "TAS",
  ACT = "ACT",
  NT = "NT",
}

export function nameOf(state: State): String {
  switch (state) {
    case State.SA: return "South Australia";
    case State.NSW: return "New South Wales";
    case State.VIC: return "Victoria";
    case State.QLD: return "Queensland";
    case State.WA: return "Western Australia";
    case State.TAS: return "Tasmania";
    case State.ACT: return "Australian Capital Territory";
    case State.NT: return "Northern Territory";
  }
}

export const allStates: Array<State> = Object.values(State);

export function stateFor(abbreviation: string): null | State {
  return State[abbreviation.toUpperCase()] || null;
}
