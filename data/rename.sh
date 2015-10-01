#! /bin/sh
for i in *.txt
do
	date="`echo $i|cut -c1-2`"
	month="`echo $i|cut -c3-4`"
	year="`echo $i|cut -c5-6`"
	mv $i proc/$year$month$date.txt
done
