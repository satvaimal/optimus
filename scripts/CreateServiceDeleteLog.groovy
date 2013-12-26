import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceDeleteLog:"Generate application logs for 'delete' service method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'delete' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceDeleteLog )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << generateImports( domainClass )
    content << generateClassDeclaration( domainClass.name )
    content << generatePointcutMethod( domainClass )
    content << generateBeforeMethod( domainClass.name )
    content << generateAfterReturningMethod( domainClass )
    content << generateAfterThrowingMethod( domainClass )
    content << "}${comment('class')}"
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceDelete.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports( domainClass ) {

    def content = '' << "import ${domainClass.fullName}\n\n"
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
    content << "class ${className}ServiceDelete {\n\n"
    content.toString()

}// End of method

String generatePointcutMethod( domainClass ) {

    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}@Pointcut(\n"
    content << "${tab()*2}value='execution(void "
    content << "${domainClass.fullName}Service.delete(..)) && bean"
    content << "(${classNameLower}Service) && args(${classNameLower})',\n"
    content << "${tab()*2}argNames='${classNameLower}')\n"
    content << "${tab()}public void delete( "
    content << "${className} ${classNameLower} ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}@Before('delete("
    content << "${classNameLower})')\n"
    content << "${tab()}void before( ${className} "
    content << "${classNameLower} ) {\n"
    content << "${tab()*2}log.info( \"Begins request:\${${classNameLower}}\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod( domainClass ) {

    def content = '' << "${tab()}@AfterReturning(\n"
    content << "${tab()*2}pointcut='delete("
    content << "${domainClass.fullName})')\n"
    content << "${tab()}void afterReturning() {\n"
    content << "${tab()*2}log.info( \"End of request\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod( domainClass ) {

    def content = '' << "${tab()}@AfterThrowing(\n"
    content << "${tab()*2}pointcut='delete("
    content << "${domainClass.fullName})',\n"
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
