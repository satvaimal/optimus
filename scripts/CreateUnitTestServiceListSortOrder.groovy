includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceListSortOrder:"Generate unit tests for 'list' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list-sort-order' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceListSortOrder )

void generate( domainClass ) {

    def content = "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass )
    content << generateMethod( 'Null', "null" )
    content << generateMethod( 'Blank', "''" )
    content << generateMethod( 'Invalid', "'A'" )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceListSortOrderTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = "import grails.test.mixin.*\n"
    content << "import org.junit.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListSortOrderTests {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n\n"
    content << "${TAB*2}20.times {\n"
    content << "${TAB*3}${className}Mock.mock( it + 1 ).save("
    content << " failOnError:true )\n"
    content << "${TAB*2}}\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    def expected = getId( domainClass, idAssigned )
    def content = ''
    content << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def params = [ sort:'${idName}', order:'desc' ]\n"
    content << "${TAB*2}def result = service.list( params )\n"
    content << "${TAB*2}def items = result.items\n"
    content << "${TAB*2}def item = items[ 0 ]\n"
    content << "${TAB*2}def expected = ${expected}\n"
    content << "${TAB*2}assertEquals \"'${idName}'"
    content << " should be '\${expected}'\","
    content << "\n${TAB*3}expected, item.${idName}\n"
    content << "\n${TAB}}\n"
    content << "\n"
    content.toString()

}// End of method

String getId( domainClass, idAssigned ) {

    if ( !idAssigned ) return '20'
    def idName = idAssigned.name
    def constraint = domainClass.constrainedProperties[ idName ]
    if ( !constraint ) return '20'
    def identifier = domainClass.properties.find { it.name == idName }
    def type = idAssigned.type
    if ( type == 'String' ) {
        def id =  "new String( ( 85 ) as Character )"
        def sizeConstraint = constraint.appliedConstraints.find{
            it.name == 'size' }
        if ( sizeConstraint )
            id << " * ${sizeConstraint.parameter.from}"
            return id.toString()
    }// End of if
    '20'

}// End of method

String generateMethod( methodSuffix, value ) {

    def content = "${TAB}void test${methodSuffix}() {\n\n"
    content << "${TAB*2}def params = [ sort:${value}"
    content << ", order:${value} ]\n"
    content << "${TAB*2}def result = service.list( params )\n"
    content << "${TAB*2}assertNotNull \"'result'"
    content << " should not be null\", result\n"
    content << "${TAB*2}def items = result.items\n"
    content << "${TAB*2}assertNotNull \"'items'"
    content << " should not be null\", items\n"
    content << "${TAB*2}assertEquals \"'size'"
    content << " should be 10\", 10, items.size()\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
