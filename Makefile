.default: dist

dist: LanderDist.zip

clean:
	rm -rf ./bin/

BINFILES := ./bin/net/pbrennan/Lander_2009/*.class
DATAFILES := ./data/Missions.csv ./data/terrain001.csv
DOCFILES := ./doc/Bibliography.odt ./doc/orbital_mechanics.pdf ./Changelog.txt ./README.odt ./README.txt
FONTFILES := ./fonts/*.ttf
IMAGEFILES := ./images/LanderHelpBg.png ./images/LMSprite.png ./images/MoonNPoleProjection2.png
SOUNDFILES := ./sounds/*.wav

ALLFILES:= $(BINFILES) $(DATAFILES) $(DOCFILES) $(FONTFILES) $(IMAGEFILES) $(SOUNDFILES)

MANIFEST := ./MANIFEST.MF

LanderDist.zip : $(ALLFILES)
	zip LanderDist.zip $(ALLFILES)

JAR := jar

Lander_2009.jar : $(ALLFILES) $(MANIFEST)
	$(JAR) -cvfm Lander_2009.jar $(MANIFEST) $(ALLFILES)
