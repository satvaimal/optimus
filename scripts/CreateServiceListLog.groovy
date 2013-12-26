import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceListLog:"Generate application logs for 'list' service method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceListLog )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generatePointcutMethod( domainClass.packageName, domainClass.name )
    content << generateBeforeMethod()
    content << generateAfterReturningMethod()
    content << generateAfterThrowingMethod()
    content << "}${comment('class')}"
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceList.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = new StringBuilder()
    [ 'AfterReturning', 'AfterThrowing', 'Aspect', 'Before',
        'Pointcut' ].each {
        content << "import org.aspectj.lang.annotation.${it}\n"
    } // End of closure
    content << "\nimport org.springframework.stereotype.Component\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << '@Component\n'
    content << '@Aspect\n'
    content << "class ${className}ServiceList {\n\n"
    content.toString()

}// End of method

String generatePointcutMethod( packageName, className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}@Pointcut(\n"
    content << "${tab()*2}value='execution(java.util.Map "
    content << "${packageName}.${className}Service.list(..)) && bean"
    content << "(${classNameLower}Service) && args(params)',\n"
    content << "${tab()*2}argNames='params')\n"
    content << "${tab()}public void list( Map params ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod() {

    def content = '' << "${tab()}@Before('list(params)')\n"
    content << "${tab()}void before( Map params ) {\n"
    content << "${tab()*2}log.info( \"Begins request: \${params}\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod() {

    def content = '' << "${tab()}@AfterReturning(\n"
    content << "${tab()*2}pointcut='list(java.util.Map)',\n"
    content << "${tab()*2}returning='map')\n"
    content << "${tab()}void afterReturning( Map map ) {\n"
    content << "${tab()*2}log.info( \"End of request: \${map}\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod() {

    def content = '' << "${tab()}@AfterThrowing(\n"
    content << "${tab()*2}pointcut='list(java.util.Map)',\n"
    content << "${tab()*2}throwing='e' )\n"
    content << "${tab()}void afterThrowing( Exception e ) {\n\n"
    content << "${tab()*2}def message = '' << ''\n"
    content << "${tab()*2}message << \"Error in request\"\n"
    content << "${tab()*2}message << \":"
    content << " \${e.class.simpleName} - \${e.message}\"\n"
    content << "${tab()*2}log.info( message.toString() )\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
