ALTER TABLE senate_election
  ADD COLUMN name TEXT;

ALTER TABLE senate_election
  ALTER COLUMN id TYPE TEXT;

INSERT INTO senate_election(id, aec_id, date, name) VALUES
  ('2016', '20499', '2016-07-02', '2016 election'),
  ('2014WA', '17875', '2014-04-05', '2014 WA Senate election'),
  ('2013', '17496', '2013-09-07', '2013 election');

CREATE UNIQUE INDEX uk_division_name ON division(LOWER(name));
