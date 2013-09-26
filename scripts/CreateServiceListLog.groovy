import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceListLog:"Generate application logs for 'list' service method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of 'list' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceListLog )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << this.generateImports()
    content << this.generateClassDeclaration( domainClass.name )
    content << this.generatePointcutMethod( domainClass.packageName,
        domainClass.name )
    content << this.generateBeforeMethod()
    content << this.generateAfterReturningMethod()
    content << this.generateAfterThrowingMethod()
    content << '}'
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceList.groovy"
    new File( "${directory}/${fileName}" ).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << ''
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
    def content = '' << "${TAB}@Pointcut(\n"
    content << "${TAB*2}value='execution(java.util.Map "
    content << "${packageName}.${className}Service.list(..)) && bean"
    content << "(${classNameLower}Service) && args(params)',\n"
    content << "${TAB*2}argNames='params')\n"
    content << "${TAB}public void list( Map params ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod() {

    def content = '' << "${TAB}@Before('list(params)')\n"
    content << "${TAB}void before( Map params ) {\n"
    content << "${TAB*2}log.info( \"Begins request: \${params}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod() {

    def content = '' << "${TAB}@AfterReturning(\n"
    content << "${TAB*2}pointcut='list(java.util.Map)',\n"
    content << "${TAB*2}returning='map')\n"
    content << "${TAB}void afterReturning( Map map ) {\n"
    content << "${TAB*2}log.info( \"End of request: \${map}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod() {

    def content = '' << "${TAB}@AfterThrowing(\n"
    content << "${TAB*2}pointcut='list(java.util.Map)',\n"
    content << "${TAB*2}throwing='e' )\n"
    content << "${TAB}void afterThrowing( Exception e ) {\n\n"
    content << "${TAB*2}def message = '' << ''\n"
    content << "${TAB*2}message << \"Error in request\"\n"
    content << "${TAB*2}message << \":"
    content << " \${e.class.simpleName} - \${e.message}\"\n"
    content << "${TAB*2}log.info( message.toString() )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
