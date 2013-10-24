includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createI18n:'Generate i18n messages for class domain' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of i18n messages"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createI18n )

void generate( domainClass ) {

    def content = '' << "\n#${domainClass.name} messages\n"
    def constraints = domainClass.constrainedProperties
    constraints.each { content << processConstraint( domainClass, it.value ) }
    new File( basedir, 'grails-app/i18n/messages.properties' ).append(
        content.toString() )

}// End of method

def processConstraint( domainClass, constraint ) {

    if ( EXCLUDED_ATTRIBUTES.contains( constraint.propertyName ) ||
        constraint.propertyType.name == 'boolean' ) return
    def content = new StringBuffer()
    constraint.appliedConstraints.each { ac ->
        if ( EXCLUDED_CONSTRAINTS.contains( ac.name ) ) return
        else if ( PRIMITIVE_TYPES.contains( constraint.propertyType ) &&
        ac.name == 'nullable' ) return
        content << getConstraintMessage( domainClass, constraint, ac )
        content << '\n'
    }// End of closure
    content.toString()

}// End of method

def getConstraintMessage( domainClass, constraint, ac ) {

    def content = new StringBuffer()
    def prefix = '' << "${domainClass.propertyName}.${constraint.propertyName}"
    def msg = "get${ac.name.capitalize()}Message"( prefix )
    msg = msg.replaceAll( /\{0\}/, constraint.propertyName )
    msg = msg.replaceAll( /\{1\}/, domainClass.fullName )
    content << "${msg}"
    content.toString()

}// End of method

def getBlankMessage( prefix ) {
    "${prefix}.blank=Property [{0}] of class [{1}] cannot be blank"
}// End of method

def getCreditCardMessage( prefix ) {

    def msg = '' << "${prefix}.creditCard.invalid=Property [{0}] of class [{1}]"
    msg << " with value [{2}] is not a valid credit card number"
    msg.toString()
    
}// End of method

def getEmailMessage( prefix ) {

    def msg = '' << "${prefix}.email.invalid=Property [{0}] of class [{1}]"
    msg << " with value [{2}] is not a valid e-mail address"
    msg.toString()
    
}// End of method

def getInListMessage( prefix ) {

    def msg = '' << "${prefix}.not.inList=Property [{0}] of class [{1}]"
    msg << " with value [{2}] is not contained within the list [{3}]"
    msg.toString()
    
}// End of method

def getNullableMessage( prefix ) {
    "${prefix}.nullable=Property [{0}] of class [{1}] cannot be null"
}// End of method

def getSizeMessage( prefix ) {

    def msg = '' << "${prefix}.size.toosmall"
    msg << "=Property [{0}] of class [{1}] with value [{2}]"
    msg << " should be greater than [{3}] characters\n"
    msg << "${prefix}.size.toobig"
    msg << "=Property [{0}] of class [{1}] with value [{2}]"
    msg << " should be less than [{4}] characters"
    msg.toString()

}// End of method
