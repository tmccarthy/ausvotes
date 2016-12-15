CREATE TABLE senate_election (
  id VARCHAR(5) PRIMARY KEY,
  aec_id CHAR(5),
  date DATE
);

CREATE TABLE state (
  abbreviation VARCHAR(3) PRIMARY KEY,
  name VARCHAR(28),
  territory BOOLEAN DEFAULT FALSE
);

INSERT INTO state(abbreviation, name) VALUES
  ('NSW', 'New South Wales'),
  ('QLD', 'Queensland'),
  ('SA', 'South Australia'),
  ('TAS', 'Tasmania'),
  ('VIC', 'Victoria'),
  ('WA', 'Western Australia');

INSERT INTO state(abbreviation, name, territory) VALUES
  ('NT', 'Northern Territory', TRUE),
  ('ACT', 'Australian Capital Territory', TRUE);

CREATE TABLE division (
  id SERIAL PRIMARY KEY,
  election VARCHAR(5) REFERENCES senate_election(id),
  aec_id INTEGER,
  state VARCHAR(3) REFERENCES state(abbreviation) ,
  name VARCHAR
);

CREATE TABLE total_formal_ballot_count (
  id SERIAL PRIMARY KEY,

  total_formal_ballots INTEGER,
  ordinal_nationally INTEGER,

  -- TODO unneeded?
  ordinal_state INTEGER,
  ordinal_division INTEGER
);

CREATE TABLE division_stats (
  division_id INTEGER REFERENCES division(id),

  total_formal_ballot_count_id INTEGER REFERENCES total_formal_ballot_count(id)
);
