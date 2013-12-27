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

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass )
    content << generateMethod( 'null', "null" )
    content << generateMethod( 'blank', "''" )
    content << generateMethod( 'invalid', "'A'" )
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ServiceListSortOrderSpec.groovy"
    createFile( directory, filename, content.toString() )

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListSortOrderSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${tab()}def setup() {\n\n"
    content << "${tab()*2}20.times {\n"
    content << "${tab()*3}${className}Mock.mock( it + 1 ).save("
    content << " failOnError:true )\n"
    content << "${tab()*2}}${comment('closure')}\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateOkMethod( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    def expected = getId( domainClass, idAssigned )
    def content = '' << "${tab()}def \"test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.list( params )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result.items[ 0 ].${idName} == ${expected}\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}params = [ sort:'${idName}', order:'desc' ]\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String getId( domainClass, idAssigned ) {

    if ( !idAssigned ) return '20'
    def idName = idAssigned.name
    def constraint = domainClass.constrainedProperties[ idName ]
    if ( !constraint ) return '20'
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

    def content = '' << "${tab()}def \"test ${methodSuffix}\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.list( params )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result.items.size() == 10\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}params = [ sort:${value}"
    content << ", order:${value} ]\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
