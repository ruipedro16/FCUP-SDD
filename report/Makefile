.PHONY: main clean FORCE 

main: report.pdf clean

report.pdf: FORCE
	hindent  sortacronyms.hs
	runghc sortacronyms.hs 
	latexmk -auxdir=tmp -outdir=tmp -pdflatex='texfot lualatex -interaction nonstopmode' -pdf -f report.tex
	mv tmp/report.pdf .

clean:
	rm -rf tmp
