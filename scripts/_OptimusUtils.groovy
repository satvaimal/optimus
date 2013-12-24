import org.codehaus.groovy.grails.orm.hibernate.cfg.CompositeIdentity
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

includeTargets << grailsScript( '_GrailsPackage' )
includeTargets << grailsScript( '_GrailsBootstrap' )

EXCLUDED_CONSTRAINTS = [
    'attributes',
    'bindable',
    'scale',
    'widget'
]

EXCEPTION_CONSTRAINTS = [
    'matches',
    'validator'
]

ARRAY_TYPES = [
    '[Z':'boolean',
    '[B':'byte',
    '[S':'short',
    '[I':'int',
    '[J':'long',
    '[F':'float',
    '[D':'double',
    '[C':'char'
]

PRIMITIVE_NUMERIC_TYPES = [
    'byte',
    'short',
    'int',
    'long',
    'float',
    'double'
]

PRIMITIVE_TYPES = PRIMITIVE_NUMERIC_TYPES + [
    'boolean',
    'char'
]

RANGE_CONSTRAINTS = [
    'range',
    'size'
]

EXCLUDED_ATTRIBUTES = [
    'dateCreated', 
    'lastUpdated'
]

WRAPPER_NUMERIC_TYPES = [
    'java.lang.Byte',
    'java.lang.Short',
    'java.lang.Integer',
    'java.lang.Long',
    'java.lang.Float',
    'java.lang.Double'
]

WRAPPER_TYPES = WRAPPER_NUMERIC_TYPES + [
    'java.lang.Boolean',
    'java.lang.Character'
]

CRACKING_SERVICE = 'cracking'

I18N_COMMON_MSG = 'Property [{0}] of class [{1}] with value [{2}] '

properties = [:]

getDomainClassList = { args ->

    if ( !args ) {
    grailsConsole.error 'You must specify a Domain class'
    return null
    }// End of if
    def domainClassList = []
    def arg = args.split( /\n/ )[ 0 ]
    if ( arg == '*' ) domainClassList = grailsApp.domainClasses
    else {
        def domainClass = grailsApp.domainClasses.find { it.name == arg }
        if ( !domainClass ) {
            grailsConsole.error "'${arg}' IS NOT A DOMAIN CLASS!!!"
            return null
        }// End of if
        domainClassList << domainClass
    }// End of else
    domainClassList

}// End of closure

getArrayType = { name ->

    if ( name.startsWith( '[Ljava.lang.' ) ) {
        return name - '[Ljava.lang.' - ';'
    }// End of if
    ARRAY_TYPES[ name ]

}// End of closure

getRequiredAttributes = { constraints ->

    def requiredAttributes = []
    constraints.each {
        for ( ac in it.value.appliedConstraints ) {
            if ( ac.name == 'nullable' && ac.parameter == false ) {
                requiredAttributes << it.key
                break;
            }// End of if
        }// End of closure
    }// End of closure
    requiredAttributes

}// End of closure

getUniqueSettings = { constraints ->

    def uniqueSettings = [:]
    constraints.each {
        def map = null
        def minValue = null
        it.value.appliedConstraints.each { ac ->
            if ( ac.name == 'unique' ) {
                map = [ propertyType:it.value.propertyType.name ]
            }// End of if
            if ( RANGE_CONSTRAINTS.contains( ac.name ) ) {
                minValue = ac.parameter.from
            }// End of if
        }// End of closure
        if ( map ) {
            map.min = minValue
            uniqueSettings[ it.key ] = map
        }// End of if
    }// End of closure
    uniqueSettings

}// End of closure

getIdAssigned = { domainClass ->

    def mapping = new GrailsDomainBinder().getMapping( domainClass.clazz )
    if ( !mapping ) return null
    def identity = mapping.identity
    if ( identity instanceof CompositeIdentity ) return null
    if ( identity.generator != 'assigned' ) return null
    def name = identity.name ?: 'id'
    def identifier = domainClass.properties.find { it.name == name }
    [ name:name, type:identifier.type.simpleName ]

}// End of closure

generateDirectory = { rootDir, pckg ->

    def packageDirectory = pckg.split( /\./ ).join( File.separator )
    def directoryName = "${rootDir}/${packageDirectory}"
    def directory = new File( directoryName )
    if ( !directory.exists() ) directory.mkdirs()
    directoryName

}// End of closure

tab = {
  ' ' * grailsApp.config.grails.optimus.tab
}// End of closure
