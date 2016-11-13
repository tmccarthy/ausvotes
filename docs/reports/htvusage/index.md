---
layout: page
title: How to vote card usage
---

These tables cross-tabulate the number of votes that exactly match a 
[how-to-vote card](https://en.wikipedia.org/wiki/How-to-vote_card) issued at the 2016 federal Senate election.

* auto-gen TOC:
{:toc}

## Explanation

How to vote cards are composed by the [`HowToVoteCardGeneration` class](https://github.com/tmccarthy/SenateDB/blob/master/src/main/scala/au/id/tmm/senatedb/parsing/HowToVoteCardGeneration.scala),
using the cards [listed on the ABC election website](http://www.abc.net.au/news/federal-election-2016/guide/svic/htv/).
Ballots are then matched against these cards by the [`MatchingHowToVoteCalculator` class](https://github.com/tmccarthy/SenateDB/blob/master/src/main/scala/au/id/tmm/senatedb/computations/howtovote/MatchingHowToVoteCalculator.scala).

Many how-to-vote cards (eg from the Jacquie Lambie Network) simply told voters where to put their first 
preference, and then asked them to mark the rest as they liked. Others (eg the Motoring Enthusiasts) gave suggestions 
about which parties to preference, but left the voter to choose the order. Still others (eg Sustainable Australia) 
specified less than 6 preferences, and asked the voter to choose the rest.

To ensure comparisons between like and like, only how-to-vote cards that unambiguously specified at least 6 preferences
above the line have been included in this analysis. In order to be considered to match a how to vote card, a ballot must

* Match the above-the-line preferences on the how to vote card exactly, 
* Not specify more or fewer preferences than on the how-to-vote card,
* Not use a tick or cross to express its first preference,
* Not have marked below the line at all.

### Bad how-to-vote cards

In Tasmania, the [One Nation how-to-vote card](http://www.abc.net.au/news/federal-election-2016/guide/stas/htv/#I) 
listed the Shooters, Fishers and Farmers party (group AG) as its third preference. In fact, the Shooters, Fishers and 
Farmers party was in group P, and there was no group AG on the ballot paper. SenateDB corrects this mistake.

In New South Wales, the [Veteran's party how-to-vote card](http://www.abc.net.au/news/federal-election-2016/guide/snsw/htv/#T)
listed the Cycling Party as fourth preference, and the Science Party as the fifth preference. In fact, these parties 
shared group I, and so had to be preferenced together. Because it would have been impossible to follow this how-to-vote
card and have completed a formal ballot paper, it has been ignored. 

## Tables

### National total

| |Ballots matching an HTV card|Total formal ballots|%|
|---|---|---|---|
|**Total**|**2,010,114**|**13,838,900**|**14.53%**|

### Nationally by first-preferenced party

|Party|Ballots matching an HTV card|Total formal ballots for party|%|
|---|---|---|---|
|Liberal Party of Australia|1,322,391|4,821,314|27.43%|
|Australian Labor Party|522,915|4,123,084|12.68%|
|The Greens|112,670|1,197,657|9.41%|
|Pauline Hanson's One Nation|17,617|593,013|2.97%|
|Australian Liberty Alliance|8,813|102,982|8.56%|
|Family First Party|6,611|191,112|3.46%|
|Australian Christians|4,492|66,525|6.75%|
|Shooters, Fishers and Farmers|3,542|192,923|1.84%|
|Animal Justice Party|3,445|159,373|2.16%|
|Katter's Australian Party|2,360|53,199|4.44%|
|Glenn Lazarus Team|1,801|45,149|3.99%|
|Rise Up Australia Party|1,293|36,424|3.55%|
|Australian Sex Party|1,078|97,882|1.10%|
|Democratic Labour Party|322|94,510|0.34%|
|Socialist Alliance|188|9,968|1.89%|
|Australian Sex Party/Marijuana (HEMP) Party|170|69,247|0.25%|
|Marriage Equality|127|44,982|0.28%|
|Pirate Party Australia|106|35,184|0.30%|
|Science Party / Cyclists Party|73|24,673|0.30%|
|Liberal Democratic Party|56|298,915|0.02%|
|Australian Cyclists Party|34|25,438|0.13%|
|Veterans Party|6|10,391|0.06%|
|Science Party|3|5,405|0.06%|
|Renewable Energy Party|1|29,983|0.00%|
|**Total**|**2,010,114**|**13,838,900**|**14.53%**|

### By state

|State|Ballots matching an HTV card|Total formal ballots for party|%|
|---|---|---|---|
|VIC|691,540|3,500,237|19.76%|
|NSW|688,074|4,492,197|15.32%|
|QLD|295,329|2,723,166|10.85%|
|WA|236,394|1,366,182|17.30%|
|SA|42,941|1,061,165|4.05%|
|ACT|28,508|254,767|11.19%|
|TAS|13,744|339,159|4.05%|
|NT|13,584|102,027|13.31%|
|**Total**|**2,010,114**|**13,838,900**|**14.53%**|

### By group in the Australian Capital Territory

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|16,472|84,615|19.47%|
|C (Australian Labor Party)|9,317|96,667|9.64%|
|H (The Greens)|2,697|41,006|6.58%|
|D (Rise Up Australia Party)|22|2,523|0.87%|
|**Total**|**28,508**|**254,767**|**11.19%**|

### By group in New South Wales

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal & Nationals)|466,311|1,610,626|28.95%|
|N (Australian Labor Party)|181,766|1,405,088|12.94%|
|AL (The Greens)|31,603|332,860|9.49%|
|S (Pauline Hanson's One Nation)|2,381|184,012|1.29%|
|J (Shooters, Fishers and Farmers)|2,350|88,837|2.65%|
|AM (Australian Liberty Alliance)|2,273|29,795|7.63%|
|AB (Animal Justice Party)|895|37,991|2.36%|
|C (Family First Party)|122|53,027|0.23%|
|AG (Australian Sex Party)|93|30,038|0.31%|
|L (Socialist Alliance)|76|5,382|1.41%|
|I (Science Party / Cyclists Party)|54|18,367|0.29%|
|H (Democratic Labour Party)|54|51,510|0.10%|
|M (Rise Up Australia Party)|45|7,538|0.60%|
|R (Pirate Party Australia)|43|11,418|0.38%|
|D (Liberal Democratic Party)|8|139,007|0.01%|
|**Total**|**688,074**|**4,492,197**|**15.32%**|

### By group in the Northern Territory

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|E (Country Liberals (NT))|6,208|37,156|16.71%|
|F (Australian Labor Party (Northern Territory) Branch)|6,020|38,197|15.76%|
|D (The Greens)|1,284|11,003|11.67%|
|A (Rise Up Australia Party)|72|6,768|1.06%|
|**Total**|**13,584**|**102,027**|**13.31%**|

### By group in Queensland

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|G (Liberal National Party of Queensland)|183,011|960,467|19.05%|
|D (Australian Labor Party)|80,870|717,524|11.27%|
|X (Pauline Hanson's One Nation)|11,767|250,126|4.70%|
|AK (The Greens)|6,879|188,323|3.65%|
|N (Australian Liberty Alliance)|4,677|29,392|15.91%|
|T (Family First Party)|3,086|52,453|5.88%|
|I (Katter's Australian Party)|2,360|48,807|4.84%|
|AC (Glenn Lazarus Team)|1,801|45,149|3.99%|
|Y (Rise Up Australia Party)|345|5,734|6.02%|
|H (Animal Justice Party)|291|32,306|0.90%|
|AF (Australian Christians)|85|9,686|0.88%|
|V (Australian Sex Party/Marijuana (HEMP) Party)|84|30,157|0.28%|
|M (Pirate Party Australia)|30|10,342|0.29%|
|Q (Shooters, Fishers and Farmers)|16|29,571|0.05%|
|A (Australian Cyclists Party)|13|19,933|0.07%|
|S (Democratic Labour Party)|8|15,443|0.05%|
|AJ (Veterans Party)|6|4,534|0.13%|
|**Total**|**295,329**|**2,723,166**|**10.85%**|

### By group in South Australia

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|H (Liberal Party of Australia)|30,674|345,767|8.87%|
|B (Australian Labor Party)|5,390|289,902|1.86%|
|N (Family First Party)|2,984|30,464|9.80%|
|D (The Greens)|2,582|62,329|4.14%|
|O (Pauline Hanson's One Nation)|597|31,621|1.89%|
|S (Australian Liberty Alliance)|276|4,435|6.22%|
|Q (Shooters, Fishers and Farmers)|265|7,815|3.39%|
|U (Animal Justice Party)|140|8,981|1.56%|
|R (Australian Sex Party/Marijuana (HEMP) Party)|28|12,091|0.23%|
|E (Australian Cyclists Party)|5|1,664|0.30%|
|**Total**|**42,941**|**1,061,165**|**4.05%**|

### By group in Tasmania

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|9,824|110,318|8.91%|
|B (Australian Labor Party)|2,306|113,935|2.02%|
|C (The Greens)|1,490|37,840|3.94%|
|P (Shooters, Fishers and Farmers)|56|4,688|1.19%|
|I (Pauline Hanson's One Nation)|27|8,700|0.31%|
|N (Australian Liberty Alliance)|22|1,112|1.98%|
|A (Family First Party)|12|6,692|0.18%|
|H (Australian Sex Party/Marijuana (HEMP) Party)|4|4,493|0.09%|
|R (Science Party)|3|1,306|0.23%|
|**Total**|**13,744**|**339,159**|**4.05%**|

### By group in Victoria

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|AF (Liberal & Nationals)|446,721|1,158,800|38.55%|
|D (Australian Labor Party)|182,633|1,075,658|16.98%|
|AK (The Greens)|53,387|380,499|14.03%|
|C (Animal Justice Party)|1,928|60,780|3.17%|
|Y (Pauline Hanson's One Nation)|1,758|63,528|2.77%|
|H (Australian Christians)|1,697|34,763|4.88%|
|AL (Australian Sex Party)|985|54,128|1.82%|
|AG (Shooters, Fishers and Farmers)|657|36,669|1.79%|
|AI (Rise Up Australia Party)|641|10,166|6.31%|
|O (Family First Party)|363|39,747|0.91%|
|R (Democratic Labour Party)|260|18,152|1.43%|
|U (Australian Liberty Alliance)|253|23,080|1.10%|
|X (Marriage Equality)|127|17,139|0.74%|
|Z (Socialist Alliance)|67|2,597|2.58%|
|J (Pirate Party Australia)|33|13,424|0.25%|
|E (Science Party / Cyclists Party)|19|11,567|0.16%|
|AH (Liberal Democratic Party)|11|55,501|0.02%|
|**Total**|**691,540**|**3,500,237**|**19.76%**|

### By group in Western Australia

|Group|Ballots matching an HTV card|Total formal ballots for group|%|
|---|---|---|---|
|X (Liberal Party of Australia)|163,170|525,879|31.03%|
|D (Australian Labor Party)|54,613|386,113|14.14%|
|J (The Greens (WA))|12,748|143,797|8.87%|
|W (Australian Christians)|2,710|22,076|12.28%|
|P (Australian Liberty Alliance)|1,312|15,168|8.65%|
|R (Pauline Hanson's One Nation)|1,087|55,026|1.98%|
|B (Shooters, Fishers and Farmers)|198|25,343|0.78%|
|K (Animal Justice Party)|191|12,687|1.51%|
|Q (Rise Up Australia Party)|168|3,695|4.55%|
|S (Australian Sex Party/Marijuana (HEMP) Party)|54|25,047|0.22%|
|G (Socialist Alliance)|45|1,989|2.26%|
|AB (Family First Party)|44|8,729|0.50%|
|Z (Liberal Democratic Party)|37|10,771|0.34%|
|N (Australian Cyclists Party)|16|2,679|0.60%|
|O (Renewable Energy Party)|1|4,617|0.02%|
|**Total**|**236,394**|**1,366,182**|**17.30%**|

### By division

|State|Division|Ballots matching an HTV card|Total formal ballots for division|%|
|---|---|---|---|---|
|VIC|Corangamite|26,605|101,208|26.29%|
|VIC|Goldstein|24,837|94,266|26.35%|
|VIC|Higgins|23,701|92,981|25.49%|
|VIC|Flinders|23,677|102,326|23.14%|
|VIC|McMillan|23,558|103,623|22.73%|
|VIC|Bendigo|23,202|98,858|23.47%|
|VIC|Dunkley|22,888|92,801|24.66%|
|VIC|Kooyong|22,408|90,502|24.76%|
|VIC|McEwen|22,050|116,948|18.85%|
|WA|Curtin|21,873|86,950|25.16%|
|VIC|Deakin|21,166|90,788|23.31%|
|VIC|Ballarat|21,107|99,124|21.29%|
|VIC|La Trobe|20,969|95,866|21.87%|
|VIC|Jagajaga|20,877|92,862|22.48%|
|NSW|Robertson|20,544|97,333|21.11%|
|VIC|Casey|20,350|92,985|21.89%|
|NSW|Cook|20,276|93,322|21.73%|
|VIC|Corio|20,179|95,512|21.13%|
|VIC|Menzies|19,673|89,299|22.03%|
|NSW|Gilmore|19,653|103,993|18.90%|
|VIC|Chisholm|19,564|87,447|22.37%|
|VIC|Murray|19,252|91,912|20.95%|
|VIC|Wannon|19,178|88,716|21.62%|
|NSW|Bradfield|18,968|95,426|19.88%|
|NSW|Wentworth|18,915|86,997|21.74%|
|NSW|Mackellar|18,672|96,196|19.41%|
|VIC|Aston|18,583|86,266|21.54%|
|VIC|Gippsland|18,199|91,696|19.85%|
|VIC|Isaacs|17,983|93,032|19.33%|
|NSW|Berowra|17,957|96,129|18.68%|
|NSW|Dobell|17,759|98,944|17.95%|
|NSW|Warringah|17,737|90,669|19.56%|
|WA|Tangney|17,541|84,692|20.71%|
|NSW|Richmond|17,533|98,660|17.77%|
|NSW|Hughes|17,441|95,574|18.25%|
|NSW|Page|17,422|105,314|16.54%|
|WA|Canning|17,251|86,088|20.04%|
|NSW|North Sydney|17,139|95,479|17.95%|
|NSW|Lyne|17,003|101,063|16.82%|
|VIC|Melbourne Ports|16,978|87,080|19.50%|
|WA|Moore|16,760|89,071|18.82%|
|VIC|Bruce|16,755|83,947|19.96%|
|NSW|Macquarie|16,743|95,408|17.55%|
|NSW|Eden-Monaro|16,739|97,685|17.14%|
|WA|Forrest|16,572|87,433|18.95%|
|WA|Stirling|16,438|84,731|19.40%|
|NSW|Mitchell|16,431|93,531|17.57%|
|NSW|Hume|16,366|97,350|16.81%|
|NSW|Paterson|16,305|102,146|15.96%|
|VIC|Maribyrnong|16,128|94,811|17.01%|
|NSW|Bennelong|16,033|94,573|16.95%|
|VIC|Indi|15,871|93,028|17.06%|
|VIC|Batman|15,813|94,648|16.71%|
|ACT|Canberra|15,778|131,653|11.98%|
|NSW|Reid|15,712|92,958|16.90%|
|NSW|Cowper|15,676|105,049|14.92%|
|WA|Perth|15,434|85,562|18.04%|
|NSW|Shortland|15,434|99,696|15.48%|
|NSW|Banks|15,422|92,859|16.61%|
|VIC|Hotham|15,402|87,677|17.57%|
|VIC|Mallee|15,292|87,316|17.51%|
|QLD|Brisbane|15,175|96,743|15.69%|
|VIC|Lalor|15,014|107,821|13.92%|
|VIC|Melbourne|14,989|96,863|15.47%|
|NSW|Kingsford Smith|14,947|94,825|15.76%|
|NSW|Lindsay|14,897|97,434|15.29%|
|WA|Fremantle|14,852|86,272|17.22%|
|WA|Swan|14,842|83,100|17.86%|
|WA|Hasluck|14,771|83,211|17.75%|
|QLD|Griffith|14,595|94,267|15.48%|
|NSW|Newcastle|14,239|101,094|14.08%|
|NSW|Riverina|14,213|99,749|14.25%|
|QLD|Bowman|14,102|92,954|15.17%|
|VIC|Wills|14,091|96,962|14.53%|
|QLD|Lilley|13,989|95,588|14.63%|
|VIC|Gellibrand|13,872|92,824|14.94%|
|VIC|Holt|13,808|101,061|13.66%|
|NSW|New England|13,673|98,825|13.84%|
|NSW|Farrer|13,648|98,050|13.92%|
|NSW|Grayndler|13,626|92,786|14.69%|
|NSW|Whitlam|13,626|100,060|13.62%|
|VIC|Scullin|13,459|96,802|13.90%|
|NSW|Cunningham|13,207|98,289|13.44%|
|WA|Cowan|13,059|83,895|15.57%|
|WA|Burt|12,907|86,071|15.00%|
|NSW|Parkes|12,901|94,716|13.62%|
|VIC|Gorton|12,827|99,397|12.90%|
|QLD|Ryan|12,742|95,859|13.29%|
|ACT|Fenner|12,730|123,114|10.34%|
|NSW|Calare|12,593|101,908|12.36%|
|NSW|Greenway|12,375|93,556|13.23%|
|NSW|Sydney|12,329|92,677|13.30%|
|WA|Pearce|12,217|89,503|13.65%|
|QLD|Fadden|12,194|89,730|13.59%|
|NSW|Barton|12,046|90,875|13.26%|
|WA|Brand|12,000|83,309|14.40%|
|NSW|Macarthur|11,946|92,622|12.90%|
|QLD|Moncrieff|11,919|86,929|13.71%|
|VIC|Calwell|11,235|90,982|12.35%|
|WA|O'Connor|11,150|88,046|12.66%|
|NSW|Hunter|11,127|101,470|10.97%|
|QLD|Dickson|11,030|91,690|12.03%|
|NSW|Parramatta|10,960|87,315|12.55%|
|QLD|Wright|10,231|89,811|11.39%|
|QLD|Petrie|10,109|93,917|10.76%|
|QLD|Moreton|10,088|86,424|11.67%|
|QLD|Bonner|9,942|90,759|10.95%|
|QLD|Rankin|9,782|87,211|11.22%|
|QLD|Longman|9,671|92,881|10.41%|
|QLD|Hinkler|9,666|89,554|10.79%|
|QLD|Forde|9,331|86,059|10.84%|
|QLD|Fairfax|9,210|95,601|9.63%|
|QLD|Fisher|9,189|86,931|10.57%|
|QLD|McPherson|8,969|89,343|10.04%|
|NSW|McMahon|8,872|89,555|9.91%|
|NT|Solomon|8,839|57,000|15.51%|
|NSW|Werriwa|8,816|91,662|9.62%|
|WA|Durack|8,727|78,248|11.15%|
|QLD|Oxley|8,526|84,412|10.10%|
|QLD|Kennedy|8,379|86,698|9.66%|
|QLD|Blair|8,265|87,784|9.42%|
|NSW|Watson|8,149|87,200|9.35%|
|QLD|Wide Bay|8,135|91,228|8.92%|
|NSW|Chifley|7,980|88,789|8.99%|
|QLD|Leichhardt|7,921|92,437|8.57%|
|NSW|Fowler|7,592|88,417|8.59%|
|QLD|Dawson|7,580|91,665|8.27%|
|QLD|Groom|7,538|92,084|8.19%|
|QLD|Capricornia|7,496|89,065|8.42%|
|QLD|Herbert|7,451|91,913|8.11%|
|SA|Grey|7,428|90,793|8.18%|
|QLD|Flynn|6,609|90,639|7.29%|
|NSW|Blaxland|6,432|83,969|7.66%|
|QLD|Maranoa|5,495|92,990|5.91%|
|NT|Lingiari|4,745|45,027|10.54%|
|SA|Boothby|4,608|98,242|4.69%|
|SA|Mayo|4,587|95,749|4.79%|
|SA|Sturt|4,510|94,640|4.77%|
|SA|Barker|4,151|95,669|4.34%|
|SA|Hindmarsh|3,912|100,105|3.91%|
|TAS|Bass|3,814|66,777|5.71%|
|SA|Adelaide|3,587|97,375|3.68%|
|SA|Makin|3,174|96,111|3.30%|
|TAS|Lyons|2,966|69,549|4.26%|
|TAS|Franklin|2,767|70,216|3.94%|
|SA|Wakefield|2,628|98,476|2.67%|
|TAS|Braddon|2,530|65,724|3.85%|
|SA|Kingston|2,511|95,095|2.64%|
|SA|Port Adelaide|1,845|98,910|1.87%|
|TAS|Denison|1,667|66,893|2.49%|
|**Total**|****|**2,010,114**|**13,838,900**|**14.53%**|
