# QDWizard
**_Why should it be more complicated ?_**
* QDWizard is a dead simple API for creating Swing wizards with a learning curve of less than 15 minutes.
* It is released under the [LGPL V2.1](http://www.gnu.org/licenses/lgpl-2.1.html) license
* This library follows [semantic versioning scheme](http://semver.org/) from version 4.0.0

![](qdwizard.jpeg)

# Download
* Zipped source, javadoc and jar are available from the [Maven central repository](http://search.maven.org/#search|ga|1|a%3A%22qdwizard%22)

# Features
* Designed to minimize required code. Only few methods to implement.
* Simple design, only two classes visible to the programmer.
* None dependency.
* Maven powered : QDWizard is available from Maven central repository as artifact (net.sf.qdwizard:qdwizard).
* I18n support for action buttons.
> see [this class](https://github.com/bflorat/qdwizard/blob/master/src/main/java/org/qdwizard/Langpack.java) to get the list of natively supported languages. It is still possible to add manually your own langpack.
* Full branching support, can implement any wizard navigation.
* Error management using the simple setProblem() method.
* Supports Wizard images natively and resizes image automatically.
* Ultra light API: only few KB.
* Actively supported by the Jajuk team.
* Learning curve of 15 minutes maximum.
* Real world samples provided, see [jajuk classes](https://github.com/jajuk-team/jajuk/blob/master/jajuk/src/main/java/org/jajuk/ui/wizard/digital_dj/DigitalDJWizard.java).

#Requirements
* To use QDWizard, just add the qdwizard-[release].jar to your CLASSPATH
* JRE 1.5 and above.

#Manual
## Concepts
* A wizard is build of several screens. 
 * The wizard provides the screens cinematic (by setting the screen that's after or before each screen) and the code to be run at the end of the wizard (through `finish()` method).
 * The screens contains the GUI code and the business logic (can we go next ? for instance).
* Previous and next screens decision is taken by the wizard class.
* Wizard data is stored in a map named 'data'. It is accessible from Wizard and Screen class. Store and get options selected by the user here.

### Wizard creation
* Create a class that extends Wizard. You'll have to implement `getPreviousScreen()`, `getNextScreen()` and `finish()` abstract methods.
* Instantiate the wizard using its fluent builder (only the constructor arguments of the Builder class are mandatory, all the others methods like `icon()` are optional):
````java
MyWizard wizard = new Wizard(new Wizard.Builder("wizard name", ActionSelectionPanel.class,window)
   .hSize(600).vSize(500).locale(LocaleManager.getLocale()).icon(anIcon)
   .headerBackgroundImage(backgroundImage).leftSideImage(leftSideImage));
````
* The builder arguments are : 
 * [mandatory] The wizard name displayed as title of the dialog
 * [mandatory] The initial screen class
 * [mandatory] The parent window
 * `hSize()` method set the horizontal size (in pixels). Default is 700.
 * `vSize()` method set the vertical size (in pixels). Default is 500.
 * `locale()` method set the locale to use. If provided locale is not supported, English is used.
 * `icon()` method set the header left-side icon
 * `headerBackgroundImage()` adds an auto-resized image as header's background
 * `leftSideImage()` adds an image to be displayed as a left side panel. This image is automatically extended to fit all the available space. To avoid letting users to see this image being resized at first display, make sure to create an image than has the right dimension out of the box.
* `finish()` method implements the actions to be done at the end of the wizard.
* `getPreviousScreen()` and `getNextScreen()` have to return previous or next screen class :

````java
public Class getNextScreen(Class screen) {
	if (ActionSelectionPanel.class.equals(getCurrentScreen())){
	  if (...){
	     return TypeSelectionPanel.class;
	  }
	  else {
	     return RemovePanel.class;
	  }
	}
}
````
* `show()` displays the wizard.

### Screen creation
* For each wizard page, create a public Screen class. You have to implement `initUI()`, `getDescription()` and `getName()` abstract methods.
* `getName()` method should return the step name and getDescription() the step description (return `null` if none description required).
* `initUI()` method contains graphical code for your screen. This method is automatically called from screen constructor, don't call it by yourself.

### Data sharing between screens and wizard
Get and set wizard data using the `data` map available from wizard and screen classes. This is a `HashMap<Object,Object>` so you can use anything as a key or a value. 
A good practice is to create an enum in the Wizard class and use to enum entry as key for the data map :
````java
public Class MyWizard extends Wizard {
	enum Variable {VARIABLE_1,VARIABLE_2}
	...
	void someMethod(){
		data.put(Variable.VARIABLE1,"a String");
	}
}

public Class MyScreen extends Screen {
	void someMethod(){
		String var1 = data.get(Variable.VARIABLE_1);
	}
}
````
## General use
* It is a good practice to create wizards and their associated screens in the same package. It's also better to implement screens in different java files.
* Set errors using the `setProblem(String)` method. The error will be displayed in red at the bottom of the screen. When error is fixed, use a `setProblem(null)` to remove it.
* Use `setCanFinish(true)` method in a screen to state the fact that this screen is the last one (user can click on Finish button).

##Advanced topics
* The Screen class contains two empty methods `onEnter()` and `onLeave()` which are called by the wizard respectively on entering and before leaving the screen. You can override them to add specific behaviors. Note that this happens only in forward mode, which means that `onEnter()` won't be called when you return to a screen via the previous button and that `onLeave()` won't be called when you leave the screen via the previous button.
* By default, QDwizard keeps the screens (and its widgets models) into memory so user can go previous or next and keep typed values. If you want to clear this cache, use the `ClearPoint` annotation against your screen(s) classes. When user reaches a screen that use this annotation, the screens cache is cleaned up and the screens `initUI()` is called.
* By default, the Cancel button just close the wizard window. You can implement a `Wizard.onCancel()` method which will be called when the user presses the Cancel button. This method should should return true to close the window or false if you want to keep it opened.
* You can come with you own langpack if it is not provided natively by QDWizard or you can override an existing langpack using the `Langpack.addLocale()` method.
* [from 4.2.0] You can call the `Screen.setCanGoNext(boolean)`, `Screen.setCanGoPrevious(boolean)`,`Screen.setCanCancel(boolean)` and `Screen.setCanFinish(boolean)` from your screen class as a way to force disabling of "Previous","Next", "Cancel" or "Finish" buttons on certain events.
* [from 4.2.0] You can call the `Wizard.forceNextScreen()`, `Wizard.forcePreviousScreen()`, `Wizard.forceCancel()` and `Wizard.forceFinish()` to force programmatically the screen actions (without actual user clicking on the buttons). Beware that this action still follows the state of the screen (canGoNext, canGoPrev, canCancel, canFinish) and will have no effect if this condition is not fulfilled. The same methods are available from the `Screen` classes.

##Samples
Have a look at [the Jajuk DJ wizard](https://github.com/jajuk-team/jajuk/blob/master/jajuk/src/main/java/org/jajuk/ui/wizard/digital_dj/DigitalDJWizard.java)

##Javadoc
Check http://bflorat.github.io/qdwizard/apidocs/

# History
* 2015/04/10: [5.0.0] (some methods removed)
 * Several bugfixes, check https://github.com/bflorat/qdwizard/issues
 * Added programmatical actions on wizards and screens like `forceNextScreen()` (thx Boformer)
 * `setCanGoNext()` and `setCanGoPrevious()` methods are now public and be overriden in screens (thx Boformer)
 * `Screen.onCancelled()` and `Screen.onFinished()` methods removed (should use `Wizard.onCancel()` and `Wizard.finish()` instead))
 * `Screen.onNext()` method has been replaced by the `onLeave()` method.
* 2014/03/28: [4.1.0] (backward compatible)
 * It is now possible to add or override a langpack using the `Langpack.addLocale()` method
* 2014/03/28: [4.0.0] (backward compatibility slightly broken at runtime only)
 * The Wizard.setScreen() method now throws an `IllegalArgumentException` if provided screen is wrong or not accessible
 * javadoc augmentation and fixes
 * Version update due to semantic versioning compliance from this version
* 2014/03/28: [3.1.2] (backward compatible) 
 * data is now a `HashMap<Object,Object>`, no more a `HashMap<String,Object>` to allow using enums as data entries
 * Code and javadoc cleanup  
* 2014/03/25: [3.1.1] (backward compatible) 
 * Fixes a regression on the clearpoint annotation that has been broken in 3.1
 * 2014/03/25: [3.1] (backward compatibility slightly broken) 
 * `ClearPoint` interface is now an annotation
* 2013/07/14: [3.0] (backward compatibility broken) 
 * Builder pattern to build a wizard to make the instantiation much easier and cleaner
 * Moved the project to GitHub
 * Code cleanup
* 2009/02/22: [2.1] Enhancements and fixes
 * Fixed an issue preventing right previous/next buttons state display
 * Added a `setHeaderIcon()` method that allow to display an icon at the right upper side of the header
 * Code cleanup
 * Removed deprecated methods `Wizard.cancel()` and `Screen.onLeave()` methods
 * Fixed painting issues with some look and feels (like substance) 
* 2008/03/26: [2.0.4] Cleanup
 * Code spelling and formatting
 * Fixed a potential flaw in `ScreenStatus` class
* 2008/03/18: [2.0.3] Enhancements
 * Added Galician support
* 2007/12/22: [2.0.2] Fixes
 * Fixed ant script that depends on my own path
 * Added Russian support
* 2007/12/21: [2.0.1] Fixes
 * Fixed broken compatibility with previous Wizard constructors to ensure upward compatibility
 * Improved javadoc documentation, especially in classes headers
 * Code cleanup
* 2007/12/20: [2.0] Enhancements
 * QDWizard as maven project
 * New project website
 * More stack traces for debug
* 2007/01/02: [1.9] Fixes
 * Timer should be stopped when pressing finish or cancel
 * Code cleanup
* 2006/09/04: [1.7] Fixes and enhancements
 * Zip Packaging fix (extract to a qdwizard directory)
 * Header thinner
 * Wizard is now displayed using an external `show()` call
* 2006/08/09: [1.6] Enhancements
 * i18n with property resource bundles
 * New notification methods `onEnter()` and `onLeave()` in Screen class
* 2006/07/24: [1.5] Enhancements
 * Wizards are now fully resizable (including left-side picture)
 * No more dependencies to TableLayout
* 2006/07/08: [1.4] Fixes and enhancements
 * German langpack fixes
 * Renamed WizardCleaner interface to `ClearPoint`
 * Added arrows in Next and Previous buttons
 * QDWizard project is now fully autonomous from Jajuk
* 2006/06/22: [1.3] Fixes and enhancements
 * Keep screens memory
 * Added WizardClearer stuff to reset screens history
* 2006/05/22: [1.2] Fixes and enhancements
 * Screens are no more cached to allow user value changes
 * Locale can now be set explicitly (no more uses only the default locale)
 * Doc screenshot fix
* 2006/05/19: [1.1] Fixes
 * [FATAL] Fixed wrong jajuk Main class dependency
 * Fixed wizard size issue if screen too small
 * Do not display 'null' if screen description is not provided
 * Fixed documentation and javadoc
* 2006/05/14: [1.0] Initial release
