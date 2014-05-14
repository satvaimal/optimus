includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceListMax:"Generate  unit tests for 'list' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list-max' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceListMax )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateMethod( 'low value', "'9'", 9 )
    content << generateMethod( 'high value', "'11'", 10 )
    content << generateMethod( 'null value', "null", 10 )
    content << generateMethod( 'blank value', "''", 10 )
    content << generateMethod( 'invalid value', "'A'", 10 )
    content << generateMethod( 'integer value', "9", 9 )
    content << generateMethod( 'floating value', "9.5", 10 )
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ServiceListMaxSpec.groovy"
    createFile( directory, filename, content.toString() )

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.Mock\n"
    content << "import grails.test.mixin.TestFor\n"
    content << "import spock.lang.Specification\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListMaxSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${tab()}def setup() {\n\n"
    content << "${tab()*2}20.times {\n"
    content << "${tab()*3}${className}Mock.mock( it ).save("
    content << " failOnError:true )\n"
    content << "${tab()*2}}${comment('closure')}\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateMethod( methodSuffix, maxValue, equalsValue ) {

    def content = '' << "${tab()}def \"Testing ${methodSuffix}\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.list( params )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result.items.size() == ${equalsValue}\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}params = [ max:${maxValue} ]\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
