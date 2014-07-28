<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                             xmlns:xalan="http://xml.apache.org/xalan"
                             xmlns:auxBean="xalan://com.mebigfatguy.fbaas.fbp.AuxBean"
                             exclude-result-prefixes="xsl xalan" 
                             extension-element-prefixes="auxBean">

	<xsl:param name="fbp_name"/>
	<xsl:param name="fbp_jar"/>
	<xsl:param name="fbp_src"/>
	<xsl:param name="fbp_aux"/>
	<xsl:output indent="yes" method="xml" xalan:indent-amount="4" omit-xml-declaration="yes" />
	<xsl:template match = "/">

		<xsl:element name="Project">
			<xsl:attribute name="projectName"><xsl:value-of select="$fbp_name"/></xsl:attribute>
			<xsl:element name="Jar">
				<xsl:value-of select="$fbp_jar"/>
			</xsl:element>
			<xsl:element name="SrcDir">
				<xsl:value-of select="$fbp_src"/>
			</xsl:element>
			<xsl:for-each select="auxBean:getAuxPaths($fbp_aux)">"
				<xsl:element name="AuxClasspathEntry">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:transform>