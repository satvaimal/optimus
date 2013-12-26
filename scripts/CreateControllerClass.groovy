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
    content << "}${comment('class')}"
    def directory = generateDirectory( "grails-app/controllers", domainClass.packageName )
    def fileName = "${domainClass.name}Controller.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateAllowedMethods() {

    def content = '' << "${tab()}static allowedMethods = [\n"
    content << "${tab()*2}index:'GET',\n"
    content << "${tab()*2}content:'GET',\n"
    content << "${tab()*2}list:'GET',\n"
    content << "${tab()*2}create:'GET',\n"
    content << "${tab()*2}save:'POST',\n"
    content << "${tab()*2}edit:'GET',\n"
    content << "${tab()*2}update:'POST',\n"
    content << "${tab()*2}delete:'POST'\n"
    content << "${tab()}]\n\n"
    content.toString()

}// End of method

String generateServiceDependencies( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def ${classNameLower}Service\n"
    content << "${tab()}def ${CRACKING_SERVICE}Service\n\n"
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

    def content = '' << "${tab()}def index() {\n"
    content << "${tab()*2}redirect( action:'content', params:params )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateContentMethod() {

    def content = '' << "${tab()}def content() {\n"
    content << "${tab()*2}renderList( 'content' )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateListMethod() {

    def content = '' << "${tab()}def list() {\n"
    content << "${tab()*2}renderList( 'list' )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateCreateMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def create() {\n\n"
    content << "${tab()*2}def model = "
    content << "[ ${classNameLower}Instance:new ${className}( params ) ]\n"
    content << "${tab()*2}render( template:'form', model:model )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateSaveMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def save() {\n\n"
    content << "${tab()*2}def ${classNameLower} = new ${className}( params )\n"
    content << "${tab()*2}saveOnDb( ${classNameLower}, 'create' )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateEditMethod( idType ) {

    def content = '' << "${tab()}def edit( ${idType} id ) {\n\n"
    content << "${tab()*2}def map = get( id )\n"
    content << "${tab()*2}if ( !map ) return\n"
    content << "${tab()*2}map.edit = true\n"
    content << "${tab()*2}render( template:'form', model:map )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateUpdateMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def update( ${idType} id ) {\n\n"
    content << "${tab()*2}def map = get( id )\n"
    content << "${tab()*2}if ( !map ) return\n"
    content << "${tab()*2}map.${classNameLower}Instance.properties = params\n"
    content << "${tab()*2}map.edit = true\n"
    content << "${tab()*2}saveOnDb( map.${classNameLower}Instance,"
    content << " 'update', true )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateDeleteMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def delete( ${idType} id ) {\n\n"
    content << "${tab()*2}def map = get( id )\n"
    content << "${tab()*2}if ( !map ) return\n"
    content << "${tab()*2}${classNameLower}Service.delete("
    content << " map.${classNameLower}Instance )\n"
    content << "${tab()*2}flash.listMessage = message("
    content << " code:'default.deleted.message',\n"
    content << "${tab()*3}args:[ message( code:'${classNameLower}.label',\n"
    content << "${tab()*3}default:'${className}' ), id ] )\n"
    content << "${tab()*2}redirect( action:'content' )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateRenderListMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}private void renderList( template ) {\n\n"
    content << "${tab()*2}def model = [:]\n"
    content << "${tab()*2}def result = ${classNameLower}Service.list( params )\n"
    content << "${tab()*2}model.items = result.items\n"
    content << "${tab()*2}model.total = result.total\n"
    content << "${tab()*2}render( template:template, model:model )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateGetMethod( className, idType ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}private Map get( ${idType} id ) {\n\n"
    content << "${tab()*2}if ( id == null ) {\n"
    content << "${tab()*3}notifyCrack()\n"
    content << "${tab()*3}return null\n"
    content << "${tab()*2}}${comment('if')}\n"
    content << "${tab()*2}def ${classNameLower} ="
    content << " ${classNameLower}Service.get( id )\n"
    content << "${tab()*2}if ( !${classNameLower} ) {\n"
    content << "${tab()*3}notifyCrack()\n"
    content << "${tab()*3}return null\n"
    content << "${tab()*2}}${comment('if')}\n"
    content << "${tab()*2}[ ${classNameLower}Instance:${classNameLower} ]\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateSaveOnDbMethod( className, idName ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}private void saveOnDb( ${classNameLower}"
    content << ", method, edit = false ) {\n\n"
    content << "${tab()*2}try {\n"
    content << "${tab()*3}${classNameLower}Service.\"\${method}\""
    content << "( ${classNameLower} )\n"
    content << "${tab()*2}} catch ( IllegalArgumentException e ) {\n"
    content << "${tab()*3}response.status = 400\n"
    content << "${tab()*3}render( template:'form',"
    content << " model:[ ${classNameLower}Instance:${classNameLower},\n"
    content << "${tab()*4}edit:edit ] )\n"
    content << "${tab()*3}return\n"
    content << "${tab()*2}}${comment('catch')}\n"
    content << "${tab()*2}flash.formMessage = message(\n"
    content << "${tab()*3}code:\"default.\${edit?'updated'"
    content << ":'created'}.message\",\n"
    content << "${tab()*3}args:[ message( code:'${classNameLower}.label',\n"
    content << "${tab()*3}default:'${className}' ),"
    content << " ${classNameLower}.${idName}])\n"
    content << "${tab()*2}redirect( action:'edit'"
    content << ", id:${classNameLower}.${idName} )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateNotifyCrackMethod() {

    def content = '' << "${tab()}private void notifyCrack() {\n\n"
    content << "${tab()*2}crackingService.notify( request, params )\n"
    content << "${tab()*2}redirect( controller:'logout' )\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
