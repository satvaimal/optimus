includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestConstraints:'Generate unit test for class domain constraints' ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of constraints unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestConstraints )

void generate( domainClass ) {

    def constraints = domainClass.constrainedProperties
    properties.uniqueSettings = getUniqueSettings( constraints )
    properties.requiredAttributes = getRequiredAttributes( constraints )
    constraints.each { this.processConstraint( domainClass, it.value ) }

}// End of method

void processConstraint( domainClass, constraint ) {

    if ( EXCLUDED_ATTRIBUTES.contains( constraint.propertyName ) ||
        constraint.propertyType.name == 'boolean' ) return
    def testMethods = '' << ''
    constraint.appliedConstraints.each { ac ->
        if ( EXCLUDED_CONSTRAINTS.contains( ac.name ) ) return
        else if ( PRIMITIVE_TYPES.contains( constraint.propertyType ) &&
        ac.name == 'nullable' ) return
        else if ( RANGE_CONSTRAINTS.contains( ac.name ) ) {
        testMethods << this.generateRangeTestMethods( domainClass.name,
            constraint, ac )
        } else {
        testMethods << this.generateTestMethod( domainClass.name,
            constraint, ac, '' )
        }// End of else
    }// End of closure
    this.generateFile( domainClass, constraint.propertyName, testMethods )

}// End of method

void generateFile( domainClass, propertyName, testMethods ) {

    def className = domainClass.name
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << this.generateImports()
    content << this.generateClassDeclaration( className, propertyName )
    content << this.generateSetUpMethod( className, propertyName )
    content << testMethods
    content << '}'
    def directory = generateDirectory( "test/unit",
        domainClass.packageName )
    new File( "${directory}/${getFilename(className, propertyName)}" ).text =
        content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className, propertyName ) {

    def content = '' << "@TestFor(${className})\n"
    content << "class ${className}${propertyName.capitalize()}"
    content << 'ConstraintsTests {\n\n'
    content.toString()

}// End of method

String generateSetUpMethod( className, propertyName ) {

    def attribute = this.getInitializedAttribute( propertyName )
    def content = '' << "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n"
    content << "${TAB*2}mockForConstraintsTests("
    content << " ${className}, [ new ${className}(${attribute}) ] )\n"
    content << "${TAB}}\n\n"
    content.toString()
    
}// End of method

String getInitializedAttribute( propertyName ) {

    if ( !properties.uniqueSettings ) return ''
    def uniqueSetting = properties.uniqueSettings[ propertyName ]
    if ( !uniqueSetting ) return ''
    " ${propertyName}:${this.getUniqueMinValue( uniqueSetting )} "

}// End of method

String getUniqueMinValue( map ) {

    def uniqueValue = map.min
    if ( map.propertyType == 'java.lang.String' )
        return "'A'${uniqueValue?' * ' + uniqueValue:''}"
    else return "${uniqueValue}"

}// End of method

String generateRangeTestMethods( className, constraint,
    appliedConstraint ) {

    def content = '' << ''
    content << this.generateRangeTooShortTestMethods( className,
        constraint, appliedConstraint )
    content << this.generateTestMethod( className, constraint,
        appliedConstraint, 'TooLong' )
    content.toString()

}// End of method

String generateRangeTooShortTestMethods( className, constraint,
    appliedConstraint ) {

    if ( appliedConstraint.name == 'size' &&
        constraint.propertyType.name == 'java.lang.String' &&
        appliedConstraint.parameter.from <= 1 ) return ''
    this.generateTestMethod( className, constraint,
        appliedConstraint, 'TooShort' )

}// End of method

String generateTestMethod( className, constraint, appliedConstraint, suffix ) {

    def propertyName = constraint.propertyName
    def constraintName = appliedConstraint.name
    def content = '' << "${TAB}void test"
    content << "${constraintName.capitalize()}${suffix}() {\n\n"
    content << this.generateException( constraintName )
    content << this.generateClassInstance( className )
    content << this.generateAttributeSetting( constraint, appliedConstraint,
        suffix )
    content << this.generateValidateAssertion( propertyName, appliedConstraint )
    content << this.generateNullAssertion( propertyName, appliedConstraint )
    content << this.generateEqualsAssertion( propertyName, constraintName )
    content << this.closeExceptionComment( constraintName )
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateException( constraintName ) {

    if ( !EXCEPTION_CONSTRAINTS.contains( constraintName ) ) return ''
    def content = '' << "${TAB*2}throw new IllegalStateException("
    content << "\n${TAB*3}\"'${constraintName}' constraint found."
    content << " Please implement it by hand\" )\n/*\n"
    content.toString()

}// End of method

String generateClassInstance( className ) {

    def content = '' << "${TAB*2}def instance = new ${className}()\n"
    content.toString()

}// End of method

String generateAttributeSetting( constraint, appliedConstraint, suffix ) {

    def propertyValue = this.getPropertyValue( constraint, appliedConstraint,
        suffix )
    def content = '' << "${TAB*2}instance.${constraint.propertyName}"
    content << " = ${propertyValue}\n"
    content.toString()
    
}// End of method

String getPropertyValue( constraint, appliedConstraint, suffix ) {

    def propertyName = constraint.propertyName
    def propertyType = constraint.propertyType
    def constraintName = appliedConstraint.name
    def constraintValue = appliedConstraint.parameter
    if ( [ 'inList', 'range' ].contains( constraintName ) ) {
        this."get${constraintName.capitalize()}${suffix}Value"(
        constraintValue )
    } else if ( [ 'max', 'maxSize', 'min', 'minSize', 'notEqual',
        'size' ].contains( constraintName ) ) {
        this."get${constraintName.capitalize()}${suffix}Value"(
        propertyType, constraintValue )
    } else if ( [ 'unique' ].contains( constraintName ) ) {
        this."get${constraintName.capitalize()}${suffix}Value"( propertyName )
    } else {
        this."get${constraintName.capitalize()}Value"()
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

    def value = this.createRandomWord( 10 )
    while ( constraintValue.contains( value ) ) {
        value = this.createRandomWord( 10 )
    }// End of while
    "'${value}'"

}// End of method

String getMatchesValue() {
    "'FIX ME'"
}// End of method

String getMaxValue( propertyType, constraintValue ) {
    this.getMaxMinValue( propertyType, constraintValue, 1 )
}// End of method

String getMaxSizeValue( propertyType, constraintValue ) {
    this.getMiscValue( propertyType, constraintValue, 1 )
}// End of method

String getMinValue( propertyType, constraintValue ) {
    this.getMaxMinValue( propertyType, constraintValue, -1 )
}// End of method

String getMinSizeValue( propertyType, constraintValue ) {
    this.getMiscValue( propertyType, constraintValue, -1 )
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
    this.getMiscValue( propertyType, constraintValue.from, -1 )
}// End of method

String getSizeTooLongValue( propertyType, constraintValue ) {
    this.getMiscValue( propertyType, constraintValue.to, 1 )
}// End of method

String getSizeValue() {
    "'A'"
}// End of method

String getUniqueValue( propertyName ) {
    this.getUniqueMinValue( properties.uniqueSettings[ propertyName ] )
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
    } else {
        return "${(constraintValue as BigDecimal) + add}"
    }// End of else

}// End of method

String getMiscValue( propertyType, constraintValue, add ) {

    if ( propertyType.name == 'java.lang.String' ) {
        return "'A' * ${constraintValue + add}"
    } else if ( propertyType.name.startsWith( '[' ) ) {
        def arrayType = getArrayType( propertyType.name )
        return "new ${arrayType}[ ${(constraintValue as BigDecimal)+add} ]"
    } else {
        return "${(constraintValue as BigDecimal) + add}"
    }// End of else

}// End of method

String generateValidateAssertion( propertyName, appliedConstraint ) {

    def flag = this.getValidateFlag( propertyName, appliedConstraint )
    properties.flag = flag
    def content = '' << "${TAB*2}assert${flag.toString().capitalize()}"
    content << " \"'validate' should be ${flag}\", instance.validate()\n"
    content.toString()

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
    content << "${TAB*2}assert${flag?'':'Not'}Null"
    content << " \"'errors[ '${propertyName}' ]'"
    content << " should${flag?' ':' not '}be null\",\n"
    content << "${TAB*3}instance.errors[ '${propertyName}' ]\n"
    content.toString()

}// End of method

String generateEqualsAssertion( propertyName, constraintName ) {

    if ( properties.flag ) return ''
    def content = '' << ''
    content << "${TAB*2}assertEquals \"'errors[ '${propertyName}' ]'"
    content << " should be '${constraintName}'\",\n"
    content << "${TAB*3}'${constraintName}', "
    content << "instance.errors[ '${propertyName}' ]\n"
    content.toString()

}// End of method

String closeExceptionComment( constraintName ) {

    if ( !EXCEPTION_CONSTRAINTS.contains( constraintName ) ) return ''
    "*/\n"

}// End of method

String createRandomWord( size ) {

    def random = new Random()
    def output = '' << ''
    size.times { output << ( ( 65 + random.nextInt( 26 ) ) as Character ) }
    output.toString()

}// End of method

String getFilename( className, propertyName ) {

    def fileName = '' << "${className}${propertyName.capitalize()}"
    fileName << "ConstraintsTests.groovy"
    fileName.toString()

}// End of method
