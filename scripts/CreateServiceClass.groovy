import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/_CreateListUtilsClass.groovy' )

target( createServiceClass:'Generate service for class domain' ) {

    depends( checkVersion, configureProxy, bootstrap,
        createListUtils )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of service class files"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceClass )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << this.generateImports()
    content << "class ${domainClass.name}Service {\n\n"
    content << this.generateListMethod( domainClass.name )
    content << this.generateSavePublicMethod( domainClass.name, 'create' )
    content << this.generateSavePublicMethod( domainClass.name, 'update' )
    content << this.generateGetMethod( domainClass )
    content << this.generateDeleteMethod( domainClass.name )
    content << this.generateProcessParamsMethod( domainClass )
    content << this.generateSaveMethod( domainClass.name )
    content << '}'
    def directory = generateDirectory( "grails-app/services",
        domainClass.packageName )
    def fileName = "${domainClass.name}Service.groovy"
    new File( "${directory}/${fileName}" ).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.gorm.DetachedCriteria\n"
    content << "import grails.validation.ValidationException\n"
    content << "\n"
    content.toString()

}// End of method

String generateListMethod( name ) {

    def content = '' << "${TAB}Map list( Map params ) {\n\n"
    content << "${TAB*2}this.processParams( params )\n"
    content << "${TAB*2}def criteria = new DetachedCriteria( ${name}"
    content << " ).build {}\n"
    content << "${TAB*2}[ items:criteria.list( params )"
    content << ", total:criteria.count() ]\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateSavePublicMethod( name, method ) {

    def className = WordUtils.uncapitalize( name )
    def content = '' << ''
    content << "${TAB}void ${method}( ${className.capitalize()} "
    content << "${className} ) {\n"
    content << "${TAB*2}this.save( ${className} )"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateSaveMethod( name ) {

    def className = WordUtils.uncapitalize( name )
    def content = '' << ''
    content << "${TAB}private void save( ${className.capitalize()} "
    content << "${className} ) {\n\n"
    content << "${TAB*2}if ( !${className} )"
    content << " throw new IllegalArgumentException(\n"
    content << "${TAB*3}\"Parameter '${className}' is null\" )\n"
    content << "${TAB*2}try {\n"
    content << "${TAB*3}${className}.save( failOnError:true )\n"
    content << "${TAB*2}} catch ( ValidationException ) {\n"
    content << "${TAB*3}throw new IllegalArgumentException(\n"
    content << "${TAB*4}\"Parameter '${className}'"
    content << " is invalid\" )\n"
    content << "${TAB*2}}\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateGetMethod( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def className = domainClass.name
    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << ''
    content << "${TAB}${className} get( ${idType} ${idName} ) {\n\n"
    content << "${TAB*2}if ( ${idName} == null )"
    content << " throw new IllegalArgumentException(\n"
    content << "${TAB*3}\"Parameter '${idName}' is null\" )\n"
    content << "${TAB*2}${className}.findBy"
    content << "${idName.capitalize()}( ${idName} )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateDeleteMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void delete( ${className} "
    content << "${classNameLower} ) {\n\n"
    content << "${TAB*2}if ( ${classNameLower} == null )"
    content << " throw new IllegalArgumentException(\n"
    content << "${TAB*3}\"Parameter '${classNameLower}'"
    content << " is null\" )\n"
    content << "${TAB*2}${classNameLower}.delete()\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateProcessParamsMethod( domainClass ) {

    def content = '' << "${TAB}private void processParams( params ) {\n\n"
    [ 'max', 'offset', 'order' ].each {
        content << "${TAB*2}params.${it} = "
        content << "ListUtils.parse${it.capitalize()}( params.${it} )\n"
    }// End of closure
    content << this.generateParseSort( domainClass )
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateParseSort( domainClass ) {

    def fields = this.getSortFields( domainClass )
    def content = '' << "${TAB*2}def fields = [ ${fields} ]\n"
    content << "${TAB*2}params.sort = ListUtils.parseSort("
    content << " params.sort, fields )\n"
    content.toString()

}// End of method

String getSortFields( domainClass ) {

    def fields = []
    domainClass.constrainedProperties.each {
        def type = it.value.propertyType.name
        if ( PRIMITIVE_TYPES.contains( type ) ||
            WRAPPER_TYPES.contains( type ) ||
            type == 'java.lang.String' ||
            type == 'java.util.Date' ||
            type == 'java.math.BigDecimal' ) {
            fields << "'${it.key}'"
        }// End of if
    }// End of closure
    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    fields << "'${idName}'"
    ( fields as Set ).join( ', ' )

}// End of method
