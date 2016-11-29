---
layout: page
title: Saved ballots
---

Sections [269](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s269.html) and 
[268A](http://www.austlii.edu.au/au/legis/cth/consol_act/cea1918233/s268a.html) of the Electoral Act outline savings 
provisions. Under these rules, ballots can still be formal if they do not follow instructions given to voters, or if 
they contain other errors. These tables display the number of ballots that were "saved" by these provisions.

SenateDB determines whether to count a ballot in these tables in the [`CountSavedBallots` class](https://github.com/tmccarthy/SenateDB/blob/master/src/main/scala/au/id/tmm/senatedb/tallies/CountSavedBallots.scala).
A ballot is included in this count if it meets the following conditions:

* It numbers between 1 and 5 squares above the line (voters were told to number at least 6), or
* It numbers between 6 and 11 squares below the line (voters were told to number at least 12), or
* It contains a counting error above the line after the first preference, or
* It contains a counting error below the line after the sixth preference, or
* It uses a tick or a cross to express a first preference either above or below the line.

* auto-gen TOC:
{:toc}

### National total

| |Saved ballots|Total formal ballots|%|
|---|---|---|---|
|**Total**|**1,046,837**|**13,838,900**|**7.56%**|

### Savings provision usage

Note that the total number of saved ballots is less than the total number of ballots saved by each provision, since many
ballots were saved by more than one provision.

|Savings provision|Ballots saved by provision|%|
|---|---|---|
|Insufficient squares numbered above-the-line|913,730|6.60%|
|Counting error above-the-line|442,132|3.19%|
|Insufficient squares numbered below-the-line|66,895|0.48%|
|Counting error below-the-line|44,915|0.32%|
|Used tick for first preference|28,247|0.20%|
|Used cross for first preference|23,415|0.17%|
|**Total**|**1,046,837**|**7.56%**|

### Nationally by first-preferenced party

|Party|Saved ballots|Total formal ballots for party|%|
|---|---|---|---|
|Australian Labor Party|356,342|4,123,084|8.64%|
|Liberal Party of Australia|313,450|4,821,314|6.50%|
|The Greens|52,362|1,197,657|4.37%|
|Pauline Hanson's One Nation|48,659|593,013|8.21%|
|Liberal Democratic Party|34,735|298,915|11.62%|
|Nick Xenophon Team|24,572|456,369|5.38%|
|Derryn Hinch's Justice Party|22,070|266,607|8.28%|
|Shooters, Fishers and Farmers|19,239|192,923|9.97%|
|Family First Party|17,439|191,112|9.13%|
|Democratic Labour Party|14,103|94,510|14.92%|
|Christian Democratic Party (Fred Nile Group)|13,054|162,155|8.05%|
|Animal Justice Party|12,739|159,373|7.99%|
|Health Australia Party|10,438|85,233|12.25%|
|Australian Sex Party|8,629|97,882|8.82%|
|Australian Liberty Alliance|8,215|102,982|7.98%|
|Drug Law Reform|6,438|61,327|10.50%|
|Australian Sex Party/Marijuana (HEMP) Party|6,210|69,247|8.97%|
|Jacqui Lambie Network|5,194|69,074|7.52%|
|Australian Christians|4,646|66,525|6.98%|
|Australian Motoring Enthusiast Party|4,473|53,232|8.40%|
|Katter's Australian Party|4,160|53,199|7.82%|
|Marijuana (HEMP) Party|3,960|33,387|11.86%|
|Independent|3,734|23,437|15.93%|
|Rise Up Australia Party|3,394|36,424|9.32%|
|Pirate Party Australia|3,258|35,184|9.26%|
|The Arts Party|3,190|37,702|8.46%|
|Marriage Equality|3,159|44,982|7.02%|
|The Nationals|3,060|46,932|6.52%|
|Seniors United Party of Australia|2,768|22,213|12.46%|
|Australian Cyclists Party|2,609|25,438|10.26%|
|Palmer United Party|2,578|26,210|9.84%|
|Glenn Lazarus Team|2,427|45,149|5.38%|
|Mature Australia|2,342|19,354|12.10%|
|VOTEFLUX.ORG &#124; Upgrade Democracy!|2,340|21,151|11.06%|
|Online Direct Democracy - (Empowering the People!)|2,151|12,018|17.90%|
|Voluntary Euthanasia Party|2,022|23,252|8.70%|
|Science Party / Cyclists Party|1,995|24,673|8.09%|
|Renewable Energy Party|1,977|29,983|6.59%|
|Sustainable Australia|1,910|26,341|7.25%|
|Citizens Electoral Council of Australia|1,387|9,850|14.08%|
|Australian Country Party|1,345|9,316|14.44%|
|Socialist Alliance|1,266|9,968|12.70%|
|Secular Party of Australia|1,204|11,077|10.87%|
|Veterans Party|1,146|10,391|11.03%|
|Socialist Equality Party|1,126|7,865|14.32%|
|Australian Progressives|785|6,251|12.56%|
|CountryMinded|642|5,989|10.72%|
|Science Party|599|5,405|11.08%|
|MFP|453|5,268|8.60%|
|Non-Custodial Parents Party (Equal Parenting)|338|2,102|16.08%|
|Australia First Party|303|3,005|10.08%|
|Australian Recreational Fishers Party|147|2,376|6.19%|
|Australian Antipaedophile Party|55|474|11.60%|
|**Total**|**1,046,837**|**13,838,900**|**7.56%**|

### By state

|State|Saved ballots|Total formal ballots for party|%|
|---|---|---|---|
|NSW|437,158|4,492,197|9.73%|
|VIC|245,082|3,500,237|7.00%|
|QLD|169,958|2,723,166|6.24%|
|WA|90,596|1,366,182|6.63%|
|SA|68,476|1,061,165|6.45%|
|TAS|19,512|339,159|5.75%|
|ACT|9,635|254,767|3.78%|
|NT|6,420|102,027|6.29%|
|**Total**|**1,046,837**|**13,838,900**|**7.56%**|

### By group in the Australian Capital Territory

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|F (Liberal Party of Australia)|3,490|84,615|4.12%|
|C (Australian Labor Party)|3,466|96,667|3.59%|
|H (The Greens)|849|41,006|2.07%|
|A (Liberal Democratic Party)|690|7,460|9.25%|
|J (Australian Sex Party)|475|10,096|4.70%|
|G (Animal Justice Party)|189|4,251|4.45%|
|D (Rise Up Australia Party)|131|2,523|5.19%|
|E (Sustainable Australia)|122|2,678|4.56%|
|I (Christian Democratic Party (Fred Nile Group))|117|3,087|3.79%|
|B (Secular Party of Australia)|76|1,378|5.52%|
|UG (Ungrouped)|30|1,006|2.98%|
|**Total**|**9,635**|**254,767**|**3.78%**|

### By group in New South Wales

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|N (Australian Labor Party)|157,821|1,405,088|11.23%|
|F (Liberal & Nationals)|135,595|1,610,626|8.42%|
|S (Pauline Hanson's One Nation)|19,322|184,012|10.50%|
|D (Liberal Democratic Party)|19,295|139,007|13.88%|
|AL (The Greens)|16,399|332,860|4.93%|
|J (Shooters, Fishers and Farmers)|10,342|88,837|11.64%|
|AF (Christian Democratic Party (Fred Nile Group))|9,564|121,379|7.88%|
|H (Democratic Labour Party)|8,609|51,510|16.71%|
|A (Health Australia Party)|7,766|53,154|14.61%|
|C (Family First Party)|7,140|53,027|13.46%|
|AI (Nick Xenophon Team)|4,220|80,111|5.27%|
|AG (Australian Sex Party)|3,716|30,038|12.37%|
|AO (Marijuana (HEMP) Party)|3,581|29,510|12.13%|
|AB (Animal Justice Party)|3,366|37,991|8.86%|
|B (Seniors United Party of Australia)|2,768|22,213|12.46%|
|P (Derryn Hinch's Justice Party)|2,523|26,720|9.44%|
|AJ (Drug Law Reform)|2,520|20,883|12.07%|
|AM (Australian Liberty Alliance)|2,421|29,795|8.13%|
|AA (Australian Motoring Enthusiast Party)|1,802|16,356|11.02%|
|E (VOTEFLUX.ORG &#124; Upgrade Democracy!)|1,606|12,578|12.77%|
|Q (Jacqui Lambie Network)|1,552|16,502|9.40%|
|O (Online Direct Democracy - (Empowering the People!))|1,497|6,353|23.56%|
|I (Science Party / Cyclists Party)|1,480|18,367|8.06%|
|K (Voluntary Euthanasia Party)|1,406|15,198|9.25%|
|G (Independent)|1,186|3,871|30.64%|
|R (Pirate Party Australia)|1,133|11,418|9.92%|
|M (Rise Up Australia Party)|1,001|7,538|13.28%|
|AC (The Arts Party)|960|11,805|8.13%|
|T (Veterans Party)|819|5,857|13.98%|
|L (Socialist Alliance)|728|5,382|13.53%|
|AN (Renewable Energy Party)|567|8,936|6.35%|
|AK (Sustainable Australia)|547|7,723|7.08%|
|X (Katter's Australian Party)|503|4,316|11.65%|
|W (Socialist Equality Party)|466|2,933|15.89%|
|V (CountryMinded)|432|3,153|13.70%|
|AE (Mature Australia)|430|2,805|15.33%|
|Y (Palmer United Party)|429|2,805|15.29%|
|U (Secular Party of Australia)|371|2,773|13.38%|
|Z (Citizens Electoral Council of Australia)|352|1,895|18.58%|
|AD (Non-Custodial Parents Party (Equal Parenting))|338|2,102|16.08%|
|AH (Australian Progressives)|305|1,817|16.79%|
|UG (Ungrouped)|280|2,953|9.48%|
|**Total**|**437,158**|**4,492,197**|**9.73%**|

### By group in the Northern Territory

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|F (Australian Labor Party (Northern Territory) Branch)|2,266|38,197|5.93%|
|E (Country Liberals (NT))|1,845|37,156|4.97%|
|A (Rise Up Australia Party)|905|6,768|13.37%|
|D (The Greens)|562|11,003|5.11%|
|B (Australian Sex Party/Marijuana (HEMP) Party)|451|4,956|9.10%|
|C (Citizens Electoral Council of Australia)|186|1,255|14.82%|
|G (Christian Democratic Party (Fred Nile Group))|122|1,660|7.35%|
|UG (Ungrouped)|83|1,032|8.04%|
|**Total**|**6,420**|**102,027**|**6.29%**|

### By group in Queensland

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|G (Liberal National Party of Queensland)|50,906|960,467|5.30%|
|D (Australian Labor Party)|47,180|717,524|6.58%|
|X (Pauline Hanson's One Nation)|17,840|250,126|7.13%|
|AK (The Greens)|7,850|188,323|4.17%|
|E (Liberal Democratic Party)|6,791|77,601|8.75%|
|I (Katter's Australian Party)|3,650|48,807|7.48%|
|T (Family First Party)|3,503|52,453|6.68%|
|V (Australian Sex Party/Marijuana (HEMP) Party)|2,712|30,157|8.99%|
|Q (Shooters, Fishers and Farmers)|2,709|29,571|9.16%|
|L (Nick Xenophon Team)|2,576|55,653|4.63%|
|H (Animal Justice Party)|2,551|32,306|7.90%|
|AC (Glenn Lazarus Team)|2,427|45,149|5.38%|
|A (Australian Cyclists Party)|2,145|19,933|10.76%|
|S (Democratic Labour Party)|1,808|15,443|11.71%|
|J (Marriage Equality)|1,780|23,811|7.48%|
|N (Australian Liberty Alliance)|1,720|29,392|5.85%|
|AG (Drug Law Reform)|1,628|17,060|9.54%|
|B (The Arts Party)|960|11,030|8.70%|
|O (Derryn Hinch's Justice Party)|892|14,256|6.26%|
|M (Pirate Party Australia)|822|10,342|7.95%|
|AF (Australian Christians)|695|9,686|7.18%|
|AH (Health Australia Party)|694|10,147|6.84%|
|F (Online Direct Democracy - (Empowering the People!))|650|5,504|11.81%|
|AD (Jacqui Lambie Network)|585|9,138|6.40%|
|AA (Christian Democratic Party (Fred Nile Group))|508|7,314|6.95%|
|K (Mature Australia)|492|5,519|8.91%|
|C (Secular Party of Australia)|462|4,623|9.99%|
|AB (Palmer United Party)|445|4,816|9.24%|
|R (Independent)|389|1,536|25.33%|
|Y (Rise Up Australia Party)|385|5,734|6.71%|
|U (Renewable Energy Party)|370|6,245|5.92%|
|AL (Sustainable Australia)|344|5,366|6.41%|
|AJ (Veterans Party)|327|4,534|7.21%|
|UG (Ungrouped)|282|4,154|6.79%|
|AI (CountryMinded)|210|2,836|7.40%|
|P (Citizens Electoral Council of Australia)|190|1,877|10.12%|
|Z (Socialist Equality Party)|177|1,639|10.80%|
|W (VOTEFLUX.ORG &#124; Upgrade Democracy!)|177|1,881|9.41%|
|AE (Australian Progressives)|126|1,213|10.39%|
|**Total**|**169,958**|**2,723,166**|**6.24%**|

### By group in South Australia

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|B (Australian Labor Party)|21,423|289,902|7.39%|
|H (Liberal Party of Australia)|20,541|345,767|5.94%|
|F (Nick Xenophon Team)|13,079|230,703|5.67%|
|D (The Greens)|2,686|62,329|4.31%|
|O (Pauline Hanson's One Nation)|2,242|31,621|7.09%|
|N (Family First Party)|2,185|30,464|7.17%|
|R (Australian Sex Party/Marijuana (HEMP) Party)|1,135|12,091|9.39%|
|K (Liberal Democratic Party)|895|6,913|12.95%|
|A (Mature Australia)|749|4,440|16.87%|
|Q (Shooters, Fishers and Farmers)|613|7,815|7.84%|
|U (Animal Justice Party)|547|8,981|6.09%|
|J (Australian Motoring Enthusiast Party)|443|5,091|8.70%|
|S (Australian Liberty Alliance)|294|4,435|6.63%|
|P (Marriage Equality)|267|4,032|6.62%|
|C (The Arts Party)|255|3,368|7.57%|
|M (Christian Democratic Party (Fred Nile Group))|214|2,799|7.65%|
|E (Australian Cyclists Party)|182|1,664|10.94%|
|T (Derryn Hinch's Justice Party)|155|2,359|6.57%|
|V (Voluntary Euthanasia Party)|133|2,286|5.82%|
|I (Palmer United Party)|125|778|16.07%|
|G (Australian Progressives)|115|1,157|9.94%|
|UG (Ungrouped)|83|851|9.75%|
|L (VOTEFLUX.ORG &#124; Upgrade Democracy!)|62|820|7.56%|
|W (Citizens Electoral Council of Australia)|53|499|10.62%|
|**Total**|**68,476**|**1,061,165**|**6.45%**|

### By group in Tasmania

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|B (Australian Labor Party)|7,063|113,935|6.20%|
|F (Liberal Party of Australia)|5,751|110,318|5.21%|
|C (The Greens)|1,559|37,840|4.12%|
|M (Jacqui Lambie Network)|1,558|28,146|5.54%|
|A (Family First Party)|697|6,692|10.42%|
|I (Pauline Hanson's One Nation)|532|8,700|6.11%|
|H (Australian Sex Party/Marijuana (HEMP) Party)|412|4,493|9.17%|
|P (Shooters, Fishers and Farmers)|387|4,688|8.26%|
|E (Nick Xenophon Team)|224|5,128|4.37%|
|G (Palmer United Party)|212|2,363|8.97%|
|Q (Animal Justice Party)|154|2,377|6.48%|
|T (Liberal Democratic Party)|150|1,662|9.03%|
|S (Australian Recreational Fishers Party)|147|2,376|6.19%|
|D (Christian Democratic Party (Fred Nile Group))|137|2,861|4.79%|
|N (Australian Liberty Alliance)|104|1,112|9.35%|
|J (Derryn Hinch's Justice Party)|95|1,473|6.45%|
|L (Renewable Energy Party)|82|1,340|6.12%|
|R (Science Party)|78|1,306|5.97%|
|O (VOTEFLUX.ORG &#124; Upgrade Democracy!)|63|946|6.66%|
|U (The Arts Party)|51|728|7.01%|
|UG (Ungrouped)|31|498|6.22%|
|K (Citizens Electoral Council of Australia)|25|177|14.12%|
|**Total**|**19,512**|**339,159**|**5.75%**|

### By group in Victoria

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|D (Australian Labor Party)|88,365|1,075,658|8.21%|
|AF (Liberal & Nationals)|64,725|1,158,800|5.59%|
|A (Derryn Hinch's Justice Party)|17,653|211,733|8.34%|
|AK (The Greens)|15,903|380,499|4.18%|
|AH (Liberal Democratic Party)|5,844|55,501|10.53%|
|C (Animal Justice Party)|4,998|60,780|8.22%|
|Y (Pauline Hanson's One Nation)|4,826|63,528|7.60%|
|AL (Australian Sex Party)|4,077|54,128|7.53%|
|O (Family First Party)|3,227|39,747|8.12%|
|AG (Shooters, Fishers and Farmers)|3,146|36,669|8.58%|
|V (Nick Xenophon Team)|2,917|55,118|5.29%|
|H (Australian Christians)|2,738|34,763|7.88%|
|R (Democratic Labour Party)|2,495|18,152|13.75%|
|U (Australian Liberty Alliance)|2,404|23,080|10.42%|
|AC (Drug Law Reform)|2,290|23,384|9.79%|
|W (Australian Motoring Enthusiast Party)|2,228|31,785|7.01%|
|L (Health Australia Party)|1,616|17,169|9.41%|
|G (Jacqui Lambie Network)|1,499|15,288|9.81%|
|AA (Australian Country Party)|1,345|9,316|14.44%|
|J (Pirate Party Australia)|1,303|13,424|9.71%|
|E (Science Party / Cyclists Party)|1,134|11,567|9.80%|
|X (Marriage Equality)|1,112|17,139|6.49%|
|F (Palmer United Party)|948|10,456|9.07%|
|B (Independent)|935|3,386|27.61%|
|I (Sustainable Australia)|897|10,574|8.48%|
|P (Christian Democratic Party (Fred Nile Group))|873|9,287|9.40%|
|AI (Rise Up Australia Party)|720|10,166|7.08%|
|M (Renewable Energy Party)|690|8,845|7.80%|
|Q (The Arts Party)|678|7,737|8.76%|
|AD (Voluntary Euthanasia Party)|483|5,768|8.37%|
|K (Socialist Equality Party)|483|3,293|14.67%|
|AB (MFP)|453|5,268|8.60%|
|AE (Mature Australia)|372|3,469|10.72%|
|N (VOTEFLUX.ORG &#124; Upgrade Democracy!)|327|2,838|11.52%|
|S (Citizens Electoral Council of Australia)|314|2,098|14.97%|
|Z (Socialist Alliance)|299|2,597|11.51%|
|T (Secular Party of Australia)|295|2,303|12.81%|
|AJ (Australian Progressives)|239|2,064|11.58%|
|UG (Ungrouped)|231|2,860|8.08%|
|**Total**|**245,082**|**3,500,237**|**7.00%**|

### By group in Western Australia

|Group|Saved ballots|Total formal ballots for group|%|
|---|---|---|---|
|X (Liberal Party of Australia)|31,628|525,879|6.01%|
|D (Australian Labor Party)|28,758|386,113|7.45%|
|J (The Greens (WA))|6,554|143,797|4.56%|
|R (Pauline Hanson's One Nation)|3,897|55,026|7.08%|
|S (Australian Sex Party/Marijuana (HEMP) Party)|2,240|25,047|8.94%|
|B (Shooters, Fishers and Farmers)|2,042|25,343|8.06%|
|F (The Nationals)|2,029|34,618|5.86%|
|C (Nick Xenophon Team)|1,556|29,656|5.25%|
|A (Christian Democratic Party (Fred Nile Group))|1,519|13,768|11.03%|
|P (Australian Liberty Alliance)|1,272|15,168|8.39%|
|W (Australian Christians)|1,213|22,076|5.49%|
|T (Democratic Labour Party)|1,191|9,405|12.66%|
|Z (Liberal Democratic Party)|1,070|10,771|9.93%|
|K (Animal Justice Party)|934|12,687|7.36%|
|H (Derryn Hinch's Justice Party)|752|10,066|7.47%|
|AB (Family First Party)|687|8,729|7.87%|
|I (Palmer United Party)|419|4,992|8.39%|
|U (Health Australia Party)|362|4,763|7.60%|
|Y (Australia First Party)|303|3,005|10.08%|
|M (The Arts Party)|286|3,034|9.43%|
|L (Mature Australia)|280|2,687|10.42%|
|O (Renewable Energy Party)|268|4,617|5.80%|
|E (Citizens Electoral Council of Australia)|267|2,049|13.03%|
|Q (Rise Up Australia Party)|252|3,695|6.82%|
|G (Socialist Alliance)|239|1,989|12.02%|
|N (Australian Cyclists Party)|184|2,679|6.87%|
|V (Independent)|172|949|18.12%|
|UG (Ungrouped)|133|2,184|6.09%|
|AA (VOTEFLUX.ORG &#124; Upgrade Democracy!)|89|1,390|6.40%|
|**Total**|**90,596**|**1,366,182**|**6.63%**|

### By division

|State|Division|Saved ballots|Total formal ballots for division|%|
|---|---|---|---|---|
|NSW|Blaxland|15,995|83,969|19.05%|
|NSW|Fowler|15,827|88,417|17.90%|
|NSW|McMahon|14,581|89,555|16.28%|
|NSW|Watson|14,168|87,200|16.25%|
|NSW|Chifley|13,039|88,789|14.69%|
|NSW|Werriwa|12,972|91,662|14.15%|
|NSW|Barton|11,998|90,875|13.20%|
|NSW|Macarthur|11,199|92,622|12.09%|
|NSW|Lindsay|10,684|97,434|10.97%|
|NSW|Parramatta|10,658|87,315|12.21%|
|VIC|Calwell|10,614|90,982|11.67%|
|NSW|Banks|10,564|92,859|11.38%|
|NSW|Kingsford Smith|10,441|94,825|11.01%|
|NSW|Cook|10,319|93,322|11.06%|
|NSW|Greenway|9,895|93,556|10.58%|
|NSW|Dobell|9,751|98,944|9.86%|
|NSW|Reid|9,713|92,958|10.45%|
|VIC|Scullin|9,702|96,802|10.02%|
|NSW|Cowper|9,519|105,049|9.06%|
|NSW|Farrer|9,330|98,050|9.52%|
|NSW|Riverina|9,261|99,749|9.28%|
|NSW|Calare|9,241|101,908|9.07%|
|NSW|Hughes|9,096|95,574|9.52%|
|NSW|Hunter|9,078|101,470|8.95%|
|NSW|Parkes|9,025|94,716|9.53%|
|NSW|Whitlam|8,992|100,060|8.99%|
|NSW|Paterson|8,967|102,146|8.78%|
|VIC|Holt|8,876|101,061|8.78%|
|NSW|Hume|8,845|97,350|9.09%|
|NSW|Shortland|8,727|99,696|8.75%|
|VIC|Lalor|8,681|107,821|8.05%|
|NSW|Lyne|8,479|101,063|8.39%|
|VIC|McEwen|8,271|116,948|7.07%|
|NSW|Newcastle|8,258|101,094|8.17%|
|NSW|Eden-Monaro|8,179|97,685|8.37%|
|VIC|Gorton|8,178|99,397|8.23%|
|SA|Port Adelaide|8,166|98,910|8.26%|
|NSW|Gilmore|8,134|103,993|7.82%|
|NSW|Cunningham|8,131|98,289|8.27%|
|NSW|Bennelong|8,098|94,573|8.56%|
|NSW|Robertson|8,056|97,333|8.28%|
|NSW|Page|8,044|105,314|7.64%|
|NSW|Mackellar|7,968|96,196|8.28%|
|VIC|Maribyrnong|7,954|94,811|8.39%|
|NSW|Mitchell|7,588|93,531|8.11%|
|SA|Barker|7,530|95,669|7.87%|
|VIC|Wills|7,468|96,962|7.70%|
|VIC|Wannon|7,337|88,716|8.27%|
|VIC|Gellibrand|7,224|92,824|7.78%|
|VIC|Murray|7,219|91,912|7.85%|
|NSW|Macquarie|7,192|95,408|7.54%|
|NSW|New England|7,172|98,825|7.26%|
|VIC|Batman|7,116|94,648|7.52%|
|NSW|Berowra|7,103|96,129|7.39%|
|NSW|Richmond|7,028|98,660|7.12%|
|VIC|Mallee|6,995|87,316|8.01%|
|VIC|McMillan|6,975|103,623|6.73%|
|SA|Wakefield|6,944|98,476|7.05%|
|QLD|Kennedy|6,907|86,698|7.97%|
|VIC|Hotham|6,796|87,677|7.75%|
|VIC|Isaacs|6,778|93,032|7.29%|
|NSW|Grayndler|6,748|92,786|7.27%|
|SA|Grey|6,633|90,793|7.31%|
|QLD|Hinkler|6,606|89,554|7.38%|
|VIC|Corio|6,605|95,512|6.92%|
|WA|Cowan|6,553|83,895|7.81%|
|VIC|Gippsland|6,529|91,696|7.12%|
|VIC|Flinders|6,503|102,326|6.36%|
|WA|Burt|6,456|86,071|7.50%|
|QLD|Rankin|6,443|87,211|7.39%|
|VIC|Ballarat|6,395|99,124|6.45%|
|QLD|McPherson|6,388|89,343|7.15%|
|VIC|Bruce|6,351|83,947|7.57%|
|VIC|Bendigo|6,302|98,858|6.37%|
|SA|Hindmarsh|6,255|100,105|6.25%|
|SA|Kingston|6,211|95,095|6.53%|
|WA|Pearce|6,186|89,503|6.91%|
|QLD|Bowman|6,180|92,954|6.65%|
|QLD|Maranoa|6,177|92,990|6.64%|
|NSW|Sydney|6,167|92,677|6.65%|
|SA|Makin|6,156|96,111|6.41%|
|QLD|Longman|6,139|92,881|6.61%|
|QLD|Fadden|6,093|89,730|6.79%|
|QLD|Leichhardt|6,083|92,437|6.58%|
|WA|Fremantle|6,048|86,272|7.01%|
|WA|Perth|6,044|85,562|7.06%|
|WA|Canning|6,024|86,088|7.00%|
|VIC|Jagajaga|6,016|92,862|6.48%|
|WA|Stirling|5,989|84,731|7.07%|
|QLD|Petrie|5,974|93,917|6.36%|
|QLD|Herbert|5,973|91,913|6.50%|
|NSW|Wentworth|5,931|86,997|6.82%|
|VIC|Dunkley|5,910|92,801|6.37%|
|VIC|Indi|5,908|93,028|6.35%|
|QLD|Lilley|5,864|95,588|6.13%|
|NSW|Warringah|5,849|90,669|6.45%|
|QLD|Moncrieff|5,839|86,929|6.72%|
|QLD|Wright|5,829|89,811|6.49%|
|QLD|Wide Bay|5,826|91,228|6.39%|
|QLD|Blair|5,782|87,784|6.59%|
|VIC|Aston|5,756|86,266|6.67%|
|VIC|Corangamite|5,752|101,208|5.68%|
|QLD|Forde|5,743|86,059|6.67%|
|QLD|Oxley|5,737|84,412|6.80%|
|VIC|Casey|5,715|92,985|6.15%|
|NSW|Bradfield|5,715|95,426|5.99%|
|SA|Sturt|5,697|94,640|6.02%|
|QLD|Groom|5,695|92,084|6.18%|
|WA|O'Connor|5,641|88,046|6.41%|
|WA|Brand|5,634|83,309|6.76%|
|WA|Hasluck|5,624|83,211|6.76%|
|VIC|La Trobe|5,621|95,866|5.86%|
|WA|Forrest|5,598|87,433|6.40%|
|QLD|Dawson|5,597|91,665|6.11%|
|VIC|Menzies|5,522|89,299|6.18%|
|QLD|Flynn|5,473|90,639|6.04%|
|NSW|North Sydney|5,433|95,479|5.69%|
|QLD|Fairfax|5,396|95,601|5.64%|
|QLD|Capricornia|5,334|89,065|5.99%|
|SA|Boothby|5,263|98,242|5.36%|
|VIC|Goldstein|5,224|94,266|5.54%|
|SA|Adelaide|5,207|97,375|5.35%|
|WA|Tangney|5,201|84,692|6.14%|
|QLD|Fisher|5,170|86,931|5.95%|
|VIC|Chisholm|5,158|87,447|5.90%|
|VIC|Melbourne|5,153|96,863|5.32%|
|WA|Durack|5,085|78,248|6.50%|
|QLD|Bonner|5,084|90,759|5.60%|
|WA|Swan|5,045|83,100|6.07%|
|QLD|Moreton|4,896|86,424|5.67%|
|VIC|Higgins|4,895|92,981|5.26%|
|VIC|Deakin|4,892|90,788|5.39%|
|ACT|Canberra|4,861|131,653|3.69%|
|ACT|Fenner|4,774|123,114|3.88%|
|WA|Curtin|4,735|86,950|5.45%|
|WA|Moore|4,733|89,071|5.31%|
|QLD|Dickson|4,651|91,690|5.07%|
|QLD|Griffith|4,609|94,267|4.89%|
|VIC|Melbourne Ports|4,424|87,080|5.08%|
|SA|Mayo|4,414|95,749|4.61%|
|VIC|Kooyong|4,267|90,502|4.71%|
|TAS|Lyons|4,247|69,549|6.11%|
|QLD|Brisbane|4,244|96,743|4.39%|
|QLD|Ryan|4,226|95,859|4.41%|
|TAS|Braddon|4,173|65,724|6.35%|
|TAS|Franklin|3,867|70,216|5.51%|
|TAS|Bass|3,619|66,777|5.42%|
|TAS|Denison|3,606|66,893|5.39%|
|NT|Lingiari|3,553|45,027|7.89%|
|NT|Solomon|2,867|57,000|5.03%|
|**Total**|****|**1,046,837**|**13,838,900**|**7.56%**|
