TEX=rubber -d
TEXs=$(wildcard *.tex)
PDFs=$(TEXs:.tex=.pdf)
default: $(PDFs)
clean:
	rm -f *.blg *.log *.out *.aux *.toc *.bbl *.pdf chapters/*.aux
keeppdf:
	rm -f *.blg *.log *.out *.aux *.toc *.bbl chapters/*.aux
%.pdf: %.tex
	$(TEX) $<
