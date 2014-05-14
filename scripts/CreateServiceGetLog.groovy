import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceGetLog:"Generate application logs for 'get' service method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'get' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceGetLog )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << generateImports( domainClass.packageName, domainClass.name )
    content << generateClassDeclaration( domainClass.name )
    content << generatePointcutMethod( domainClass, idAssigned )
    content << generateBeforeMethod( idAssigned )
    content << generateAfterReturningMethod( domainClass.name, idAssigned )
    content << generateAfterThrowingMethod( idAssigned )
    content << "}${comment('class')}"
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def filename = "${domainClass.name}ServiceGet.groovy"
    createFile( directory, filename, content.toString() )

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
    content << "class ${className}ServiceGet {\n\n"
    content.toString()

}// End of method

String generatePointcutMethod( domainClass, idAssigned ) {

    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${tab()}@Pointcut(\n"
    content << "${tab()*2}value='execution(${domainClass.fullName} "
    content << "${domainClass.fullName}Service.get(..)) && bean"
    content << "(${classNameLower}Service) && args(${idName})',\n"
    content << "${tab()*2}argNames='${idName}')\n"
    content << "${tab()}public void getMethod( ${idType}"
    content << " ${idName} ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod( idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${tab()}@Before('getMethod(${idName})')\n"
    content << "${tab()}void before( ${idType} ${idName} ) {\n"
    content << "${tab()*2}log.info( \"Begins request: \${${idName}}\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod( className, idAssigned ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${tab()}@AfterReturning(\n"
    content << "${tab()*2}pointcut='getMethod(${idType})',\n"
    content << "${tab()*2}returning='${classNameLower}')\n"
    content << "${tab()}void afterReturning( "
    content << "${className} ${classNameLower} ) {\n"
    content << "${tab()*2}log.info( \"End of request: \${${classNameLower}}\" )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod( idAssigned ) {

    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${tab()}@AfterThrowing(\n"
    content << "${tab()*2}pointcut='getMethod(${idType})',\n"
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
