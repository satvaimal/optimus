target( createCrackingService:'Generate cracking service' ) {

    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    generate( domainClassList[ 0 ] )
    def msg = "Finished generation of cracking service"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createCrackingService )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << 'import javax.servlet.http.HttpServletRequest\n\n'
    content << "class ${CRACKING_SERVICE.capitalize()}Service {\n\n"
    content << generateNotifyMethod()
    content << '}'
    def directory = generateDirectory( "grails-app/services",
        domainClass.packageName )
    def fileName = "${CRACKING_SERVICE.capitalize()}Service.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateNotifyMethod() {

    def content = '' << "${TAB}void notify( HttpServletRequest request"
    content << ", Map params ) {\n\n"
    content << "${TAB*2}def message = \"Request"
    content << " \${request.requestURL}\"\n"
    content << "${TAB*2}message << \" from \${request.remoteAddr}\"\n"
    content << "${TAB*2}message << \" and params \${params}\"\n"
    content << "${TAB*2}message << \" has been detected as unusual activity\"\n"
    content << "${TAB*2}println message.toString()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
