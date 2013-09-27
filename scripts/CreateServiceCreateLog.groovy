import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceCreateLog:"Generate application logs for 'create' service method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'create' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceCreateLog )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << generateImports( domainClass.packageName, domainClass.name )
    content << generateClassDeclaration( domainClass.name )
    content << generatePointcutMethod( domainClass.packageName, domainClass.name )
    content << generateBeforeMethod( domainClass.name )
    content << generateAfterReturningMethod( domainClass.packageName, domainClass.name )
    content << generateAfterThrowingMethod( domainClass.packageName, domainClass.name )
    content << '}'
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceCreate.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports( packageName, className ) {

    def content = '' << "import ${packageName}.${className}\n\n"
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
    content << "class ${className}ServiceCreate {\n\n"
    content.toString()

}// End of method

String generatePointcutMethod( packageName, className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}@Pointcut(\n"
    content << "${TAB*2}value='execution(void "
    content << "${packageName}.${className}Service.create(..)) && bean"
    content << "(${classNameLower}Service) && args(${classNameLower})',\n"
    content << "${TAB*2}argNames='${classNameLower}')\n"
    content << "${TAB}public void create( "
    content << "${className} ${classNameLower} ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}@Before('create("
    content << "${classNameLower})')\n"
    content << "${TAB}void before( ${className} "
    content << "${classNameLower} ) {\n"
    content << "${TAB*2}log.info( \"Begins request: \${${classNameLower}}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod( packageName, className ) {
    def content = '' << "${TAB}@AfterReturning(\n"
    content << "${TAB*2}pointcut='create("
    content << "${packageName}.${className})')\n"
    content << "${TAB}void afterReturning() {\n"
    content << "${TAB*2}log.info( \"End of request\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod( packageName, className ) {

    def content = '' << "${TAB}@AfterThrowing(\n"
    content << "${TAB*2}pointcut='create("
    content << "${packageName}.${className})',\n"
    content << "${TAB*2}throwing='e' )\n"
    content << "${TAB}void afterThrowing( Exception e ) {\n\n"
    content << "${TAB*2}def message = ''\n"
    content << "${TAB*2}message << \"Error in request\"\n"
    content << "${TAB*2}message << \":"
    content << " \${e.class.simpleName} - \${e.message}\"\n"
    content << "${TAB*2}log.info( message.toString() )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
