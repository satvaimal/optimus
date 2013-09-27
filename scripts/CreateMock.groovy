includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createMock:"Generate mock for domain class" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of mock classes"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createMock )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << "class ${domainClass.name}Mock {\n\n"
    content << generateMockMethod( domainClass )
    content << '}'
    def directory = generateDirectory( "src/groovy",
        domainClass.packageName )
    def fileName = "${domainClass.name}Mock.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateMockMethod( domainClass ) {

    def initializedAttributes = initializeAttributes( domainClass )
    def className = domainClass.name
    def content = '' << "${TAB}static ${className} mock( id ) {\n\n"
    if ( properties.mockException ) content << generateException()
    content << "${TAB*2}def instance = new ${className}(\n"
    content << initializedAttributes
    content << "${TAB*2})\n"
    content << setUniqueProperties( domainClass.constrainedProperties )
    if ( properties.idAssigned && !properties.idSet ) {
        content << "${TAB*2}instance.id = id\n"
    }// End of if
    properties.remove( 'idAssigned' )
    properties.remove( 'idSet' )
    content << "${TAB*2}instance\n"
    if ( properties.mockException ) content << "*/\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String initializeAttributes( domainClass ) {

    def content = new StringBuilder()
    domainClass.constrainedProperties.each {
        content << "${TAB*3}${it.key}:"
        def attributeValue = getValue( domainClass, it.key,
            it.value.appliedConstraints, it.value.propertyType )
        content << "${attributeValue},\n"
    }// End of closure
    content.toString()

}// End of method

String getValue( domainClass, name, appliedConstraints, type ) {

    def value = getValueNullable( appliedConstraints )
    if ( !value ) value = getValueException( appliedConstraints )
    if ( !value ) value = getValueId( domainClass, name, appliedConstraints, type )
    if ( !value ) value = getValueEmail( appliedConstraints )
    if ( !value ) value = getValueInList( appliedConstraints, type )
    if ( !value ) value = getValueString( appliedConstraints, type )
    if ( !value ) value = getValueBoolean( type )
    if ( !value ) value = getValueNumeric( type )
    if ( !value ) value = getValueArray( type, 1 )
    if ( !value ) value = getValueBigDecimal( type )
    if ( !value ) value = getValueObject( type )
    value

}// End of method

String getValueNullable( appliedConstraints ) {

    def constraint = appliedConstraints.find { it.name == 'nullable' }
    if ( constraint?.parameter == true ) return "null"

}// End of method

String getValueException( appliedConstraints ) {

    if ( !appliedConstraints*.name.disjoint( EXCEPTION_CONSTRAINTS ) ) {
        properties.mockException = true
        return "'SET ME'"
    }// End of if

}// End of method

String getValueId( domainClass, name, appliedConstraints, type ) {

    def idAssigned = getIdAssigned( domainClass )
    if ( !idAssigned ) return null
    properties.idAssigned = true
    if ( idAssigned.name != name ) return null
    properties.idSet = true
    if ( type.name == 'java.lang.String' ) {
        def content = '' << "new String( ( 65 + id ) as Character )"
        def sizeConstraint = appliedConstraints.find { it.name == 'size' }
        if ( sizeConstraint )
            content << " * ${sizeConstraint.parameter.from}"
        return content.toString()
    } else return 'id'

}// End of method

String getValueEmail( appliedConstraints ) {

    def constraint = appliedConstraints.find { it.name == 'email' }
    if ( constraint?.parameter == true ) return "'a@a.com'"

}// End of method

String getValueInList( appliedConstraints, type ) {

    def constraint = appliedConstraints.find { it.name == 'inList' }
    if ( constraint ) {
        def quote = ( type.name == 'java.lang.String' ? "'" : "" )
        return "${quote}${constraint.parameter[0]}${quote}"
    }// End of if

}// End of method

String getValueString( appliedConstraints, type ) {

    if ( type.name != 'java.lang.String' ) return null
    def content = '' << "'A'"
    def sizeConstraint = appliedConstraints.find { it.name == 'size' }
    if ( sizeConstraint )
        content << " * ${sizeConstraint.parameter.from}"
    content.toString()

}// End of method

String getValueBoolean( type ) {

    if ( type.name == 'java.lang.Boolean' || type.name == 'boolean' )
        return "true"

}// End of method

String getValueNumeric( type ) {

    if ( PRIMITIVE_NUMERIC_TYPES.contains( type.name ) ||
        WRAPPER_NUMERIC_TYPES.contains( type.name ) )
        return '1'

}// End of method

String getValueArray( type, value ) {

    if ( !type.name.startsWith( '[' ) ) return null
    def arrayType = getArrayType( type.name )
    "new ${arrayType}[ ${(value as BigDecimal)} ]"

}// End of method

String getValueBigDecimal( type ) {
    if ( type.name == 'java.math.BigDecimal' ) return "new BigDecimal( 0 )"
}// End of method

String getValueObject( type ) {

    if ( !PRIMITIVE_TYPES.contains( type.name ) &&
        !WRAPPER_TYPES.contains( type.name ) )
        return "new ${type.simpleName}()"

}// End of method

String generateException() {

    def content = '' << "${TAB*2}throw new IllegalStateException("
    content << " 'Please set some values by hand' )\n/*\n"
    content.toString()

}// End of method

String setUniqueProperties( constraints ) {

    def uniqueSettings = getUniqueSettings( constraints )
    if ( !uniqueSettings ) return ''
    def content = new StringBuilder('\n')
    uniqueSettings.each {
        def value = getUniqueValue( it.value )
        content << "${TAB*2}instance.${it.key} = ${value}\n"
    }// End of closure
    content.toString()

}// End of method

String getUniqueValue( map ) {

    def uniqueValue = map.min
    if ( map.propertyType == 'java.lang.String' ) {
    def content = '' << "new String( ( 65 + id ) as Character )"
        content << "${uniqueValue?' * ' + uniqueValue:''}"
        return content.toString()
    } else return "${uniqueValue}"

}// End of method
