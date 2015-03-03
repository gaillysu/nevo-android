# README #

### What is this repository for? ###

This is the repository of Nevo-Android version

### Contribution guidelines ###

#### 1-Naming conventions :
Variable :
    /**
     * Comment goes here 
     */
    private boolean mIsJavaCamera = true;  (all Object variables should be private or (default) and start with an m.
If you want to get or set a variable, create a getter or setter)

Constant :
public static int NAME_OF_VARIABLE

Class :
​public class NameOfClass {

}​
or
​/*package*/ class NameOfClass {

}
​(Why : Because if all variables are private, a variable won't be changed without your consent)​


####  2- Optionals​

https://developer.apple.com/library/ios/documentation/Swift/Conceptual/Swift_Programming_Language/TheBasics.html#//apple_ref/doc/uid/TP40014097-CH5-ID330

null is a big big plague in Java.

So no variable should ever be null. Except inside an Optional.

Since android doesn't have optionals, use the following class :

/**
 * Concept stolen from Java 8, it is a convenience method to warn that this object of type T can be null
 * @author Hugo
 *
 * @param <T>
 */
public class Optional<T> {
	
	/**
	 * The instance contained in this Optional, this can be null
	 */
	protected T mObject;

	/**
	 * Creates a new Optionnal, Warning, it's content is null !
	 */
	public Optional() {

	}
	
	/**
	 * Creates a new Optionnal with an object to initialise it
	 * @param object
	 */
	public Optional(T object){
		mObject = object;
	}
	
	/**
	 * Don't try to get this object before checking carefully if it is null or not !
	 * @return a non-null object
	 */
	public T get(){
		if(isEmpty()) throw new NullPointerException();
		return mObject;
	}
	
	/**
	 * Sets this Optional, the new value can be null
	 * @param object
	 */
	public void set(T object){
		mObject = object;
	}
	
	/**
	 * @return true if this Optional is currently empty
	 */
	public boolean isEmpty(){
		return mObject == null;
	}
	
	/**
	 * @return true if this Optional is currently not empty
	 */
	public boolean notEmpty(){
		return !isEmpty();
	}
	
}


####  3- Everything which have a close relation to the UI, should be inside a View
Ex This shouldn't be inside a Controller :
        var titleLabel:UILabel = UILabel(frame: CGRectMake(0, 0, 120, 30))
        titleLabel.textColor = UIColor.whiteColor()
        titleLabel.text = NSLocalizedString("stepGoalTitle", comment: "")
        titleLabel.font = UIFont.systemFontOfSize(25)
Everything that starts with UIXXXXX or ColorXXX should be done inside a View
(Why ? UI changes often, Controller much less often. So every thigns specific to teh appearance. Colors, Fonts, positions. Should be isolated from the rest of the code)
​
#### 4- TODO
It is ok to leave //TODO in the code, but you should write your name on it
ex :

//TODO by Hugo

(Why ? So when I publish, I will contact you and check if we can publish even with this TODO​)

#### 7-Log
When you do some tests, it's ok to put some Log.v everywhere. Just don't forget to remove them when your have finished
Or keep them , but make them very explicit
This is ok :
Error : The connection has timed out unexpectedly. for peripheral : Nevo

This is not ok :
Log.v("AAA","end");
Log.v("AAA","begin");

### Tips to a cleaner code ###

#### 1-Variables is your worst enemy
Use as little variables as you can.
Because most bugs happen when a variable stores a value that is not compatible with another value
And there's a conflict.
The more variables you have, the more different situations can happen.
One boolean vairable -> two different situations possible
Three boolean variables -> 8 different situations possible

#### 2-Short classes
Ideally, a class should be less than 300 lines
600 is the real maximum. Above that, it means you have probably made a mistake

#### 3-No bug policy
As long as there are known bugs. Don't write new code, fix all the bugs first (Nevo only)
In case you commit something with a known bug, write a //TODO by yourName fix this bug (then explain the bug)
Never start a new task as long as there are known remaining bugs

#### 4- Write in 3 steps
Writing codes should come in 3 steps.
First, you think about your structure.

You create different folders and classes. But all classes should be interfaces or empty classes.

Then you make the code work. You do all the efforts necessary to have you code functionnal.

Then you take the time you need to look carefully at your code and you re-write it in a clean way.

Nobody writes clean code the first time. It takes some time to re-read and clean your code.

Only when it's clean, you can start testing.

Ideally you should take :
10% time thinking the structure

30% time writing

10% time cleaning

50% testing

(some company do 20% creating bugs 80% chasing bugs. We don't ;-) )

#### 4- Commit every day
Commit at least once a day.

At best 3 times a day.

If you have separate functions, do separate commits and write a different comment for each commit.

#### 5- Comment a lot
Comment you code A LOT. (and in English)

It doesn't matter what you write in the comments.

Even if there's a lot of mispellings and some smiley :-) , no worries. But there should be nearly one line out of 6 as a comment.

### Who do I talk to? ###

* Repo owner or admin
hugo@five-doors.com