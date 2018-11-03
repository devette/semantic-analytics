<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:afm="http://afm.nl/register/short#">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    <xsl:template match="/">
        <rdf:RDF>
            <xsl:for-each select="register/vermelding">
                <afm:vermelding>
                    <afm:meldingsplichtige><xsl:value-of select="meldingsplichtige/text()"/></afm:meldingsplichtige>
                    <afm:uitgevende-instelling><xsl:value-of select="uitgevende-instelling/text()"/></afm:uitgevende-instelling>
                    <afm:isin><xsl:value-of select="isin/text()"/></afm:isin>
                    <afm:netto-shortpositie><xsl:value-of select="netto-shortpositie/text()"/></afm:netto-shortpositie>
                    <xsl:variable name="datePart" select="substring-before(positiedatum,' ')"/>
                    <xsl:variable name="timePart" select="concat('0',substring-after(positiedatum,' '))"/>
                    <xsl:variable name="day" select="concat('00', substring-before($datePart,'-'))"/>
                    <xsl:variable name="month" select="concat('00', substring-before(substring-after($datePart,'-'), '-'))"/>
                    <xsl:variable name="year" select="substring-after(substring-after($datePart,'-'), '-')"/>
                    <xsl:variable name="day2" select="substring($day, string-length($day)-1, 2)"/>
                    <xsl:variable name="month2" select="substring($month, string-length($month)-1, 2)"/>
                    <xsl:variable name="positieDatumXsdDate" select="concat($year,'-',$month2,'-', $day2,'T', $timePart)"/>
                    <afm:positiejaar><xsl:value-of select="$year"/></afm:positiejaar>
                    <afm:positiemaand><xsl:value-of select="$month2"/></afm:positiemaand>
                    <afm:positiedatum rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"><xsl:value-of select="$positieDatumXsdDate"/></afm:positiedatum>
                    <afm:meldingtekst><xsl:value-of select="vermelding/text()"/></afm:meldingtekst>
                </afm:vermelding>
            </xsl:for-each>
        </rdf:RDF>
    </xsl:template>
</xsl:stylesheet>