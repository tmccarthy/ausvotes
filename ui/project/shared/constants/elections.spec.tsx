import { expect } from 'chai';
import 'mocha';
import { allStates } from "./states";
import { election2016, lookupElectionById } from "./elections";

describe('2016 election', () => {
  it('should have an id of 2016', () => {
    expect(election2016.id).to.equal('2016')
  });

  it('should have a name of "2016 Election"', () => {
    expect(election2016.name).to.equal('2016 Election')
  });

  it('should be for all states', () => {
    expect(election2016.states).to.equal(allStates)
  })
});

describe('lookupElectionById', () => {
  it('should return the 2016 election', () => {
    expect(lookupElectionById('2016')).to.equal(election2016)
  });

  it('should return null for an invalid id', () => {
    expect(lookupElectionById('invalid')).to.equal(null)
  });
});
