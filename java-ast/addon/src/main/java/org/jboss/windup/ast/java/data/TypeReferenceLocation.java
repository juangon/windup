package org.jboss.windup.ast.java.data;

/**
 * Designates a location where a given {@link org.jboss.windup.ast.java.data.ClassReference} was found in a Java source
 * file.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum TypeReferenceLocation
{
    /**
     * A Java class imports the type.
     */
    IMPORT("Import of"),
    
    /**
     * A Java class declares the type.
     */
    TYPE("Declares type"),
    
    /**
     * A Java class declares the referenced method.
     */
    METHOD("Declares method"),
    
    /**
     * A Java class inherits the specified type.
     */
    INHERITANCE("Inherits type"),
    
    /**
     * A Java class constructs the specified type.
     */
    CONSTRUCTOR_CALL("Constructing type"),
    
    /**
     * A Java class calls the specified method.
     */
    METHOD_CALL("Calls method"),
    
    /**
     * A Java class declares the referenced method parameter.
     */
    METHOD_PARAMETER("Method parameter"),
    
    /**
     * A Java class references the annotation.
     */
    ANNOTATION("References annotation"),
    
    /**
     * A Java class returns the specified type.
     */
    RETURN_TYPE("Returns type"),
    
    /**
     * A Java class of the specified type is used in an instanceof statement.
     */
    INSTANCE_OF("Instance of type"),
    
    /**
     * A Java class declares that it may throw the specified type.
     */
    THROWS_METHOD_DECLARATION("Throws"),
    
    /**
     * A method in the Java class throws the an instance of the specified type.
     */
    THROW_STATEMENT("Throw"),
    
    /**
     * A Java class method catches the specified type.
     */
    CATCH_EXCEPTION_STATEMENT("Catches exception"),
    
    /**
     * A Java class declares a field of the specified type.
     */
    FIELD_DECLARATION("Declares field"),
    
    /**
     * A Java class declares a variable of the specified type.
     */
    VARIABLE_DECLARATION("Declares variable"),
    
    /**
     * A Java class implements the specified type.
     */
    IMPLEMENTS_TYPE("Implements type");

    private String readablePrefix;

    private TypeReferenceLocation(String readablePrefix)
    {
        this.readablePrefix = readablePrefix;
    }

    /**
     * Returns the enumeration as a human readable value.
     */
    public String toReadablePrefix()
    {
        return this.readablePrefix;
    }
}