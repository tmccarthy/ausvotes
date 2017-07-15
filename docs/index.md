---
layout: page
title: SenateDB
---

SenateDB is a tool for performing analysis of formal ballots cast for the Senate during the 2016 Australian federal 
election. The source code and instructions for running it are available 
[on GitHub](https://github.com/tmccarthy/SenateDB/tree/v0.4.1).

* auto-gen TOC:
{:toc}

## General notes

### The raw data

These reports are composed by the [SenateDB tool](https://github.com/tmccarthy/SenateDB/tree/v0.4.1) from raw data made 
available on [the AEC's website](http://results.aec.gov.au/20499/Website/SenateDownloadsMenu-20499-Csv.htm). In 
particular, they rely on the large csv files on that website containing the preferences on every formal ballot paper at 
the 2016 federal election.

### Formal ballots

The tables in these reports are constructed based on the *formal* ballot papers included in the AEC data. The 
[567,806 *informal* ballots](http://results.aec.gov.au/20499/Website/SenateInformalByState-20499.htm) cast are not 
available in the AEC data, and so are not included in this analysis.

### Formality and savings provisions

For each ballot, the AEC data includes the number (or tick or cross) written in each square on the ballot paper. We need
to apply the rules in the Electoral Act in order to retrieve the actual candidate order used in the count.

This process is performed by the [`BallotNormaliser` class](https://github.com/tmccarthy/SenateDB/blob/v0.4.1/src/main/scala/au/id/tmm/senatedb/computations/ballotnormalisation/BallotNormaliser.scala).
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

## Findings for the 2016 Election

Check out the full reports for more data and notes on methodology.

### Donkey votes

[*Full report*](reports/donkeyvotes)

Nationally, donkey votes were a tiny fraction of the total.

| |Donkey votes|%|
|---|---|---|
|**Total**|**20,553**|**0.15%**|

The only candidate elected to the Senate from group A was Derryn Hinch in Victoria. Only 7.74% of his primary vote came 
from donkey votes.

### How-to-vote card usage

[*Full report*](reports/htvusage)

There were large variations in how-to-vote card usage by party. In particular, a high proportion of Liberal/Coalition
voters used a how-to-vote card.

|Party|Ballots matching an HTV card|Total formal ballots for party|%|
|---|---|---|---|
|Liberal Party of Australia|1,322,391|4,821,314|27.43%|
|Australian Labor Party|522,915|4,123,084|12.68%|
|The Greens|112,670|1,197,657|9.41%|
|Pauline Hanson's One Nation|17,617|593,013|2.97%|

| |Ballots matching an HTV card|%|
|---|---|---|
|**Total**|**2,010,114**|**14.53%**|

### Ballots that marked only '1' ATL

[*Full report*](reports/oneatl)

2% of the population still voted '1' above the line, despite new rules in 2016 requiring them to mark at least 6 
squares. 

| |Ballots with only '1' above the line|%|
|---|---|---|
|**Total**|**290,758**|**2.10%**|

### Ballots saved from being informal by savings provisions

[*Full report*](reports/savedballots)

More than a million ballots were saved from being informal by savings provisions in the Electoral Act.

| |Saved ballots|%|
|---|---|---|
|**Total**|**1,087,711**|**7.86%**|

### Votes above the line

[*Full report*](reports/atlvotes)

Despite changes to the Electoral Act making it easier to vote below the line, the vast majority of Australians continued
to vote above the line, preferencing parties rather than particular candidates.

| |Votes above the line|%|
|---|---|---|
|**Total**|**12,940,784**|**93.51%**|

Bucking the national trend, Tasmanians were much more likely to vote below the line. No doubt this is related to the 
extraordinary election of Lisa Singh, who was elected on below-the-line votes despite being the last candidate on the 
ALP ticket.

|State|Votes above the line|Total formal ballots for party|%|
|---|---|---|---|
|TAS|243,942|339,159|71.93%|
|ACT|216,278|254,767|84.89%|
|NT|93,307|102,027|91.45%|
|SA|971,322|1,061,165|91.53%|
|QLD|2,556,482|2,723,166|93.88%|
|WA|1,291,224|1,366,182|94.51%|
|NSW|4,252,904|4,492,197|94.67%|
|VIC|3,315,325|3,500,237|94.72%|
|**Total**|**12,940,784**|**13,838,900**|**93.51%**|


### Votes above and below the line

[*Full report*](reports/atlandbtl)

A very small number of Australians voted both above and below the line.

| |Ballots formal both above and below the line|%|
|---|---|---|
|**Total**|**163,141**|**1.18%**|

### Exhausted votes

[*Full report*](reports/exhaustedvotes)

There were more than 1 million exhausted votes in the 2016 Senate election. Votes whose first preference was for a minor
party contributed the vast majority of these.

|Party type|Exhausted votes|Total formal ballots|%|
|---|---|---|---|
|Minor parties|906,319|3,626,476|24.99%|
|Major parties|124,754|10,188,987|1.22%|
|Independents|11,060|23,437|47.19%|
|**Total**|**1,042,132**|**13,838,900**|**7.53%**|

### Exhausted ballots

[*Full report*](reports/exhaustedballots)

Nearly 50% of ballot papers eventually exhausted. Many of these exhausted after electing a candidate, and so contributed
very little to the above tally of exhausted votes. Take a look at the [exhausted votes report](reports/exhaustedvotes) 
for an explanation of this distinction.

| |Exhausted ballots|Total formal ballots|%|
|---|---|---|---|
|**Total**|**6,865,733**|**13,838,900**|**49.61%**|

## Feedback

If you have any feedback or suggestions, don't hesitate to contact [@tmccarthy_](https://twitter.com/tmccarthy_) on 
Twitter, or [raise a ticket on GitHub](https://github.com/tmccarthy/SenateDB/issues). Pull requests are welcome.
