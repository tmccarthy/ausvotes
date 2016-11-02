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

SenateDB is intended to grow into a set of tools to analyse this data. Its original incarnation (version 0.1) loaded the
data into a relational database. You can view that tool [here](https://github.com/tmccarthy/SenateDB/tree/v0.1).

## Running

SenateDB is built and run with [SBT](http://www.scala-sbt.org/). Simply check it out and run `sbt run` to generate the
reports.

SenateDB will download the raw data from the AEC the first time it is run (about 180 MB), and then reads through these
files to generate the reports. The report generation takes about 13 minutes on my Macbook Pro.

## Licence

The `src/test/resources/au/id/tmm/senatedb/fixtures` directory contains some (truncated) copies of raw AEC data. These 
files are © Commonwealth of Australia 2014, and are [licenced](http://www.aec.gov.au/footer/Copyright.htm) under the 
[Creative Commons Attribution 3.0 Australia Licence](http://creativecommons.org/licenses/by/3.0/au/).

The accompanying website for this project is contained in the `docs/` directory. This is built on the 
[Lanyon theme](https://github.com/poole/lanyon) for Jekyll. It is licenced under the terms of the 
[docs/LICENCE](docs/LICENCE) file (MIT).

The rest of the application is licenced under the terms in the [LICENCE](LICENCE) file (GNU GPL version 3).