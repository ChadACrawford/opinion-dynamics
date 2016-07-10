set print "-"

set terminal png
#set terminal postscript eps enhanced solid "Helvetica" 1

# print independent
# print infile
# print outfile

set output outfile.".png"

set title "Opinion Density"
set xlabel independent
set ylabel "Opinion value"

set view map
set palette defined (0 "white", 1 "black", 10 "red")
set pointsize 1.2

splot infile using 1:2:3 with points pointtype 5 palette notitle