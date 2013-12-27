import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListMax.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListOffset.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListSortOrder.groovy' )

target( createUnitTestServiceList:
    "Generate unit tests for 'list' service method" ) {

    depends( createUnitTestServiceListMax,
        createUnitTestServiceListOffset,
        createUnitTestServiceListSortOrder )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceList )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod()
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ServiceListSpec.groovy"
    createFile( directory, filename, content.toString() )

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateOkMethod() {

    def content = '' << "${tab()}def \"test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.list( params )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result.items != null\n"
    content << "${tab()*3}result.total != null\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}params = [:]\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
