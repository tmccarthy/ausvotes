CREATE TABLE address (
  id SERIAL PRIMARY KEY,

  lines TEXT[],

  suburb TEXT,
  postcode CHAR(4),
  state VARCHAR(3) REFERENCES state(abbreviation)
);

CREATE TYPE VOTE_COLLECTION_POINT_TYPE AS ENUM ('polling_place', 'absentee', 'postal', 'prepoll', 'provisional');
CREATE TYPE POLLING_PLACE_TYPE AS ENUM ('polling_place', 'special_hospital_team', 'remote_mobile_team', 'other_mobile_team', 'pre_poll_voting_centre');

CREATE TABLE vote_collection_point (
  id SERIAL PRIMARY KEY,

  election VARCHAR(5) REFERENCES senate_election(id),
  aec_id INTEGER,
  state VARCHAR(3) REFERENCES state(abbreviation),
  division_id INTEGER REFERENCES division(id),

  type VOTE_COLLECTION_POINT_TYPE,
  name VARCHAR,

  -- Only if this is a polling place

  polling_place_type POLLING_PLACE_TYPE,

  multiple_locations BOOLEAN,

  premises_name VARCHAR,
  address INTEGER REFERENCES address(id),

  latitude DOUBLE PRECISION,
  longtitude DOUBLE PRECISION
);

CREATE TABLE vote_collection_point_stats (
  vote_collection_point_id INTEGER REFERENCES vote_collection_point(id),

  total_formal_ballot_count_id INTEGER REFERENCES total_formal_ballot_count(id)
);
