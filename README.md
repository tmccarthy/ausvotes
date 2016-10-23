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

There is not currently a command line interface for the application. Instead, there are a coupled of objects with `main`
methods in the `au.id.tmm.senatedb.entrypoints` package.

## Licence

With the exception of some AEC data included as test resources, this project is licenced under the terms outlined in 
the LICENCE file.

The raw AEC data included in the test resources is © Commonwealth of Australia 2014, and is 
[licenced](http://www.aec.gov.au/footer/Copyright.htm) under the 
[Creative Commons Attribution 3.0 Australia Licence](http://creativecommons.org/licenses/by/3.0/au/).