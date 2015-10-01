#for year in 2001 2002 2003 2004 2005 2006 2007 2008 2009 2010 2011
for year in 2011
do  
	for url in `curl -s http://www.forexite.com/free_forex_quotes/forex_history_arhiv_$year.html|grep zip |cut -d= -f6|cut -d'>' -f1|sed s/'"'//g`
	do
		URL="http://www.forexite.com/free_forex_quotes/$url"
		curl $URL -o `echo $url|cut -d/ -f3`
	done
done
