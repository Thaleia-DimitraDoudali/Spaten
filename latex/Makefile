.PHONY: FORCE default clean distclean

FILE=thesis

export SHELL=/bin/bash
export TEXINPUTS:=.:./Styles//:${TEXINPUTS}
export BSTINPUTS:=.:./Styles//:${BSTINPUTS}

default: $(FILE).pdf

thesis.pdf: thesis_text.pdf thesis_title.pdf
	pdftk thesis_title.pdf thesis_text.pdf cat output thesis.pdf


thesis_text.pdf: thesis_text.tex FORCE
	latexmk -pdf -f -e '$$pdflatex=q/xelatex %O %S/' $<

thesis_title.pdf: thesis_title.tex FORCE
	latexmk -pdf -f -e '$$pdflatex=q/xelatex %O %S/' $<

clean:
	$(RM) *.{dvi,aux,log,toc,lof,lol,lot,dlog,bbl,blg,idx,out,tpt,svn,backup}
	$(RM) *.{nav,snm,vrb,fls,fdb_latexmk} *~ *.bak

distclean: clean
	$(RM) $(FILE).{dvi,ps,pdf}
