import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/_CreateCrackingService.groovy' )

target( createControllerClass:'Generate controller for class domain' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createCrackingService )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of controller class files"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createControllerClass )

void generate( domainClass ) {

    def classNameLower = WordUtils.uncapitalize( domainClass.name )
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << "class ${domainClass.name}Controller {\n\n"
    content << generateAllowedMethods()
    content << generateServiceDependencies( domainClass.name )
    content << generateMethods( domainClass )
    content << '}'
    def directory = generateDirectory( "grails-app/controllers", domainClass.packageName )
    def fileName = "${domainClass.name}Controller.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateAllowedMethods() {

    def content = '' << "${TAB}static allowedMethods = [\n"
    content << "${TAB*2}index:'GET',\n"
    content << "${TAB*2}content:'GET',\n"
    content << "${TAB*2}list:'GET',\n"
    content << "${TAB*2}create:'GET',\n"
    content << "${TAB*2}save:'POST',\n"
    content << "${TAB*2}edit:'GET',\n"
    content << "${TAB*2}update:'POST',\n"
    content << "${TAB*2}delete:'POST'\n"
    content << "${TAB}]\n\n"
    content.toString()

}// End of method

String generateServiceDependencies( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def ${classNameLower}Service\n"
    content << "${TAB}def ${CRACKING_SERVICE}Service\n\n"
    content.toString()

}// End of method

String generateMethods( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idType = idAssigned ? idAssigned.type : 'Long'
    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << generateIndexMethod()
    content << generateContentMethod()
    content << generateListMethod()
    content << generateCreateMethod( domainClass.name )
    content << generateSaveMethod( domainClass.name )
    content << generateEditMethod( idType )
    content << generateUpdateMethod( domainClass.name, idType )
    content << generateDeleteMethod( domainClass.name, idType )
    content << generateRenderListMethod( domainClass.name )
    content << generateGetMethod( domainClass.name, idType )
    content << generateSaveOnDbMethod( domainClass.name, idName )
    content << generateNotifyCrackMethod()
    content.toString()

}// End of method

String generateIndexMethod() {

    def content = '' << "${TAB}def index() {\n"
    content << "${TAB*2}redirect( action:'content', params:params )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateContentMethod() {

    def content = '' << "${TAB}def content() {\n"
    content << "${TAB*2}renderList( 'content' )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateListMethod() {

    def content = '' << "${TAB}def list() {\n"
    content << "${TAB*2}renderList( 'list' )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateCreateMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def create() {\n\n"
    content << "${TAB*2}def model = "
    content << "[ ${classNameLower}Instance:new ${className}( params ) ]\n"
    content << "${TAB*2}render( template:'form', model:model )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateSaveMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def save() {\n\n"
    content << "${TAB*2}def ${classNameLower} = new ${className}( params )\n"
    content << "${TAB*2}saveOnDb( ${classNameLower}, 'create' )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateEditMethod( idType ) {

    def content = '' << "${TAB}def edit( ${idType} id ) {\n\n"
    content << "${TAB*2}def map = get( id )\n"
    content << "${TAB*2}if ( !map ) return\n"
    content << "${TAB*2}map.edit = true\n"
    content << "${TAB*2}render( template:'form', model:map )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateUpdateMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def update( ${idType} id ) {\n\n"
    content << "${TAB*2}def map = get( id )\n"
    content << "${TAB*2}if ( !map ) return\n"
    content << "${TAB*2}map.${classNameLower}Instance.properties = params\n"
    content << "${TAB*2}map.edit = true\n"
    content << "${TAB*2}saveOnDb( map.${classNameLower}Instance,"
    content << " 'update', true )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateDeleteMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def delete( ${idType} id ) {\n\n"
    content << "${TAB*2}def map = get( id )\n"
    content << "${TAB*2}if ( !map ) return\n"
    content << "${TAB*2}${classNameLower}Service.delete("
    content << " map.${classNameLower}Instance )\n"
    content << "${TAB*2}flash.listMessage = message("
    content << " code:'default.deleted.message',\n"
    content << "${TAB*3}args:[ message( code:'${classNameLower}.label',\n"
    content << "${TAB*3}default:'${className}' ), id ] )\n"
    content << "${TAB*2}redirect( action:'content' )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRenderListMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private void renderList( template ) {\n\n"
    content << "${TAB*2}def model = [:]\n"
    content << "${TAB*2}def result = ${classNameLower}Service.list( params )\n"
    content << "${TAB*2}model.items = result.items\n"
    content << "${TAB*2}model.total = result.total\n"
    content << "${TAB*2}render( template:template, model:model )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateGetMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private Map get( ${idType} id ) {\n\n"
    content << "${TAB*2}if ( id == null ) {\n"
    content << "${TAB*3}notifyCrack()\n"
    content << "${TAB*3}return null\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}def ${classNameLower} ="
    content << " ${classNameLower}Service.get( id )\n"
    content << "${TAB*2}if ( !${classNameLower} ) {\n"
    content << "${TAB*3}notifyCrack()\n"
    content << "${TAB*3}return null\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}[ ${classNameLower}Instance:${classNameLower} ]\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateSaveOnDbMethod( className, idName ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private void saveOnDb( ${classNameLower}"
    content << ", method, edit = false ) {\n\n"
    content << "${TAB*2}try {\n"
    content << "${TAB*3}${classNameLower}Service.\"\${method}\""
    content << "( ${classNameLower} )\n"
    content << "${TAB*2}} catch ( IllegalArgumentException e ) {\n"
    content << "${TAB*3}response.status = 400\n"
    content << "${TAB*3}render( template:'form',"
    content << " model:[ ${classNameLower}Instance:${classNameLower},\n"
    content << "${TAB*4}edit:edit ] )\n"
    content << "${TAB*3}return\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}flash.formMessage = message(\n"
    content << "${TAB*3}code:\"default.\${edit?'updated'"
    content << ":'created'}.message\",\n"
    content << "${TAB*3}args:[ message( code:'${classNameLower}.label',\n"
    content << "${TAB*3}default:'${className}' ),"
    content << " ${classNameLower}.${idName}])\n"
    content << "${TAB*2}redirect( action:'edit'"
    content << ", id:${classNameLower}.${idName} )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateNotifyCrackMethod() {

    def content = '' << "${TAB}private void notifyCrack() {\n\n"
    content << "${TAB*2}crackingService.notify( request, params )\n"
    content << "${TAB*2}redirect( controller:'logout' )\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
