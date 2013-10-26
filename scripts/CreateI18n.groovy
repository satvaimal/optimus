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

    def content = '' << "\n# ${domainClass.name} messages\n"
    content << getDomainMessages( domainClass )
    def constraints = domainClass.constrainedProperties
    constraints.each { content << processConstraint( domainClass, it.value ) }
    new File( basedir, 'grails-app/i18n/messages.properties' ).append(
        content.toString() )

}// End of method

def getDomainMessages( domainClass ) {

    def content = new StringBuffer()
    content << "${domainClass.propertyName}.label=${domainClass.naturalName}\n"
    domainClass.properties.each {
        if ( [ 'id', 'version' ].contains( it.name ) ) return
        content << "${domainClass.propertyName}.${it.name}.label"
        content << "=${it.naturalName}\n"
    }// End of closure
    content.toString()

}// End of method

def processConstraint( domainClass, constraint ) {

    if ( EXCLUDED_ATTRIBUTES.contains( constraint.propertyName ) ||
        constraint.propertyType.name == 'boolean' ) return ''
    def content = new StringBuffer()
    constraint.appliedConstraints.each { ac ->
        if ( EXCLUDED_CONSTRAINTS.contains( ac.name ) ) return ''
        else if ( PRIMITIVE_TYPES.contains( constraint.propertyType.name ) &&
        ac.name == 'nullable' ) return ''
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

    def msg = '' << "${prefix}.creditCard.invalid=${I18N_COMMON_MSG}"
    msg << "is not a valid credit card number"
    msg.toString()
    
}// End of method

def getEmailMessage( prefix ) {

    def msg = '' << "${prefix}.email.invalid=${I18N_COMMON_MSG}"
    msg << "is not a valid e-mail address"
    msg.toString()
    
}// End of method

def getInListMessage( prefix ) {

    def msg = '' << "${prefix}.not.inList=${I18N_COMMON_MSG}"
    msg << "is not contained within the list [{3}]"
    msg.toString()
    
}// End of method

def getMatchesMessage( prefix ) {

    def msg = '' << "${prefix}.matches.invalid=${I18N_COMMON_MSG}"
    msg << "does not match the required pattern [{3}]"
    msg.toString()
    
}// End of method

def getMaxMessage( prefix ) {

    def msg = '' << "${prefix}.max.exceeded=${I18N_COMMON_MSG}"
    msg << "exceeds maximum value [{3}]"
    msg.toString()
    
}// End of method

def getMaxSizeMessage( prefix ) {

    def msg = '' << "${prefix}.maxSize.exceeded=${I18N_COMMON_MSG}"
    msg << "exceeds the maximum size of [{3}]"
    msg.toString()
    
}// End of method

def getMinMessage( prefix ) {

    def msg = '' << "${prefix}.min.exceeded=${I18N_COMMON_MSG}"
    msg << "exceeds minimum value [{3}]"
    msg.toString()
    
}// End of method

def getMinSizeMessage( prefix ) {

    def msg = '' << "${prefix}.minSize.exceeded=${I18N_COMMON_MSG}"
    msg << "exceeds the minimum size of [{3}]"
    msg.toString()
    
}// End of method

def getNotEqualMessage( prefix ) {

    def msg = '' << "${prefix}.notEqual=${I18N_COMMON_MSG}"
    msg << "cannot equal [{3}]"
    msg.toString()
    
}// End of method

def getNullableMessage( prefix ) {
    "${prefix}.nullable=Property [{0}] of class [{1}] cannot be null"
}// End of method

def getRangeMessage( prefix ) {

    def msg = '' << "${prefix}.range.toosmall"
    msg << "=${I18N_COMMON_MSG}should be greater than [{3}]\n"
    msg << "${prefix}.range.toobig"
    msg << "=${I18N_COMMON_MSG}should be less than [{4}]"
    msg.toString()

}// End of method

def getSizeMessage( prefix ) {

    def msg = '' << "${prefix}.size.toosmall"
    msg << "=${I18N_COMMON_MSG}should be greater than [{3}] characters\n"
    msg << "${prefix}.size.toobig"
    msg << "=${I18N_COMMON_MSG}should be less than [{4}] characters"
    msg.toString()

}// End of method

def getUniqueMessage( prefix ) {

    def msg = '' << "${prefix}.unique=${I18N_COMMON_MSG}"
    msg << "must be unique"
    msg.toString()
    
}// End of method

def getUrlMessage( prefix ) {

    def msg = '' << "${prefix}.url.invalid=${I18N_COMMON_MSG}"
    msg << "is not a valid URL"
    msg.toString()
    
}// End of method

def getValidatorMessage( prefix ) {

    def msg = '' << "${prefix}.validator.error=${I18N_COMMON_MSG}"
    msg << "does not pass custom validation"
    msg.toString()
    
}// End of method
