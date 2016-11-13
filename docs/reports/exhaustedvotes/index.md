# Exhausted votes

These tables show the number of exhausted votes. A ballot's exhaustion is calculated with the 
[`ExhaustionCalculator` class](https://github.com/tmccarthy/SenateDB/blob/master/src/main/scala/au/id/tmm/senatedb/computations/exhaustion/ExhaustionCalculator.scala).
This is done by tracing the path a ballot would have taken through the count proces as described in data made 
available [on the AEC website](http://results.aec.gov.au/20499/Website/SenateDownloadsMenu-20499-Csv.htm#mainbody_titleDop).

## Explanation

Votes for the Senate are counted according to the [single transferable vote](https://en.wikipedia.org/wiki/Single_transferable_vote)
system. Under this system, ballots are allocated to their first preference, and candidates are then either elected or 
excluded until all positions have been filled. At each count, the ballots of a candidate that has been elected or 
excluded are distributed to their next preference. This process is described in more detail on 
[the AEC website](http://www.aec.gov.au/voting/counting/senate_count.htm).

During the count, a situation can arise where a ballot must be distributed to its next preference, but it either does
not express further preferences or all its remaining preferences have either been elected or excluded. Such a ballot is
said to *exhaust*. Because it expresses no further preference, it is set aside and takes no further part in the count.

Prior to the 2016 Federal Election, exhausted ballots were exceedingly rare in Australian Senate elections. Votes above
the line were preferenced according to a [group voting ticket](https://en.wikipedia.org/wiki/Group_voting_ticket). These
expressed preferences for all candidates, and so could never exhaust. Votes below the line were required to enumerate 
preferences for every candidate, and so these would also never exhaust. A ballot could only exhaust if it was numbered 
below the line, and contained a counting error. In this case, savings provisions in the Electoral Act would mean the 
vote was still formal, but its preferences would stop flowing at the counting error.

At the 2016 Federal Election, group voting tickets were abolished, and voters were required to express their own 
preferences. They were told to number at least 6 squares above the line, or at least 12 below the line (actual formality
requirements were less strict, see [the report on saved ballots](../savedballots)). Since the vast majority of ballots
did not express a preference for every candidate, exhaustion became a much more common occurrence.

#### Exhausted ballots vs exhausted votes

Under the single transferable vote system, a ballot that preferences a candidate that is elected continues to 
participate in the count (according to its preferences) at a reduced, "transfer" value. The transfer value is explained
in more detail by the AEC [here](http://www.aec.gov.au/voting/counting/senate_count.htm#surplus).

The transfer value is important when considering exhaustion. When a candidate is elected with only a handful of surplus 
votes, the ballots distributed from that candidate are passed on with a very small transfer value. If these ballots go 
on to exhaust, their total value (and hence their potential impact) is small. On the other hand, when a candidate is 
excluded, their ballots continue at full value. If these exhaust, their total value is large, and their greater 
potential impact is lost.

SenateDB describes this difference by differentiating between the terms "exhausted ballots" and "exhausted votes":

* A tally of **exhausted ballots** describes the total number of ballot papers that had exhausted by the final count, 
  without reference to their transfer value when they exhausted.
* A tally of **exhausted votes** describes the total transfer value of ballot papers that were exhausted at the final 
  count.

Because a tally of **exhausted votes** counts exhausted ballot papers according to their transfer value, it can 
reasonably be used to make inferences about the impact of exhausted votes. A tally of **exhausted ballots** is not 
appropriate for this purpose.

The tables on this page are of **exhausted votes**.

#### Comparison to AEC data

It is worth pointing out that there are small differences in the total number of exhausted votes listed here, and those
recorded in the distribution of preferences data recorded by the AEC. This is because the AEC rounds the total 
number of exhausted votes to the nearest integer at the end of every count. This rounding cannot be meaningfully 
replicated when calculating the exhaustion value *of a single ballot*, and so is not done for these aggregate counts.

## Tables

### National total

| |Exhausted votes|Total formal ballots|%|
|---|---|---|---|
|**Total**|**1,042,132**|**13,838,900**|**7.53%**|

### Nationally by first-preferenced party

|Party|Exhausted votes|Total formal ballots for party|%|
|---|---|---|---|
|Shooters, Fishers and Farmers|70,450|192,923|36.52%|
|Family First Party|68,532|191,112|35.86%|
|Nick Xenophon Team|66,005|456,369|14.46%|
|Animal Justice Party|65,148|159,373|40.88%|
|Australian Labor Party|50,857|4,123,084|1.23%|
|Pauline Hanson's One Nation|48,868|593,013|8.24%|
|Australian Sex Party|44,909|97,882|45.88%|
|Liberal Party of Australia|44,425|4,821,314|0.92%|
|Democratic Labour Party|43,078|94,510|45.58%|
|Christian Democratic Party (Fred Nile Group)|39,505|162,155|24.36%|
|Health Australia Party|38,864|85,233|45.60%|
|Liberal Democratic Party|37,540|298,915|12.56%|
|Drug Law Reform|31,206|61,327|50.88%|
|Australian Christians|31,007|66,525|46.61%|
|Australian Liberty Alliance|25,142|102,982|24.41%|
|Australian Motoring Enthusiast Party|21,903|53,232|41.15%|
|Marijuana (HEMP) Party|21,146|33,387|63.33%|
|Australian Sex Party/Marijuana (HEMP) Party|20,567|69,247|29.70%|
|Pirate Party Australia|17,810|35,184|50.62%|
|The Nationals|17,372|46,932|37.01%|
|Derryn Hinch's Justice Party|17,214|266,607|6.46%|
|Marriage Equality|16,524|44,982|36.73%|
|The Arts Party|14,167|37,702|37.58%|
|Jacqui Lambie Network|13,524|69,074|19.58%|
|Science Party / Cyclists Party|12,994|24,673|52.66%|
|The Greens|12,099|1,197,657|1.01%|
|Independent|11,060|23,437|47.19%|
|Rise Up Australia Party|10,980|36,424|30.14%|
|Voluntary Euthanasia Party|10,674|23,252|45.90%|
|Renewable Energy Party|10,429|29,983|34.78%|
|Katter's Australian Party|10,141|53,199|19.06%|
|Glenn Lazarus Team|9,597|45,149|21.26%|
|Sustainable Australia|8,881|26,341|33.72%|
|Seniors United Party of Australia|8,487|22,213|38.21%|
|Palmer United Party|7,323|26,210|27.94%|
|Australian Cyclists Party|7,239|25,438|28.46%|
|Mature Australia|6,975|19,354|36.04%|
|VOTEFLUX.ORG &#124; Upgrade Democracy!|6,010|21,151|28.42%|
|Online Direct Democracy - (Empowering the People!)|5,702|12,018|47.44%|
|Socialist Equality Party|4,678|7,865|59.48%|
|Secular Party of Australia|4,525|11,077|40.85%|
|Veterans Party|4,456|10,391|42.88%|
|Citizens Electoral Council of Australia|4,188|9,850|42.52%|
|Socialist Alliance|3,987|9,968|39.99%|
|Australian Country Party|3,595|9,316|38.59%|
|Science Party|2,650|5,405|49.03%|
|CountryMinded|2,403|5,989|40.12%|
|Australian Progressives|2,352|6,251|37.62%|
|MFP|2,338|5,268|44.39%|
|Non-Custodial Parents Party (Equal Parenting)|1,213|2,102|57.71%|
|Australia First Party|884|3,005|29.42%|
|Australian Recreational Fishers Party|333|2,376|14.04%|
|Australian Antipaedophile Party|178|474|37.48%|
|**Total**|**1,042,132**|**13,838,900**|**7.53%**|

### Nationally by first-preferenced party type

For the purposes of this table, a ballot's first preference was for a major party if:

* Its first preference was for the Australian Labor Party, or
* Its first preference was for a party within The Coalition, or
* Its first preference was for The Greens.

|Party type|Exhausted votes|Total formal ballots|%|
|---|---|---|---|
|Minor parties|906,319|3,626,476|24.99%|
|Major parties|124,754|10,188,987|1.22%|
|Independents|11,060|23,437|47.19%|
|**Total**|**1,042,132**|**13,838,900**|**7.53%**|

### By state

|State|Exhausted votes|Total formal ballots for party|%|
|---|---|---|---|
|NSW|414,826|4,492,197|9.23%|
|VIC|300,431|3,500,237|8.58%|
|QLD|209,239|2,723,166|7.68%|
|WA|86,382|1,366,182|6.32%|
|SA|21,578|1,061,165|2.03%|
|TAS|9,567|339,159|2.82%|
|ACT|109|254,767|0.04%|
|NT|0|102,027|0.00%|
|**Total**|**1,042,132**|**13,838,900**|**7.53%**|

### By group in the Australian Capital Territory

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|D (Rise Up Australia Party)|51|2,523|2.02%|
|E (Sustainable Australia)|41|2,678|1.53%|
|B (Secular Party of Australia)|14|1,378|1.02%|
|F (Liberal Party of Australia)|1|84,615|0.00%|
|A (Liberal Democratic Party)|1|7,460|0.01%|
|G (Animal Justice Party)|1|4,251|0.02%|
|J (Australian Sex Party)|0|10,096|0.00%|
|UG (Ungrouped)|0|1,006|0.00%|
|H (The Greens)|0|41,006|0.00%|
|C (Australian Labor Party)|0|96,667|0.00%|
|I (Christian Democratic Party (Fred Nile Group))|0|3,087|0.00%|
|**Total**|**109**|**254,767**|**0.04%**|

### By group in New South Wales

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|AI (Nick Xenophon Team)|36,150|80,111|45.12%|
|J (Shooters, Fishers and Farmers)|31,569|88,837|35.54%|
|H (Democratic Labour Party)|27,338|51,510|53.07%|
|AF (Christian Democratic Party (Fred Nile Group))|26,062|121,379|21.47%|
|F (Liberal & Nationals)|25,695|1,610,626|1.60%|
|A (Health Australia Party)|24,957|53,154|46.95%|
|AB (Animal Justice Party)|24,326|37,991|64.03%|
|N (Australian Labor Party)|23,873|1,405,088|1.70%|
|C (Family First Party)|23,859|53,027|44.99%|
|AG (Australian Sex Party)|20,340|30,038|67.71%|
|AO (Marijuana (HEMP) Party)|19,922|29,510|67.51%|
|AJ (Drug Law Reform)|14,463|20,883|69.26%|
|I (Science Party / Cyclists Party)|12,082|18,367|65.78%|
|AA (Australian Motoring Enthusiast Party)|8,764|16,356|53.58%|
|B (Seniors United Party of Australia)|8,487|22,213|38.21%|
|K (Voluntary Euthanasia Party)|8,324|15,198|54.77%|
|P (Derryn Hinch's Justice Party)|7,675|26,720|28.72%|
|R (Pirate Party Australia)|6,863|11,418|60.11%|
|AC (The Arts Party)|5,893|11,805|49.92%|
|Q (Jacqui Lambie Network)|5,483|16,502|33.23%|
|AL (The Greens)|5,243|332,860|1.58%|
|AM (Australian Liberty Alliance)|4,815|29,795|16.16%|
|AN (Renewable Energy Party)|4,156|8,936|46.51%|
|E (VOTEFLUX.ORG &#124; Upgrade Democracy!)|3,593|12,578|28.57%|
|O (Online Direct Democracy - (Empowering the People!))|3,172|6,353|49.93%|
|T (Veterans Party)|3,130|5,857|53.43%|
|AK (Sustainable Australia)|3,122|7,723|40.43%|
|L (Socialist Alliance)|2,791|5,382|51.85%|
|M (Rise Up Australia Party)|2,790|7,538|37.02%|
|S (Pauline Hanson's One Nation)|2,427|184,012|1.32%|
|G (Independent)|2,255|3,871|58.26%|
|W (Socialist Equality Party)|1,996|2,933|68.07%|
|UG (Ungrouped)|1,833|2,953|62.06%|
|U (Secular Party of Australia)|1,762|2,773|63.55%|
|V (CountryMinded)|1,643|3,153|52.11%|
|AE (Mature Australia)|1,525|2,805|54.37%|
|X (Katter's Australian Party)|1,502|4,316|34.80%|
|Y (Palmer United Party)|1,420|2,805|50.64%|
|AD (Non-Custodial Parents Party (Equal Parenting))|1,213|2,102|57.71%|
|Z (Citizens Electoral Council of Australia)|1,095|1,895|57.79%|
|AH (Australian Progressives)|1,079|1,817|59.41%|
|D (Liberal Democratic Party)|138|139,007|0.10%|
|**Total**|**414,826**|**4,492,197**|**9.23%**|

### By group in the Northern Territory

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|A (Rise Up Australia Party)|0|6,768|0.00%|
|E (Country Liberals (NT))|0|37,156|0.00%|
|G (Christian Democratic Party (Fred Nile Group))|0|1,660|0.00%|
|C (Citizens Electoral Council of Australia)|0|1,255|0.00%|
|B (Australian Sex Party/Marijuana (HEMP) Party)|0|4,956|0.00%|
|D (The Greens)|0|11,003|0.00%|
|F (Australian Labor Party (Northern Territory) Branch)|0|38,197|0.00%|
|UG (Ungrouped)|0|1,032|0.00%|
|**Total**|**0**|**102,027**|**0.00%**|

### By group in Queensland

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|T (Family First Party)|22,279|52,453|42.47%|
|H (Animal Justice Party)|15,949|32,306|49.37%|
|E (Liberal Democratic Party)|15,834|77,601|20.40%|
|V (Australian Sex Party/Marijuana (HEMP) Party)|13,838|30,157|45.89%|
|L (Nick Xenophon Team)|12,252|55,653|22.02%|
|D (Australian Labor Party)|11,464|717,524|1.60%|
|J (Marriage Equality)|10,412|23,811|43.73%|
|G (Liberal National Party of Queensland)|10,160|960,467|1.06%|
|AC (Glenn Lazarus Team)|9,597|45,149|21.26%|
|Q (Shooters, Fishers and Farmers)|9,167|29,571|31.00%|
|I (Katter's Australian Party)|8,617|48,807|17.66%|
|AG (Drug Law Reform)|6,358|17,060|37.27%|
|A (Australian Cyclists Party)|5,746|19,933|28.83%|
|S (Democratic Labour Party)|4,701|15,443|30.44%|
|B (The Arts Party)|4,516|11,030|40.94%|
|M (Pirate Party Australia)|4,438|10,342|42.91%|
|N (Australian Liberty Alliance)|4,386|29,392|14.92%|
|AF (Australian Christians)|3,939|9,686|40.67%|
|AH (Health Australia Party)|3,642|10,147|35.89%|
|AA (Christian Democratic Party (Fred Nile Group))|3,169|7,314|43.32%|
|O (Derryn Hinch's Justice Party)|2,975|14,256|20.87%|
|F (Online Direct Democracy - (Empowering the People!))|2,529|5,504|45.96%|
|AK (The Greens)|2,449|188,323|1.30%|
|K (Mature Australia)|2,166|5,519|39.25%|
|U (Renewable Energy Party)|2,117|6,245|33.89%|
|UG (Ungrouped)|2,104|4,154|50.65%|
|Y (Rise Up Australia Party)|1,923|5,734|33.54%|
|AD (Jacqui Lambie Network)|1,732|9,138|18.96%|
|C (Secular Party of Australia)|1,645|4,623|35.58%|
|AL (Sustainable Australia)|1,635|5,366|30.48%|
|AJ (Veterans Party)|1,326|4,534|29.25%|
|AB (Palmer United Party)|1,264|4,816|26.24%|
|R (Independent)|864|1,536|56.25%|
|P (Citizens Electoral Council of Australia)|847|1,877|45.14%|
|AI (CountryMinded)|760|2,836|26.79%|
|Z (Socialist Equality Party)|720|1,639|43.90%|
|W (VOTEFLUX.ORG &#124; Upgrade Democracy!)|664|1,881|35.31%|
|X (Pauline Hanson's One Nation)|589|250,126|0.24%|
|AE (Australian Progressives)|466|1,213|38.42%|
|**Total**|**209,239**|**2,723,166**|**7.68%**|

### By group in South Australia

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|O (Pauline Hanson's One Nation)|4,916|31,621|15.55%|
|H (Liberal Party of Australia)|2,998|345,767|0.87%|
|R (Australian Sex Party/Marijuana (HEMP) Party)|2,208|12,091|18.26%|
|F (Nick Xenophon Team)|2,100|230,703|0.91%|
|Q (Shooters, Fishers and Farmers)|1,544|7,815|19.75%|
|K (Liberal Democratic Party)|1,142|6,913|16.52%|
|J (Australian Motoring Enthusiast Party)|1,021|5,091|20.06%|
|U (Animal Justice Party)|889|8,981|9.90%|
|S (Australian Liberty Alliance)|773|4,435|17.43%|
|A (Mature Australia)|610|4,440|13.74%|
|P (Marriage Equality)|460|4,032|11.41%|
|C (The Arts Party)|370|3,368|10.97%|
|T (Derryn Hinch's Justice Party)|363|2,359|15.37%|
|V (Voluntary Euthanasia Party)|349|2,286|15.25%|
|E (Australian Cyclists Party)|305|1,664|18.34%|
|B (Australian Labor Party)|282|289,902|0.10%|
|D (The Greens)|270|62,329|0.43%|
|G (Australian Progressives)|187|1,157|16.16%|
|I (Palmer United Party)|182|778|23.33%|
|M (Christian Democratic Party (Fred Nile Group))|165|2,799|5.88%|
|UG (Ungrouped)|152|851|17.82%|
|W (Citizens Electoral Council of Australia)|140|499|28.05%|
|L (VOTEFLUX.ORG &#124; Upgrade Democracy!)|132|820|16.13%|
|N (Family First Party)|23|30,464|0.08%|
|**Total**|**21,578**|**1,061,165**|**2.03%**|

### By group in Tasmania

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|3,525|110,318|3.20%|
|H (Australian Sex Party/Marijuana (HEMP) Party)|948|4,493|21.11%|
|A (Family First Party)|868|6,692|12.97%|
|P (Shooters, Fishers and Farmers)|797|4,688|17.00%|
|E (Nick Xenophon Team)|350|5,128|6.82%|
|S (Australian Recreational Fishers Party)|333|2,376|14.04%|
|Q (Animal Justice Party)|316|2,377|13.29%|
|G (Palmer United Party)|306|2,363|12.95%|
|T (Liberal Democratic Party)|265|1,662|15.93%|
|D (Christian Democratic Party (Fred Nile Group))|256|2,861|8.93%|
|M (Jacqui Lambie Network)|249|28,146|0.88%|
|B (Australian Labor Party)|212|113,935|0.19%|
|N (Australian Liberty Alliance)|206|1,112|18.49%|
|L (Renewable Energy Party)|195|1,340|14.55%|
|J (Derryn Hinch's Justice Party)|191|1,473|12.99%|
|R (Science Party)|163|1,306|12.45%|
|O (VOTEFLUX.ORG &#124; Upgrade Democracy!)|110|946|11.65%|
|U (The Arts Party)|109|728|14.95%|
|UG (Ungrouped)|86|498|17.32%|
|K (Citizens Electoral Council of Australia)|57|177|32.14%|
|C (The Greens)|22|37,840|0.06%|
|I (Pauline Hanson's One Nation)|3|8,700|0.03%|
|**Total**|**9,567**|**339,159**|**2.82%**|

### By group in Victoria

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|Y (Pauline Hanson's One Nation)|40,881|63,528|64.35%|
|AL (Australian Sex Party)|23,072|54,128|42.62%|
|C (Animal Justice Party)|20,937|60,780|34.45%|
|AG (Shooters, Fishers and Farmers)|20,773|36,669|56.65%|
|O (Family First Party)|18,308|39,747|46.06%|
|AH (Liberal Democratic Party)|16,347|55,501|29.45%|
|D (Australian Labor Party)|13,206|1,075,658|1.23%|
|H (Australian Christians)|13,000|34,763|37.40%|
|W (Australian Motoring Enthusiast Party)|12,118|31,785|38.13%|
|AC (Drug Law Reform)|10,385|23,384|44.41%|
|V (Nick Xenophon Team)|10,374|55,118|18.82%|
|U (Australian Liberty Alliance)|9,805|23,080|42.48%|
|L (Health Australia Party)|8,769|17,169|51.07%|
|R (Democratic Labour Party)|8,679|18,152|47.81%|
|J (Pirate Party Australia)|6,509|13,424|48.49%|
|G (Jacqui Lambie Network)|6,059|15,288|39.63%|
|X (Marriage Equality)|5,652|17,139|32.98%|
|AI (Rise Up Australia Party)|5,110|10,166|50.26%|
|A (Derryn Hinch's Justice Party)|4,180|211,733|1.97%|
|I (Sustainable Australia)|4,083|10,574|38.61%|
|P (Christian Democratic Party (Fred Nile Group))|4,043|9,287|43.54%|
|E (Science Party / Cyclists Party)|3,935|11,567|34.02%|
|AK (The Greens)|3,926|380,499|1.03%|
|AA (Australian Country Party)|3,595|9,316|38.59%|
|M (Renewable Energy Party)|3,025|8,845|34.20%|
|F (Palmer United Party)|2,964|10,456|28.35%|
|Q (The Arts Party)|2,579|7,737|33.33%|
|AB (MFP)|2,338|5,268|44.39%|
|AD (Voluntary Euthanasia Party)|2,001|5,768|34.69%|
|AF (Liberal & Nationals)|2,000|1,158,800|0.17%|
|K (Socialist Equality Party)|1,963|3,293|59.60%|
|AE (Mature Australia)|1,850|3,469|53.32%|
|B (Independent)|1,680|3,386|49.60%|
|S (Citizens Electoral Council of Australia)|1,379|2,098|65.72%|
|N (VOTEFLUX.ORG &#124; Upgrade Democracy!)|1,210|2,838|42.63%|
|UG (Ungrouped)|1,192|2,860|41.69%|
|T (Secular Party of Australia)|1,104|2,303|47.94%|
|Z (Socialist Alliance)|784|2,597|30.18%|
|AJ (Australian Progressives)|619|2,064|30.01%|
|**Total**|**300,431**|**3,500,237**|**8.58%**|

### By group in Western Australia

|Group|Exhausted votes|Total formal ballots for group|%|
|---|---|---|---|
|F (The Nationals)|16,512|34,618|47.70%|
|W (Australian Christians)|14,068|22,076|63.73%|
|B (Shooters, Fishers and Farmers)|6,599|25,343|26.04%|
|S (Australian Sex Party/Marijuana (HEMP) Party)|6,294|25,047|25.13%|
|A (Christian Democratic Party (Fred Nile Group))|5,811|13,768|42.21%|
|P (Australian Liberty Alliance)|5,158|15,168|34.00%|
|C (Nick Xenophon Team)|4,779|29,656|16.12%|
|Z (Liberal Democratic Party)|3,813|10,771|35.40%|
|AB (Family First Party)|3,195|8,729|36.61%|
|K (Animal Justice Party)|2,731|12,687|21.52%|
|T (Democratic Labour Party)|2,360|9,405|25.09%|
|H (Derryn Hinch's Justice Party)|1,831|10,066|18.19%|
|D (Australian Labor Party)|1,820|386,113|0.47%|
|U (Health Australia Party)|1,497|4,763|31.43%|
|I (Palmer United Party)|1,187|4,992|23.77%|
|Q (Rise Up Australia Party)|1,106|3,695|29.93%|
|O (Renewable Energy Party)|936|4,617|20.28%|
|X (Liberal Party of Australia)|906|525,879|0.17%|
|Y (Australia First Party)|884|3,005|29.42%|
|L (Mature Australia)|807|2,687|30.04%|
|UG (Ungrouped)|724|2,184|33.16%|
|M (The Arts Party)|701|3,034|23.10%|
|E (Citizens Electoral Council of Australia)|670|2,049|32.71%|
|N (Australian Cyclists Party)|652|2,679|24.33%|
|G (Socialist Alliance)|412|1,989|20.73%|
|V (Independent)|386|949|40.69%|
|AA (VOTEFLUX.ORG &#124; Upgrade Democracy!)|301|1,390|21.62%|
|J (The Greens (WA))|190|143,797|0.13%|
|R (Pauline Hanson's One Nation)|53|55,026|0.10%|
|**Total**|**86,382**|**1,366,182**|**6.32%**|

### By division

|State|Division|Exhausted votes|Total formal ballots for division|%|
|---|---|---|---|---|
|VIC|Gippsland|13,700|91,696|14.94%|
|VIC|Murray|12,802|91,912|13.93%|
|VIC|McMillan|12,550|103,623|12.11%|
|VIC|McEwen|12,177|116,948|10.41%|
|VIC|Mallee|12,172|87,316|13.94%|
|VIC|Indi|11,819|93,028|12.71%|
|WA|O'Connor|11,797|88,046|13.40%|
|NSW|Calare|11,236|101,908|11.03%|
|NSW|Cowper|11,208|105,049|10.67%|
|NSW|New England|10,949|98,825|11.08%|
|VIC|Lalor|10,755|107,821|9.97%|
|VIC|Holt|10,633|101,061|10.52%|
|VIC|Gorton|10,621|99,397|10.68%|
|NSW|Page|10,518|105,314|9.99%|
|NSW|Farrer|10,504|98,050|10.71%|
|NSW|Hunter|10,430|101,470|10.28%|
|VIC|Bendigo|10,406|98,858|10.53%|
|VIC|Wannon|10,332|88,716|11.65%|
|NSW|Riverina|10,193|99,749|10.22%|
|NSW|Newcastle|10,159|101,094|10.05%|
|NSW|Eden-Monaro|10,019|97,685|10.26%|
|NSW|Cunningham|9,986|98,289|10.16%|
|NSW|Whitlam|9,807|100,060|9.80%|
|VIC|Ballarat|9,805|99,124|9.89%|
|NSW|Lyne|9,613|101,063|9.51%|
|NSW|Parkes|9,546|94,716|10.08%|
|NSW|Chifley|9,524|88,789|10.73%|
|NSW|Fowler|9,458|88,417|10.70%|
|NSW|Gilmore|9,452|103,993|9.09%|
|VIC|Calwell|9,284|90,982|10.20%|
|NSW|Macquarie|9,253|95,408|9.70%|
|NSW|Werriwa|9,232|91,662|10.07%|
|NSW|Richmond|9,190|98,660|9.31%|
|NSW|McMahon|9,189|89,555|10.26%|
|NSW|Shortland|9,044|99,696|9.07%|
|NSW|Watson|9,031|87,200|10.36%|
|VIC|Flinders|8,875|102,326|8.67%|
|NSW|Lindsay|8,831|97,434|9.06%|
|VIC|Corio|8,798|95,512|9.21%|
|QLD|Leichhardt|8,696|92,437|9.41%|
|NSW|Hume|8,682|97,350|8.92%|
|NSW|Macarthur|8,628|92,622|9.31%|
|NSW|Barton|8,616|90,875|9.48%|
|NSW|Paterson|8,615|102,146|8.43%|
|VIC|Scullin|8,539|96,802|8.82%|
|NSW|Sydney|8,483|92,677|9.15%|
|WA|Durack|8,448|78,248|10.80%|
|NSW|Blaxland|8,398|83,969|10.00%|
|VIC|Casey|8,372|92,985|9.00%|
|NSW|Grayndler|8,346|92,786|8.99%|
|NSW|Mackellar|8,288|96,196|8.62%|
|NSW|Bennelong|8,184|94,573|8.65%|
|NSW|Dobell|8,144|98,944|8.23%|
|NSW|Greenway|8,061|93,556|8.62%|
|QLD|Fairfax|8,052|95,601|8.42%|
|NSW|Warringah|7,967|90,669|8.79%|
|QLD|Rankin|7,952|87,211|9.12%|
|NSW|Berowra|7,844|96,129|8.16%|
|NSW|Kingsford Smith|7,834|94,825|8.26%|
|NSW|North Sydney|7,801|95,479|8.17%|
|QLD|Longman|7,757|92,881|8.35%|
|QLD|Groom|7,744|92,084|8.41%|
|NSW|Robertson|7,737|97,333|7.95%|
|VIC|La Trobe|7,735|95,866|8.07%|
|NSW|Hughes|7,724|95,574|8.08%|
|NSW|Banks|7,718|92,859|8.31%|
|QLD|Forde|7,659|86,059|8.90%|
|NSW|Parramatta|7,638|87,315|8.75%|
|QLD|Kennedy|7,605|86,698|8.77%|
|QLD|Dickson|7,595|91,690|8.28%|
|QLD|Petrie|7,404|93,917|7.88%|
|VIC|Maribyrnong|7,362|94,811|7.76%|
|QLD|McPherson|7,327|89,343|8.20%|
|QLD|Bowman|7,320|92,954|7.88%|
|QLD|Herbert|7,220|91,913|7.85%|
|VIC|Corangamite|7,206|101,208|7.12%|
|NSW|Bradfield|7,197|95,426|7.54%|
|QLD|Wide Bay|7,149|91,228|7.84%|
|VIC|Isaacs|7,094|93,032|7.63%|
|VIC|Gellibrand|7,091|92,824|7.64%|
|QLD|Fisher|7,050|86,931|8.11%|
|QLD|Maranoa|6,977|92,990|7.50%|
|NSW|Reid|6,947|92,958|7.47%|
|VIC|Dunkley|6,934|92,801|7.47%|
|NSW|Mitchell|6,852|93,531|7.33%|
|QLD|Moncrieff|6,831|86,929|7.86%|
|QLD|Fadden|6,827|89,730|7.61%|
|VIC|Hotham|6,796|87,677|7.75%|
|QLD|Blair|6,794|87,784|7.74%|
|QLD|Dawson|6,793|91,665|7.41%|
|NSW|Cook|6,708|93,322|7.19%|
|VIC|Bruce|6,679|83,947|7.96%|
|VIC|Wills|6,640|96,962|6.85%|
|VIC|Aston|6,616|86,266|7.67%|
|QLD|Lilley|6,606|95,588|6.91%|
|QLD|Bonner|6,564|90,759|7.23%|
|QLD|Wright|6,496|89,811|7.23%|
|QLD|Oxley|6,419|84,412|7.60%|
|WA|Forrest|6,394|87,433|7.31%|
|QLD|Moreton|6,364|86,424|7.36%|
|QLD|Hinkler|6,278|89,554|7.01%|
|QLD|Capricornia|6,242|89,065|7.01%|
|VIC|Batman|6,117|94,648|6.46%|
|QLD|Flynn|6,056|90,639|6.68%|
|NSW|Wentworth|6,041|86,997|6.94%|
|WA|Pearce|6,004|89,503|6.71%|
|QLD|Ryan|5,955|95,859|6.21%|
|QLD|Griffith|5,865|94,267|6.22%|
|VIC|Menzies|5,793|89,299|6.49%|
|WA|Burt|5,736|86,071|6.66%|
|QLD|Brisbane|5,642|96,743|5.83%|
|VIC|Deakin|5,520|90,788|6.08%|
|WA|Cowan|5,211|83,895|6.21%|
|VIC|Jagajaga|5,208|92,862|5.61%|
|WA|Canning|5,127|86,088|5.96%|
|VIC|Chisholm|5,061|87,447|5.79%|
|WA|Hasluck|4,792|83,211|5.76%|
|WA|Brand|4,667|83,309|5.60%|
|VIC|Melbourne|4,534|96,863|4.68%|
|WA|Tangney|4,464|84,692|5.27%|
|VIC|Goldstein|4,446|94,266|4.72%|
|WA|Moore|4,417|89,071|4.96%|
|WA|Stirling|4,281|84,731|5.05%|
|VIC|Melbourne Ports|4,241|87,080|4.87%|
|WA|Swan|4,119|83,100|4.96%|
|VIC|Higgins|3,870|92,981|4.16%|
|VIC|Kooyong|3,848|90,502|4.25%|
|WA|Fremantle|3,828|86,272|4.44%|
|WA|Perth|3,607|85,562|4.22%|
|WA|Curtin|3,490|86,950|4.01%|
|SA|Wakefield|2,909|98,476|2.95%|
|SA|Barker|2,523|95,669|2.64%|
|SA|Port Adelaide|2,286|98,910|2.31%|
|TAS|Braddon|2,258|65,724|3.44%|
|SA|Grey|2,222|90,793|2.45%|
|TAS|Lyons|2,217|69,549|3.19%|
|TAS|Bass|2,074|66,777|3.11%|
|SA|Makin|2,007|96,111|2.09%|
|SA|Kingston|1,909|95,095|2.01%|
|SA|Mayo|1,760|95,749|1.84%|
|SA|Hindmarsh|1,594|100,105|1.59%|
|TAS|Franklin|1,559|70,216|2.22%|
|SA|Boothby|1,549|98,242|1.58%|
|TAS|Denison|1,459|66,893|2.18%|
|SA|Sturt|1,433|94,640|1.51%|
|SA|Adelaide|1,386|97,375|1.42%|
|ACT|Canberra|56|131,653|0.04%|
|ACT|Fenner|53|123,114|0.04%|
|NT|Solomon|0|57,000|0.00%|
|NT|Lingiari|0|45,027|0.00%|
|**Total**|****|**1,042,132**|**13,838,900**|**7.53%**|
