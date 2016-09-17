[![Build Status](https://travis-ci.org/tmccarthy/SenateDB.svg?branch=master)](https://travis-ci.org/tmccarthy/SenateDB)
[![Coverage Status](https://coveralls.io/repos/github/tmccarthy/SenateDB/badge.svg?branch=master)](https://coveralls.io/github/tmccarthy/SenateDB?branch=master)

# SenateDB

A tool for collating and analysing the raw data about Australian Senate elections made available on the 
Australian Electoral Commission's website. This is essentially a slightly more ambitious rewrite of my 
other project at [HypotheticalSenate](https://github.com/tmccarthy/HypotheticalSenate). The initial focus
will be analysing the 2016 election.

## Overview

The AEC makes a huge amount of raw data about elections available on 
[their website](http://results.aec.gov.au/20499/Website/SenateDownloadsMenu-20499-Csv.htm). This includes the raw 
preferences expressed on every single formal ballot paper in the Senate election. The data is made available in CSV 
format, which does not lend itself to easy analysis.

SenateDB is a command line utility that loads data about the 2016 Senate election into a relational database. This 
allows for easier analysis than the raw csv format. When transferring the data into the database, SenateDB also performs
some small calculations, particularly with regard to ballot exhaustion.

## Running SenateDB

SenateDB is written in Scala, and currently the easiest way to run it is using SBT. There is support for loading data 
into either SQLite or Postgres. The SQLite support is really only there for testing. If you're going to load in anything
bigger than the really small states/territories, you should use Postgres.

In the project directory, run `sbt` to get interactive mode. You can then use the `run` command to run as you like.

Loading NSW and VIC into a postgres database looks like this:

```run --postgres-host localhost --postgres-user senatedbuser --postgres-password <password> load nsw vic```

To get the full options, run:

```run --help```

### Performance

There is a *lot* of data to load in. On my Macbook Pro, a full load of all states into a Postgres database takes about 4
hours, and the final database is just over 20GB.

I recommend running `sbt` with a max heap size of 4GB. If you run it with less than 2GB, you may run into 
`OutOfMemoryError`s. My recommendation is to start the SBT vm with `-Xmx4096m`.

## Licence

With the exception of some AEC data included as test resources, this project is licenced under the terms outlined in 
the LICENCE file.

The raw AEC data included in the test resources is © Commonwealth of Australia 2014, and is 
[licenced](http://www.aec.gov.au/footer/Copyright.htm) under the 
[Creative Commons Attribution 3.0 Australia Licence](http://creativecommons.org/licenses/by/3.0/au/).