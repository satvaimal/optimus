includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestConstraints:
  'Generate class domain unit test constraints' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of constraints unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestConstraints )

void generate( domainClass ) {

    def constraints = domainClass.constrainedProperties
    properties.uniqueSettings = getUniqueSettings( constraints )
    properties.requiredAttributes = getRequiredAttributes( constraints )
    constraints.each { processConstraint( domainClass, it.value ) }

}// End of method

void processConstraint( domainClass, constraint ) {

    if ( EXCLUDED_ATTRIBUTES.contains( constraint.propertyName ) ||
        constraint.propertyType.name == 'boolean' ) return
    def testMethods = new StringBuilder()
    constraint.appliedConstraints.each { ac ->
        if ( EXCLUDED_CONSTRAINTS.contains( ac.name ) ) return
        else if ( PRIMITIVE_TYPES.contains( constraint.propertyType ) &&
        ac.name == 'nullable' ) return
        else if ( RANGE_CONSTRAINTS.contains( ac.name ) ) {
            testMethods << generateRangeTestMethods( domainClass.name,
                constraint, ac )
        } else {
            testMethods << generateTestMethod( domainClass.name,
                constraint, ac, '' )
        }// End of else
    }// End of closure
    generateFile( domainClass, constraint.propertyName, testMethods )

}// End of method

void generateFile( domainClass, propertyName, testMethods ) {

    def className = domainClass.name
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( className, propertyName )
    content << generateSetUpMethod( className, propertyName )
    content << testMethods
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    new File( directory, getFilename( className, propertyName ) ).text =
        content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className, propertyName ) {

    def content = '' << "@TestFor(${className})\n"
    content << "class ${className}${propertyName.capitalize()}"
    content << 'ConstraintsSpec extends Specification {\n\n'
    content.toString()

}// End of method

String generateSetUpMethod( className, propertyName ) {

    def attribute = getInitializedAttribute( propertyName )
    def content = '' << "${tab()}def setup() {\n"
    content << "${tab()*2}mockForConstraintsTests("
    content << " ${className}, [ new ${className}(${attribute}) ] )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String getInitializedAttribute( propertyName ) {

    if ( !properties.uniqueSettings ) return ''
    def uniqueSetting = properties.uniqueSettings[ propertyName ]
    if ( !uniqueSetting ) return ''
    " ${propertyName}:${getUniqueMinValue( uniqueSetting )} "

}// End of method

String getUniqueMinValue( map ) {

    def uniqueValue = map.min
    if ( map.propertyType == 'java.lang.String' )
        return "'A'${uniqueValue?' * ' + uniqueValue:''}"
    else return "${uniqueValue}"

}// End of method

String generateRangeTestMethods( className, constraint,
    appliedConstraint ) {

    def content = new StringBuilder()
    content << generateRangeTooShortTestMethods( className, constraint,
        appliedConstraint )
    content << generateTestMethod( className, constraint,
        appliedConstraint, 'TooLong' )
    content.toString()

}// End of method

String generateRangeTooShortTestMethods( className, constraint,
    appliedConstraint ) {

    if ( appliedConstraint.name == 'size' &&
        constraint.propertyType.name == 'java.lang.String' &&
        appliedConstraint.parameter.from <= 1 ) return ''
    generateTestMethod( className, constraint, appliedConstraint, 'TooShort' )

}// End of method

String generateTestMethod( className, constraint, appliedConstraint, suffix ) {

    def propertyName = constraint.propertyName
    def constraintName = appliedConstraint.name
    def content = '' << generateIgnoreAnnotation( constraintName )
    content << "${tab()}def \"test '${constraintName}${suffix}'"
    content << " constraint\"() {\n\n"
    content << generateWhenBlock( className, propertyName )
    content << generateThenBlock( propertyName, appliedConstraint )
    content << generateWhereBlock( constraint, appliedConstraint, suffix )
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateIgnoreAnnotation( constraintName ) {

    if ( constraintName != 'blank' ) return ''
    "${tab()}@Ignore('See http://jira.grails.org/browse/GRAILS-10474' )\n"

}// End of method

String generateWhenBlock( className, propertyName ) {

    def content = '' << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = new ${className}("
    content << " ${propertyName}:${propertyName} )\n"
    content << "${tab()*3}def result = instance.validate()\n"
    content.toString()

}// End of method

String generateThenBlock( propertyName, appliedConstraint ) {

    def constraintName = appliedConstraint.name
    def content = '' << "${tab()*2}then:\n"
    content << generateException( constraintName )
    content << generateValidateAssertion( propertyName, appliedConstraint )
    content << generateNullAssertion( propertyName, appliedConstraint )
    content << generateEqualsAssertion( propertyName, constraintName )
    content.toString()

}// End of method

String generateException( constraintName ) {

    if ( !EXCEPTION_CONSTRAINTS.contains( constraintName ) ) return ''
    def content = '' << "${tab()*3}throw new IllegalStateException("
    content << "\n${tab()*4}\"'${constraintName}' constraint found."
    content << " Please implement it by hand\" )\n"
    content.toString()

}// End of method

String generateValidateAssertion( propertyName, appliedConstraint ) {

    def flag = getValidateFlag( propertyName, appliedConstraint )
    properties.flag = flag
    "${tab()*3}result == ${flag}\n"

}// End of method

Boolean getValidateFlag( propertyName, appliedConstraint ) {

    def constraintName = appliedConstraint.name
    def constraintValue = appliedConstraint.parameter
    def required = properties.requiredAttributes
    ( required.size() == 0 &&
        constraintName == 'nullable' && constraintValue == true ) ||
        ( required.size() == 1 && required.contains( propertyName ) &&
        constraintName == 'blank' && constraintValue == true )

}// End of method

String generateNullAssertion( propertyName, appliedConstraint ) {

    def constraintName = appliedConstraint.name
    def constraintValue = appliedConstraint.parameter
    def content = '' << ''
    def flag = ( constraintName == 'nullable' || constraintName == 'blank' ) &&
        constraintValue == true
    properties.flag = flag
    content << "${tab()*3}instance.errors[ '${propertyName}' ]"
    content << " ${flag? '=' : '!'}= null\n"
    content.toString()

}// End of method

String generateEqualsAssertion( propertyName, constraintName ) {

    if ( properties.flag ) return ''
    def content = '' << "${tab()*3}instance.errors[ '${propertyName}' ]"
    content << " == '${constraintName}'\n"
    content.toString()

}// End of method

String generateWhereBlock( constraint, appliedConstraint, suffix ) {

    def propertyValue = getPropertyValue( constraint, appliedConstraint,
        suffix )
    def content = '' << "${tab()*2}where:\n"
    content << "${tab()*3}${constraint.propertyName} = ${propertyValue}\n"
    content.toString()

}// End of method

String getPropertyValue( constraint, appliedConstraint, suffix ) {

    def propertyName = constraint.propertyName
    def propertyType = constraint.propertyType
    def constraintName = appliedConstraint.name
    def constraintValue = appliedConstraint.parameter
    if ( [ 'inList', 'range' ].contains( constraintName ) ) {
        "get${constraintName.capitalize()}${suffix}Value"(
        constraintValue )
    } else if ( [ 'max', 'maxSize', 'min', 'minSize', 'notEqual',
        'size' ].contains( constraintName ) ) {
        "get${constraintName.capitalize()}${suffix}Value"(
        propertyType, constraintValue )
    } else if ( [ 'unique' ].contains( constraintName ) ) {
        "get${constraintName.capitalize()}${suffix}Value"( propertyName )
    } else {
        "get${constraintName.capitalize()}Value"()
    }// End of else

}// End of method

String getBlankValue() {
    "''"
}// End of method

String getCreditCardValue() {
    "'A'"
}// End of method

String getEmailValue() {
    "'A'"
}// End of method

String getInListValue( constraintValue ) {

    def value = createRandomWord( 10 )
    while ( constraintValue.contains( value ) ) {
        value = createRandomWord( 10 )
    }// End of while
    "'${value}'"

}// End of method

String getMatchesValue() {
    "'FIX ME'"
}// End of method

String getMaxValue( propertyType, constraintValue ) {
    getMaxMinValue( propertyType, constraintValue, 1 )
}// End of method

String getMaxSizeValue( propertyType, constraintValue ) {
    getMiscValue( propertyType, constraintValue, 1 )
}// End of method

String getMinValue( propertyType, constraintValue ) {
    getMaxMinValue( propertyType, constraintValue, -1 )
}// End of method

String getMinSizeValue( propertyType, constraintValue ) {
    getMiscValue( propertyType, constraintValue, -1 )
}// End of method

String getNotEqualValue( propertyType, constraintValue ) {

    def prefix = ''
    def suffix = ''
    if ( propertyType.name == 'java.lang.String' ) {
        prefix = "'"
        suffix = "'"
    }// End of if
    "${prefix}${constraintValue}${suffix}"

}// End of method

String getNullableValue() {
    'null'
}// End of method

String getRangeTooShortValue( constraintValue ) {
    "${(constraintValue.from as BigDecimal) - 1}"
}// End of method

String getRangeTooLongValue( constraintValue ) {
    "${(constraintValue.to as BigDecimal) + 1}"
}// End of method

String getSizeTooShortValue( propertyType, constraintValue ) {
    getMiscValue( propertyType, constraintValue.from, -1 )
}// End of method

String getSizeTooLongValue( propertyType, constraintValue ) {
    getMiscValue( propertyType, constraintValue.to, 1 )
}// End of method

String getSizeValue() {
    "'A'"
}// End of method

String getUniqueValue( propertyName ) {
    getUniqueMinValue( properties.uniqueSettings[ propertyName ] )
}// End of method

String getUrlValue() {
    "'A'"
}// End of method

String getValidatorValue() {
    "'FIX ME'"
}// End of method

String getMaxMinValue( propertyType, constraintValue, add ) {

    if ( propertyType.name == 'java.lang.String' ) {
        def charArray = constraintValue.toCharArray()
        def lastChar = charArray[ charArray.size() - 1 ]
        charArray[ charArray.size() - 1 ] =
        ( lastChar + add ) as Character
        return "'${charArray}'"
    }
    return "${(constraintValue as BigDecimal) + add}"

}// End of method

String getMiscValue( propertyType, constraintValue, add ) {

    if ( propertyType.name == 'java.lang.String' ) {
        return "'A' * ${constraintValue + add}"
    }
    if ( propertyType.name.startsWith( '[' ) ) {
        def arrayType = getArrayType( propertyType.name )
        return "new ${arrayType}[ ${(constraintValue as BigDecimal)+add} ]"
    }
    return "${(constraintValue as BigDecimal) + add}"

}// End of method

String createRandomWord( size ) {

    def random = new Random()
    def output = new StringBuilder()
    size.times { output << ( ( 65 + random.nextInt( 26 ) ) as Character ) }
    output.toString()

}// End of method

String getFilename( className, propertyName ) {

    def fileName = '' << "${className}${propertyName.capitalize()}"
    fileName << "ConstraintsSpec.groovy"
    fileName.toString()

}// End of method

