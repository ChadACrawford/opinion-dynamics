set terminal png
#set terminal postscript eps enhanced solid "Helvetica" 1

set output outfile.".png"

set title "Number of Clusters"
set xlabel independent
set ylabel "Number of Clusters"

plot infile using 1:2 with line notitle