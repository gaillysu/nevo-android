# Nevo Android #

### Contribution guidelines ###

#### One example, all conventions. :

```java
public class SomeClass extends SomeOtherClass implements SomeInterface{
	
	// You can also extend SomeAbstractClass, thats no problem
	// You can also call your interface iSomeInterface, neither a problem
	
	//Obviously all captical letters and underscore
	// Think about if you are going to use it in the class, in the package or outside by declaring the pkg declaration
	public final int MY_FINAL_STATIC_VARIABLE = 0;

	// NEVER make public variables
	private String someVariable;

	// These variablse need to be created once and may not chance
	private final String someOtherVariable;

	// So your constructor would be:
	public SomeClass(String someOtherVariable){
		this.someOtherVariable = someOtherVariable;
	}

	// Always use method to return, never expose the internal representation
	public String getSomeVariable(){
		return someVariable;
	}

	// NEVER do this. Static methods are floating, don't know whats happening and a bad programming habbit
	public static String myStaticMethod(){
		return "Hello!";
	}
}
```
