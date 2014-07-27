<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                             xmlns:xalan="http://xml.apache.org/xalan"
                             xmlns:auxBean="xalan://com.mebigfatguy.fbaas.fbp.AuxBean">

	<xsl:param name="fbp_name"/>
	<xsl:param name="fbp_jar"/>
	<xsl:param name="fbp_src"/>
	<xsl:param name="fbp_aux"/>
	
	<xsl:template match = "/">

		<xsl:element name="Project">
			<xsl:element name="Jar">
				<xsl:value-of select="$fbp_name"/>
			</xsl:element>
			<xsl:element name="SrcDir">
				<xsl:value-of select="$fbp_src"/>
			</xsl:element>
			<xsl:for-each select="$fbp_aux">
				<xsl:element name="AuxClasspathEntry">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select=""/>
			<xsl:attribute name="projectName"><xsl:value-of select="$fbp_name"/></xsl:attribute>
		</xsl:element>

	</xsl:template>

</xsl:transform>