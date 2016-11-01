---
layout: default
title: Reports
---

# SenateDB Reports

* [Ballots that marked only '1' above the line](oneatl)
* [Donkey votes](donkeyvotes)
* [Ballots that match how-to-vote cards](htvusage)
* [Ballots saved by savings provisions](savedballots)

## General notes on the reports

### The raw data

These reports are composed by the [SenateDB tool](https://github.com/tmccarthy/SenateDB) from raw data made available on
[the AEC's website](http://results.aec.gov.au/20499/Website/SenateDownloadsMenu-20499-Csv.htm). In particular, they rely
on the large csv files on that website containing the preferences on every formal ballot paper at the 2016 federal 
election.

### Formal ballots

The tables in these reports are constructed based on the *formal* ballot papers included in the AEC data. The 
[567,806 *informal* ballots](http://results.aec.gov.au/20499/Website/SenateInformalByState-20499.htm) cast are not 
available in the AEC data, and so are not included in this analysis.

### Formality and savings provisions

For each ballot, the AEC data includes the number (or tick or cross) written in each square on the ballot paper. We need
to apply the rules in the Electoral Act in order to retrieve the actual candidate order used in the count.

This process is performed by the [`BallotNormaliser` class](https://github.com/tmccarthy/SenateDB/blob/master/src/main/scala/au/id/tmm/senatedb/computations/ballotnormalisation/BallotNormaliser.scala).
Broadly, it applies 5 processes:

1. Converts ticks or crosses to a '1' in the marked square, as per sections 
   [269(1A)(a)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) and 
   [268A(2)(a)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s268a.html) of the Act.
2. Truncates preferences expressed above the line at any counting errors after the first preference, as per section 
   [269(1A)(b)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) of the Act.
3. Truncates preferences expressed below the line at any counting errors after the sixth preference, as per section
   [268A(2)(b)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s268a.html) of
   the Act.
4. Distributes preferences expressed above the line to the candidates below the line in the preferenced groups, as per 
   section [272](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s272.html) of the Act
5. If, after the above steps, the ballot is formal both above and below the line, we use the preferences expressed below
   the line, as per section [269(2)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) of the Act. 
   Otherwise we use the preferences expressed above the line.

### Tabulating by first-preference

Many of the reports tabulate by ballots' first-preferences. Doing this at a state level is relatively simple: we simply
tabulate by the group that received the first preference (either above or below the line).

Aggregating first-preferences nationally requires doing so by party rather than by group. This requires some judgement 
calls to ensure the results both accurately reflect the ballots, and are not overshadowed by corner-cases.

SenateDB uses the following process:

1. Above-the-line ballots use the party associated with the group that received the first preference. If the group is
   not associated with a party (eg group B in Victoria at the 2016 election), the first preference is marked as 
   "Independent".
2. Below-the-line ballots use the party associated with the candidate that received the first preference. Note that this
   is not always the same as the party of the associated group. For example, group E in Victoria at the 2016 election 
   was associated with the party "SCIENCE PARTY / CYCLISTS PARTY", but Luke James (the first candidate in the group) was
   associated with the "Science Party". Again, if the candidate is not associated with a party, the first preference is
   marked as "Independent".
3. State-specific versions of national parties are rolled into their national equivalents (eg 
   "Australian Labor Party (Northern Territory) Branch") ballots are rolled into the "Australian Labor Party" count.

#### The Coalition

The above process leaves a lot of ambiguity regarding how votes for the coalition in different states should be 
aggregated. In [their aggregate tables](http://results.aec.gov.au/20499/Website/SenateStateFirstPrefsByGroup-20499-NAT.htm), 
the AEC seems to have decided to keep the counts for the different constituent parties separated. This principle is even
applied to the Country Liberals(NT), who are listed separately. On the other hand, the WA Greens are added to the 
national Greens count.

When aggregating nationally, SenateDB combines all votes for Coalition parties into the "Liberal Party of Australia" 
count, *except* where those votes were specifically cast for The Nationals. For example, a vote whose first preference 
was above the line in group AF (Liberal / The Nationals) in Victoria at the 2016 federal election would be included in 
the "Liberal Party of Australia" count. A vote whose first preference was below the line for Bridget McKenzie (in the 
same group) would count toward "The Nationals", as that is the party listed next to her name on the ballot paper.
