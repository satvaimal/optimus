import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceDeleteLog:"Generate application logs for 'delete' service method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of 'delete' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceDeleteLog )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << this.generateImports( domainClass )
    content << this.generateClassDeclaration( domainClass.name )
    content << this.generatePointcutMethod( domainClass )
    content << this.generateBeforeMethod( domainClass.name )
    content << this.generateAfterReturningMethod( domainClass )
    content << this.generateAfterThrowingMethod( domainClass )
    content << '}'
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceDelete.groovy"
    new File( "${directory}/${fileName}" ).text = content.toString()

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
    def content = '' << "${TAB}@Pointcut(\n"
    content << "${TAB*2}value='execution(void "
    content << "${domainClass.fullName}Service.delete(..)) && bean"
    content << "(${classNameLower}Service) && args(${classNameLower})',\n"
    content << "${TAB*2}argNames='${classNameLower}')\n"
    content << "${TAB}public void delete( "
    content << "${className} ${classNameLower} ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}@Before('delete("
    content << "${classNameLower})')\n"
    content << "${TAB}void before( ${className} "
    content << "${classNameLower} ) {\n"
    content << "${TAB*2}log.info( \"Begins request:\${${classNameLower}}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod( domainClass ) {

    def content = '' << "${TAB}@AfterReturning(\n"
    content << "${TAB*2}pointcut='delete("
    content << "${domainClass.fullName})')\n"
    content << "${TAB}void afterReturning() {\n"
    content << "${TAB*2}log.info( \"End of request\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod( domainClass ) {

    def content = '' << "${TAB}@AfterThrowing(\n"
    content << "${TAB*2}pointcut='delete("
    content << "${domainClass.fullName})',\n"
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
