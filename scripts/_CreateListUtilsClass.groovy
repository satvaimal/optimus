target( createListUtils:'Generate ListUtils class' ) {

    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    generate( domainClassList[ 0 ] )
    def msg = "Finished generation of ListUtils class file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createListUtils )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << "class ListUtils {\n\n"
    content << generateParseMaxMethod()
    content << generateParseOffsetMethod()
    content << generateParseOrderMethod()
    content << generateParseSortMethod()
    content << "}${comment('class')}"
    def directory = generateDirectory( "src/groovy", domainClass.packageName )
    createFile( directory, 'ListUtils.groovy', content.toString() )

}// End of method

String generateParseMaxMethod() {

    def content = new StringBuilder()
    content << "${tab()}static Integer parseMax( max ) {\n\n"
    content << "${tab()*2}if ( max instanceof Integer )"
    content << " return Math.min( max, 10 )\n"
    content << "${tab()*2}if ( !( max instanceof String ) ) return 10\n"
    content << "${tab()*2}if ( max?.isInteger() && max != '0' ) {\n"
    content << "${tab()*3}def maxInteger = new Integer( max )\n"
    content << "${tab()*3}return Math.min( maxInteger, 10 )\n"
    content << "${tab()*2}}${comment('if')}\n"
    content << "${tab()*2}10\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateParseOffsetMethod() {

    def content = new StringBuilder()
    content << "${tab()}static Integer parseOffset( offset ) {\n\n"
    content << "${tab()*2}if ( offset instanceof Integer ) return offset\n"
    content << "${tab()*2}if ( !( offset instanceof String ) ) return null\n"
    content << "${tab()*2}if ( offset?.isInteger() ) {\n"
    content << "${tab()*3}return new Integer( offset )\n"
    content << "${tab()*2}}${comment('if')}\n"
    content << "${tab()*2}null\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateParseOrderMethod() {

    def content = new StringBuilder()
    content << "${tab()}static String parseOrder( order ) {\n\n"
    content << "${tab()*2}if ( !( order instanceof String ) ) return null\n"
    content << "${tab()*2}if ( order == 'asc' ||"
    content << " order == 'desc' ) {\n"
    content << "${tab()*3}return order\n"
    content << "${tab()*2}}${comment('if')}\n"
    content << "${tab()*2}null\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateParseSortMethod() {

    def content = new StringBuilder()
    content << "${tab()}static String parseSort( sort, fields ) {\n\n"
    content << "${tab()*2}if ( !( sort instanceof String ) ) return null\n"
    content << "${tab()*2}fields.find { it == sort }\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
