---
layout: page
title: Ballots with only '1' above the line
---

These tables cross-tabulate the number of Senate ballots at the 2016 federal election that marked only one preference 
above the line.

At the 2013 election, voters could choose between expressing a single, first preference above the line, or numbering 
every square below the line. For votes above the line, the preferences were determined by the 
[group voting ticket](https://en.wikipedia.org/wiki/Group_voting_ticket) of the group that received the first 
preference.

Changes to the Electoral Act in 2016 changed how preferences were expressed on Senate ballots. Group voting tickets were
abolished, and voters were told to number at least 6 squares above the line, or at least 12 below the line. In fact, 
savings measures in [section 269](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) of the Act 
meant that a ballot would still be regarded as formal if at least a first preference was expressed. This was done, at 
least in part, to save the ballots of voters who only numbered 1 square above the line as at previous elections.

It is important to note that while numbering a single square above the line did not render a ballot informal at the 2016
election, it did not have the same effect as doing so at the 2013 election. Without a group voting ticket, ballots with
only one square marked above the line would almost always exhaust, as they did not express further preferences.

In SenateDB, the [`CountOneAtl` class](https://github.com/tmccarthy/SenateDB/blob/v0.4.1/src/main/scala/au/id/tmm/senatedb/tallies/CountOneAtl.scala)
is responsible for determining whether a ballot has marked a single square above the line. To be counted, a ballot must:

* Have expressed a single first preference above the line, either with a '1', a tick or a cross (as per 
[section 269(1A)(a)](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) of the Act), and
* Have not marked any squares below the line.

* auto-gen TOC:
{:toc}

### National total

| |Ballots with only '1' above the line|Total formal ballots|%|
|---|---|---|---|
|**Total**|**290,758**|**13,838,900**|**2.10%**|

### Nationally by first-preferenced party

|Party|Ballots with only '1' above the line|Total formal ballots for party|%|
|---|---|---|---|
|Australian Labor Party|110,121|4,123,084|2.67%|
|Liberal Party of Australia|94,443|4,821,314|1.96%|
|Pauline Hanson's One Nation|14,984|593,013|2.53%|
|Liberal Democratic Party|10,066|298,915|3.37%|
|The Greens|7,789|1,197,657|0.65%|
|Shooters, Fishers and Farmers|5,028|192,923|2.61%|
|Nick Xenophon Team|4,698|456,369|1.03%|
|Democratic Labour Party|4,294|94,510|4.54%|
|Derryn Hinch's Justice Party|4,222|266,607|1.58%|
|Family First Party|3,816|191,112|2.00%|
|Christian Democratic Party (Fred Nile Group)|3,388|162,155|2.09%|
|Animal Justice Party|2,706|159,373|1.70%|
|Australian Sex Party|2,455|97,882|2.51%|
|Australian Liberty Alliance|2,056|102,982|2.00%|
|Australian Sex Party/Marijuana (HEMP) Party|1,942|69,247|2.80%|
|Health Australia Party|1,830|85,233|2.15%|
|Drug Law Reform|1,493|61,327|2.43%|
|Independent|1,183|23,437|5.05%|
|Marijuana (HEMP) Party|1,050|33,387|3.14%|
|Online Direct Democracy - (Empowering the People!)|845|12,018|7.03%|
|Australian Motoring Enthusiast Party|843|53,232|1.58%|
|Jacqui Lambie Network|792|69,074|1.15%|
|Pirate Party Australia|764|35,184|2.17%|
|Katter's Australian Party|737|53,199|1.39%|
|Australian Christians|703|66,525|1.06%|
|Rise Up Australia Party|695|36,424|1.91%|
|The Arts Party|605|37,702|1.60%|
|Marriage Equality|544|44,982|1.21%|
|Mature Australia|500|19,354|2.58%|
|Palmer United Party|500|26,210|1.91%|
|VOTEFLUX.ORG &#124; Upgrade Democracy!|487|21,151|2.30%|
|Seniors United Party of Australia|486|22,213|2.19%|
|Voluntary Euthanasia Party|461|23,252|1.98%|
|Science Party / Cyclists Party|417|24,673|1.69%|
|Glenn Lazarus Team|363|45,149|0.80%|
|Australian Cyclists Party|353|25,438|1.39%|
|The Nationals|333|46,932|0.71%|
|Sustainable Australia|325|26,341|1.23%|
|Veterans Party|307|10,391|2.95%|
|Renewable Energy Party|278|29,983|0.93%|
|Citizens Electoral Council of Australia|271|9,850|2.75%|
|Socialist Alliance|267|9,968|2.68%|
|Socialist Equality Party|255|7,865|3.24%|
|Australian Country Party|223|9,316|2.39%|
|Secular Party of Australia|202|11,077|1.82%|
|CountryMinded|179|5,989|2.99%|
|Australian Progressives|159|6,251|2.54%|
|Non-Custodial Parents Party (Equal Parenting)|120|2,102|5.71%|
|Australia First Party|75|3,005|2.50%|
|MFP|74|5,268|1.40%|
|Australian Recreational Fishers Party|23|2,376|0.97%|
|Science Party|8|5,405|0.15%|
|**Total**|**290,758**|**13,838,900**|**2.10%**|

### By state

|State|Ballots with only '1' above the line|Total formal ballots for party|%|
|---|---|---|---|
|NSW|162,340|4,492,197|3.61%|
|VIC|49,386|3,500,237|1.41%|
|QLD|34,730|2,723,166|1.28%|
|WA|21,080|1,366,182|1.54%|
|SA|17,091|1,061,165|1.61%|
|ACT|2,496|254,767|0.98%|
|TAS|2,249|339,159|0.66%|
|NT|1,386|102,027|1.36%|
|**Total**|**290,758**|**13,838,900**|**2.10%**|

### By group in the Australian Capital Territory

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|1,204|84,615|1.42%|
|C (Australian Labor Party)|769|96,667|0.80%|
|J (Australian Sex Party)|157|10,096|1.56%|
|A (Liberal Democratic Party)|123|7,460|1.65%|
|H (The Greens)|107|41,006|0.26%|
|G (Animal Justice Party)|46|4,251|1.08%|
|D (Rise Up Australia Party)|33|2,523|1.31%|
|I (Christian Democratic Party (Fred Nile Group))|26|3,087|0.84%|
|E (Sustainable Australia)|25|2,678|0.93%|
|B (Secular Party of Australia)|6|1,378|0.44%|
|**Total**|**2,496**|**254,767**|**0.98%**|

### By group in New South Wales

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|N (Australian Labor Party)|63,298|1,405,088|4.50%|
|F (Liberal & Nationals)|57,020|1,610,626|3.54%|
|S (Pauline Hanson's One Nation)|7,409|184,012|4.03%|
|D (Liberal Democratic Party)|6,605|139,007|4.75%|
|J (Shooters, Fishers and Farmers)|3,217|88,837|3.62%|
|AL (The Greens)|3,088|332,860|0.93%|
|AF (Christian Democratic Party (Fred Nile Group))|2,891|121,379|2.38%|
|H (Democratic Labour Party)|2,853|51,510|5.54%|
|C (Family First Party)|1,701|53,027|3.21%|
|A (Health Australia Party)|1,385|53,154|2.61%|
|AG (Australian Sex Party)|1,326|30,038|4.41%|
|AO (Marijuana (HEMP) Party)|1,050|29,510|3.56%|
|AI (Nick Xenophon Team)|995|80,111|1.24%|
|AB (Animal Justice Party)|974|37,991|2.56%|
|P (Derryn Hinch's Justice Party)|797|26,720|2.98%|
|AM (Australian Liberty Alliance)|796|29,795|2.67%|
|O (Online Direct Democracy - (Empowering the People!))|732|6,353|11.52%|
|AJ (Drug Law Reform)|632|20,883|3.03%|
|G (Independent)|557|3,871|14.39%|
|AA (Australian Motoring Enthusiast Party)|500|16,356|3.06%|
|B (Seniors United Party of Australia)|486|22,213|2.19%|
|E (VOTEFLUX.ORG &#124; Upgrade Democracy!)|378|12,578|3.01%|
|Q (Jacqui Lambie Network)|370|16,502|2.24%|
|K (Voluntary Euthanasia Party)|338|15,198|2.22%|
|M (Rise Up Australia Party)|337|7,538|4.47%|
|R (Pirate Party Australia)|310|11,418|2.72%|
|I (Science Party / Cyclists Party)|284|18,367|1.55%|
|AC (The Arts Party)|254|11,805|2.15%|
|T (Veterans Party)|231|5,857|3.94%|
|L (Socialist Alliance)|187|5,382|3.47%|
|AE (Mature Australia)|156|2,805|5.56%|
|X (Katter's Australian Party)|149|4,316|3.45%|
|Y (Palmer United Party)|149|2,805|5.31%|
|W (Socialist Equality Party)|149|2,933|5.08%|
|AK (Sustainable Australia)|134|7,723|1.74%|
|V (CountryMinded)|131|3,153|4.15%|
|AD (Non-Custodial Parents Party (Equal Parenting))|120|2,102|5.71%|
|AN (Renewable Energy Party)|102|8,936|1.14%|
|Z (Citizens Electoral Council of Australia)|101|1,895|5.33%|
|AH (Australian Progressives)|75|1,817|4.13%|
|U (Secular Party of Australia)|73|2,773|2.63%|
|**Total**|**162,340**|**4,492,197**|**3.61%**|

### By group in the Northern Territory

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|F (Australian Labor Party (Northern Territory) Branch)|501|38,197|1.31%|
|E (Country Liberals (NT))|473|37,156|1.27%|
|D (The Greens)|133|11,003|1.21%|
|B (Australian Sex Party/Marijuana (HEMP) Party)|132|4,956|2.66%|
|A (Rise Up Australia Party)|100|6,768|1.48%|
|C (Citizens Electoral Council of Australia)|24|1,255|1.91%|
|G (Christian Democratic Party (Fred Nile Group))|23|1,660|1.39%|
|**Total**|**1,386**|**102,027**|**1.36%**|

### By group in Queensland

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|G (Liberal National Party of Queensland)|11,488|960,467|1.20%|
|D (Australian Labor Party)|9,112|717,524|1.27%|
|X (Pauline Hanson's One Nation)|4,695|250,126|1.88%|
|E (Liberal Democratic Party)|1,454|77,601|1.87%|
|AK (The Greens)|927|188,323|0.49%|
|V (Australian Sex Party/Marijuana (HEMP) Party)|651|30,157|2.16%|
|T (Family First Party)|617|52,453|1.18%|
|I (Katter's Australian Party)|588|48,807|1.20%|
|Q (Shooters, Fishers and Farmers)|557|29,571|1.88%|
|H (Animal Justice Party)|482|32,306|1.49%|
|S (Democratic Labour Party)|422|15,443|2.73%|
|AG (Drug Law Reform)|364|17,060|2.13%|
|AC (Glenn Lazarus Team)|363|45,149|0.80%|
|L (Nick Xenophon Team)|340|55,653|0.61%|
|N (Australian Liberty Alliance)|325|29,392|1.11%|
|J (Marriage Equality)|293|23,811|1.23%|
|A (Australian Cyclists Party)|260|19,933|1.30%|
|O (Derryn Hinch's Justice Party)|206|14,256|1.45%|
|R (Independent)|199|1,536|12.96%|
|M (Pirate Party Australia)|174|10,342|1.68%|
|B (The Arts Party)|123|11,030|1.12%|
|AH (Health Australia Party)|116|10,147|1.14%|
|F (Online Direct Democracy - (Empowering the People!))|113|5,504|2.05%|
|AB (Palmer United Party)|101|4,816|2.10%|
|K (Mature Australia)|91|5,519|1.65%|
|AD (Jacqui Lambie Network)|86|9,138|0.94%|
|AF (Australian Christians)|84|9,686|0.87%|
|AJ (Veterans Party)|76|4,534|1.68%|
|AA (Christian Democratic Party (Fred Nile Group))|60|7,314|0.82%|
|C (Secular Party of Australia)|58|4,623|1.25%|
|Y (Rise Up Australia Party)|57|5,734|0.99%|
|U (Renewable Energy Party)|50|6,245|0.80%|
|AL (Sustainable Australia)|48|5,366|0.89%|
|AI (CountryMinded)|48|2,836|1.69%|
|W (VOTEFLUX.ORG &#124; Upgrade Democracy!)|29|1,881|1.54%|
|Z (Socialist Equality Party)|28|1,639|1.71%|
|P (Citizens Electoral Council of Australia)|23|1,877|1.23%|
|AE (Australian Progressives)|22|1,213|1.81%|
|**Total**|**34,730**|**2,723,166**|**1.28%**|

### By group in South Australia

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|B (Australian Labor Party)|5,618|289,902|1.94%|
|H (Liberal Party of Australia)|5,443|345,767|1.57%|
|F (Nick Xenophon Team)|2,649|230,703|1.15%|
|O (Pauline Hanson's One Nation)|711|31,621|2.25%|
|N (Family First Party)|632|30,464|2.07%|
|D (The Greens)|413|62,329|0.66%|
|R (Australian Sex Party/Marijuana (HEMP) Party)|372|12,091|3.08%|
|K (Liberal Democratic Party)|305|6,913|4.41%|
|Q (Shooters, Fishers and Farmers)|159|7,815|2.03%|
|U (Animal Justice Party)|143|8,981|1.59%|
|A (Mature Australia)|115|4,440|2.59%|
|J (Australian Motoring Enthusiast Party)|105|5,091|2.06%|
|S (Australian Liberty Alliance)|76|4,435|1.71%|
|P (Marriage Equality)|56|4,032|1.39%|
|E (Australian Cyclists Party)|48|1,664|2.88%|
|M (Christian Democratic Party (Fred Nile Group))|45|2,799|1.61%|
|C (The Arts Party)|44|3,368|1.31%|
|T (Derryn Hinch's Justice Party)|41|2,359|1.74%|
|I (Palmer United Party)|33|778|4.24%|
|V (Voluntary Euthanasia Party)|30|2,286|1.31%|
|G (Australian Progressives)|27|1,157|2.33%|
|L (VOTEFLUX.ORG &#124; Upgrade Democracy!)|15|820|1.83%|
|W (Citizens Electoral Council of Australia)|11|499|2.20%|
|**Total**|**17,091**|**1,061,165**|**1.61%**|

### By group in Tasmania

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|817|110,318|0.74%|
|B (Australian Labor Party)|728|113,935|0.64%|
|M (Jacqui Lambie Network)|134|28,146|0.48%|
|H (Australian Sex Party/Marijuana (HEMP) Party)|101|4,493|2.25%|
|I (Pauline Hanson's One Nation)|87|8,700|1.00%|
|C (The Greens)|79|37,840|0.21%|
|P (Shooters, Fishers and Farmers)|67|4,688|1.43%|
|A (Family First Party)|58|6,692|0.87%|
|T (Liberal Democratic Party)|33|1,662|1.99%|
|Q (Animal Justice Party)|24|2,377|1.01%|
|G (Palmer United Party)|23|2,363|0.97%|
|S (Australian Recreational Fishers Party)|23|2,376|0.97%|
|E (Nick Xenophon Team)|14|5,128|0.27%|
|N (Australian Liberty Alliance)|13|1,112|1.17%|
|D (Christian Democratic Party (Fred Nile Group))|12|2,861|0.42%|
|J (Derryn Hinch's Justice Party)|9|1,473|0.61%|
|L (Renewable Energy Party)|8|1,340|0.60%|
|R (Science Party)|8|1,306|0.61%|
|O (VOTEFLUX.ORG &#124; Upgrade Democracy!)|6|946|0.63%|
|U (The Arts Party)|4|728|0.55%|
|K (Citizens Electoral Council of Australia)|1|177|0.56%|
|**Total**|**2,249**|**339,159**|**0.66%**|

### By group in Victoria

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|D (Australian Labor Party)|22,680|1,075,658|2.11%|
|AF (Liberal & Nationals)|10,611|1,158,800|0.92%|
|A (Derryn Hinch's Justice Party)|2,995|211,733|1.41%|
|AK (The Greens)|2,095|380,499|0.55%|
|AH (Liberal Democratic Party)|1,247|55,501|2.25%|
|Y (Pauline Hanson's One Nation)|1,058|63,528|1.67%|
|AL (Australian Sex Party)|972|54,128|1.80%|
|C (Animal Justice Party)|823|60,780|1.35%|
|O (Family First Party)|671|39,747|1.69%|
|R (Democratic Labour Party)|656|18,152|3.61%|
|AG (Shooters, Fishers and Farmers)|629|36,669|1.72%|
|U (Australian Liberty Alliance)|528|23,080|2.29%|
|AC (Drug Law Reform)|497|23,384|2.13%|
|V (Nick Xenophon Team)|487|55,118|0.88%|
|H (Australian Christians)|364|34,763|1.05%|
|B (Independent)|318|3,386|9.39%|
|J (Pirate Party Australia)|280|13,424|2.09%|
|L (Health Australia Party)|251|17,169|1.46%|
|W (Australian Motoring Enthusiast Party)|238|31,785|0.75%|
|AA (Australian Country Party)|223|9,316|2.39%|
|G (Jacqui Lambie Network)|202|15,288|1.32%|
|X (Marriage Equality)|195|17,139|1.14%|
|P (Christian Democratic Party (Fred Nile Group))|142|9,287|1.53%|
|E (Science Party / Cyclists Party)|133|11,567|1.15%|
|I (Sustainable Australia)|118|10,574|1.12%|
|AI (Rise Up Australia Party)|114|10,166|1.12%|
|Q (The Arts Party)|114|7,737|1.47%|
|F (Palmer United Party)|109|10,456|1.04%|
|AD (Voluntary Euthanasia Party)|93|5,768|1.61%|
|K (Socialist Equality Party)|78|3,293|2.37%|
|AB (MFP)|74|5,268|1.40%|
|M (Renewable Energy Party)|71|8,845|0.80%|
|T (Secular Party of Australia)|65|2,303|2.82%|
|AE (Mature Australia)|62|3,469|1.79%|
|S (Citizens Electoral Council of Australia)|56|2,098|2.67%|
|Z (Socialist Alliance)|54|2,597|2.08%|
|N (VOTEFLUX.ORG &#124; Upgrade Democracy!)|48|2,838|1.69%|
|AJ (Australian Progressives)|35|2,064|1.70%|
|**Total**|**49,386**|**3,500,237**|**1.41%**|

### By group in Western Australia

|Group|Ballots with only '1' above the line|Total formal ballots for group|%|
|---|---|---|---|
|D (Australian Labor Party)|7,415|386,113|1.92%|
|X (Liberal Party of Australia)|7,387|525,879|1.40%|
|R (Pauline Hanson's One Nation)|1,024|55,026|1.86%|
|J (The Greens (WA))|947|143,797|0.66%|
|S (Australian Sex Party/Marijuana (HEMP) Party)|686|25,047|2.74%|
|B (Shooters, Fishers and Farmers)|399|25,343|1.57%|
|T (Democratic Labour Party)|363|9,405|3.86%|
|F (The Nationals)|333|34,618|0.96%|
|P (Australian Liberty Alliance)|318|15,168|2.10%|
|Z (Liberal Democratic Party)|299|10,771|2.78%|
|W (Australian Christians)|255|22,076|1.16%|
|K (Animal Justice Party)|214|12,687|1.69%|
|C (Nick Xenophon Team)|213|29,656|0.72%|
|A (Christian Democratic Party (Fred Nile Group))|189|13,768|1.37%|
|H (Derryn Hinch's Justice Party)|174|10,066|1.73%|
|AB (Family First Party)|137|8,729|1.57%|
|V (Independent)|109|949|11.49%|
|I (Palmer United Party)|85|4,992|1.70%|
|U (Health Australia Party)|78|4,763|1.64%|
|L (Mature Australia)|76|2,687|2.83%|
|Y (Australia First Party)|75|3,005|2.50%|
|M (The Arts Party)|66|3,034|2.18%|
|E (Citizens Electoral Council of Australia)|55|2,049|2.68%|
|Q (Rise Up Australia Party)|54|3,695|1.46%|
|O (Renewable Energy Party)|47|4,617|1.02%|
|N (Australian Cyclists Party)|45|2,679|1.68%|
|G (Socialist Alliance)|26|1,989|1.31%|
|AA (VOTEFLUX.ORG &#124; Upgrade Democracy!)|11|1,390|0.79%|
|**Total**|**21,080**|**1,366,182**|**1.54%**|

### By division

|State|Division|Ballots with only '1' above the line|Total formal ballots for division|%|
|---|---|---|---|---|
|NSW|Blaxland|7,203|83,969|8.58%|
|NSW|Watson|7,067|87,200|8.10%|
|NSW|McMahon|6,703|89,555|7.48%|
|NSW|Fowler|6,629|88,417|7.50%|
|NSW|Barton|5,629|90,875|6.19%|
|NSW|Werriwa|5,257|91,662|5.74%|
|NSW|Chifley|5,038|88,789|5.67%|
|NSW|Banks|4,968|92,859|5.35%|
|NSW|Parramatta|4,599|87,315|5.27%|
|NSW|Lindsay|4,443|97,434|4.56%|
|NSW|Cook|4,105|93,322|4.40%|
|NSW|Macarthur|4,058|92,622|4.38%|
|NSW|Greenway|4,002|93,556|4.28%|
|NSW|Kingsford Smith|3,969|94,825|4.19%|
|NSW|Reid|3,868|92,958|4.16%|
|NSW|Dobell|3,563|98,944|3.60%|
|NSW|Hume|3,480|97,350|3.57%|
|NSW|Hughes|3,393|95,574|3.55%|
|NSW|Cowper|3,382|105,049|3.22%|
|NSW|Bennelong|3,360|94,573|3.55%|
|NSW|Riverina|3,227|99,749|3.24%|
|NSW|Calare|3,207|101,908|3.15%|
|NSW|Mackellar|3,073|96,196|3.19%|
|NSW|Farrer|2,984|98,050|3.04%|
|NSW|Whitlam|2,974|100,060|2.97%|
|NSW|Paterson|2,936|102,146|2.87%|
|NSW|Mitchell|2,848|93,531|3.04%|
|NSW|Hunter|2,830|101,470|2.79%|
|NSW|Robertson|2,783|97,333|2.86%|
|NSW|Eden-Monaro|2,778|97,685|2.84%|
|NSW|Lyne|2,777|101,063|2.75%|
|NSW|Shortland|2,728|99,696|2.74%|
|VIC|Calwell|2,694|90,982|2.96%|
|NSW|Newcastle|2,678|101,094|2.65%|
|NSW|Berowra|2,653|96,129|2.76%|
|NSW|Cunningham|2,622|98,289|2.67%|
|VIC|Scullin|2,555|96,802|2.64%|
|NSW|Macquarie|2,496|95,408|2.62%|
|NSW|Grayndler|2,323|92,786|2.50%|
|SA|Port Adelaide|2,296|98,910|2.32%|
|NSW|Wentworth|2,200|86,997|2.53%|
|NSW|Page|2,177|105,314|2.07%|
|NSW|Parkes|2,159|94,716|2.28%|
|NSW|Gilmore|2,126|103,993|2.04%|
|NSW|Sydney|2,064|92,677|2.23%|
|NSW|Warringah|1,999|90,669|2.20%|
|WA|Cowan|1,965|83,895|2.34%|
|VIC|Maribyrnong|1,957|94,811|2.06%|
|VIC|McEwen|1,930|116,948|1.65%|
|NSW|Bradfield|1,848|95,426|1.94%|
|VIC|Gorton|1,806|99,397|1.82%|
|NSW|North Sydney|1,773|95,479|1.86%|
|VIC|Wills|1,768|96,962|1.82%|
|VIC|Hotham|1,767|87,677|2.02%|
|SA|Barker|1,759|95,669|1.84%|
|SA|Wakefield|1,751|98,476|1.78%|
|SA|Hindmarsh|1,750|100,105|1.75%|
|NSW|Richmond|1,748|98,660|1.77%|
|VIC|Holt|1,747|101,061|1.73%|
|VIC|Batman|1,730|94,648|1.83%|
|WA|Stirling|1,724|84,731|2.03%|
|VIC|Lalor|1,643|107,821|1.52%|
|SA|Grey|1,632|90,793|1.80%|
|NSW|New England|1,613|98,825|1.63%|
|VIC|Gellibrand|1,602|92,824|1.73%|
|SA|Makin|1,572|96,111|1.64%|
|QLD|Hinkler|1,551|89,554|1.73%|
|VIC|Bruce|1,498|83,947|1.78%|
|SA|Sturt|1,486|94,640|1.57%|
|WA|Perth|1,481|85,562|1.73%|
|WA|Burt|1,477|86,071|1.72%|
|VIC|Isaacs|1,467|93,032|1.58%|
|QLD|Maranoa|1,435|92,990|1.54%|
|QLD|Wright|1,426|89,811|1.59%|
|WA|Fremantle|1,422|86,272|1.65%|
|QLD|Fadden|1,410|89,730|1.57%|
|QLD|Petrie|1,397|93,917|1.49%|
|QLD|Rankin|1,376|87,211|1.58%|
|WA|Forrest|1,367|87,433|1.56%|
|SA|Adelaide|1,354|97,375|1.39%|
|VIC|Murray|1,347|91,912|1.47%|
|QLD|Herbert|1,343|91,913|1.46%|
|ACT|Canberra|1,340|131,653|1.02%|
|SA|Kingston|1,336|95,095|1.40%|
|VIC|Corio|1,312|95,512|1.37%|
|QLD|Oxley|1,309|84,412|1.55%|
|WA|O'Connor|1,283|88,046|1.46%|
|QLD|Longman|1,268|92,881|1.37%|
|VIC|Menzies|1,264|89,299|1.42%|
|SA|Boothby|1,264|98,242|1.29%|
|WA|Canning|1,250|86,088|1.45%|
|QLD|Bowman|1,241|92,954|1.34%|
|VIC|Aston|1,240|86,266|1.44%|
|WA|Hasluck|1,238|83,211|1.49%|
|WA|Pearce|1,232|89,503|1.38%|
|VIC|McMillan|1,230|103,623|1.19%|
|QLD|Bonner|1,227|90,759|1.35%|
|QLD|Blair|1,216|87,784|1.39%|
|QLD|Forde|1,209|86,059|1.40%|
|QLD|McPherson|1,199|89,343|1.34%|
|QLD|Wide Bay|1,194|91,228|1.31%|
|QLD|Moncrieff|1,187|86,929|1.37%|
|WA|Brand|1,179|83,309|1.42%|
|WA|Swan|1,176|83,100|1.42%|
|QLD|Kennedy|1,175|86,698|1.36%|
|ACT|Fenner|1,156|123,114|0.94%|
|VIC|Chisholm|1,145|87,447|1.31%|
|WA|Durack|1,131|78,248|1.45%|
|WA|Tangney|1,124|84,692|1.33%|
|VIC|Ballarat|1,104|99,124|1.11%|
|QLD|Leichhardt|1,093|92,437|1.18%|
|WA|Moore|1,088|89,071|1.22%|
|VIC|Goldstein|1,080|94,266|1.15%|
|VIC|Wannon|1,076|88,716|1.21%|
|VIC|Gippsland|1,070|91,696|1.17%|
|VIC|Bendigo|1,069|98,858|1.08%|
|VIC|Higgins|1,068|92,981|1.15%|
|VIC|Flinders|1,061|102,326|1.04%|
|QLD|Fisher|1,048|86,931|1.21%|
|QLD|Groom|1,035|92,084|1.12%|
|QLD|Flynn|1,024|90,639|1.13%|
|VIC|Dunkley|1,021|92,801|1.10%|
|VIC|Mallee|990|87,316|1.13%|
|QLD|Moreton|976|86,424|1.13%|
|QLD|Dickson|974|91,690|1.06%|
|QLD|Lilley|973|95,588|1.02%|
|VIC|La Trobe|971|95,866|1.01%|
|QLD|Fairfax|962|95,601|1.01%|
|VIC|Indi|952|93,028|1.02%|
|VIC|Casey|949|92,985|1.02%|
|VIC|Jagajaga|946|92,862|1.02%|
|WA|Curtin|943|86,950|1.08%|
|VIC|Melbourne|931|96,863|0.96%|
|QLD|Dawson|924|91,665|1.01%|
|QLD|Capricornia|908|89,065|1.02%|
|VIC|Deakin|907|90,788|1.00%|
|QLD|Griffith|903|94,267|0.96%|
|SA|Mayo|891|95,749|0.93%|
|VIC|Corangamite|887|101,208|0.88%|
|VIC|Melbourne Ports|882|87,080|1.01%|
|QLD|Ryan|876|95,859|0.91%|
|QLD|Brisbane|871|96,743|0.90%|
|NT|Solomon|744|57,000|1.31%|
|VIC|Kooyong|720|90,502|0.80%|
|NT|Lingiari|642|45,027|1.43%|
|TAS|Braddon|542|65,724|0.82%|
|TAS|Lyons|520|69,549|0.75%|
|TAS|Bass|506|66,777|0.76%|
|TAS|Franklin|352|70,216|0.50%|
|TAS|Denison|329|66,893|0.49%|
|**Total**|****|**290,758**|**13,838,900**|**2.10%**|
