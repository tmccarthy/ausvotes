import { expect } from 'chai';
import 'mocha';
import { nameOf, State, stateFor } from "./states";

describe('nameOf', () => {
  it('should return the name of South Australia', () => {
    expect(nameOf(State.SA)).to.equal('South Australia');
  });
});

describe('stateFor', () => {
  it('should return the state for "SA"', () => {
    expect(stateFor("SA")).to.equal(State.SA)
  });

  it('should return the state for "sa"', () => {
    expect(stateFor("sa")).to.equal(State.SA)
  });

  it('should return null for an invalid abbreviation', () => {
    expect(stateFor("invalid")).to.equal(null)
  })
});
