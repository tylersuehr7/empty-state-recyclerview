# Empty State RecyclerView
A highly customizable subclass of RecyclerView that draws state displays for loading and empty content states.

Easily allows you to handle displaying stateful information (like content is loading, content is empty, content loading had failed, or content loaded ok) to the user in a fun and awesome way!

<img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_anim.gif" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_single_list.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_list.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_card.png" width="200">

<img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_empty.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_error.png" width="200">

Here's some of the core features of this library:
* Can show something interesting and cool while data is loading in the background
* Can show something if data fails to load or is empty
* Very lightweight, memory, and processing efficient as opposed to other alternatives
* Very customizable

How to use it...

In your project level build.gradle :
```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
} 
```

In your app level build.gradle:
```java
dependencies {
    compile 'com.github.tylersuehr7:empty-state-recyclerview:1.0.4'
}  
```

## Using the Empty State RecyclerView
The basic usage of this library is to distract the user while data is being fetched/loaded in the background. The user can be distracted with text or a neat animation that is customizable to your liking

### Using in an XML layout
`EmptyStateRecyclerView` can be used in any ViewGroup and supports all width and height attributes. Simple usage is shown here:
```xml
<com.tylersuehr.esr.EmptyStateRecyclerView
        android:id="@+id/chips_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

### Using in Java code
`EmptyStateRecyclerView` can be programmatically added into any ViewGroup. Simple usage in an Activity is shown here:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    EmptyStateRecyclerView rv = new EmptyStateRecyclerView(this);
    // Set any states for the recycler view
    
    setContentView(rv);
}
```

Here are the available states for the `EmptyStateRecyclerView`

Constant Name | Type |  Constant Value
--- | :---: | ---
`STATE_LOADING`| `byte` | 0
`STATE_EMPTY` | `byte` | 1
`STATE_ERROR` | `byte` | 2
`STATE_OK` | `byte` | 3

Here is a table of all the accessible attributes available for this view:

Method | Summary
--- | ---
`setStateDisplay(byte, StateDisplay)` | Sets a state display for the given state.
`setStateDisplays(byte[], StateDisplay[])` | Sets state displays for all the given states. 
`removeStateDisplay(byte)` | Removes a state display for a given state.
`clearStateDisplays()` |  Removes all state displays for all states.
`invokeState(byte)` | Invokes the given state, ensuring that the proper state display is shown.
`isEmptyState()` | True if the current invoked state is the empty state.
`isErrorState()` | True if the current invoked state is the error state.
`isLoadingState()` | True if the current invoked state is the loading state.
`isOkState()` | True if the current invoked state is the ok state.
`setOnStateChangedListener(OnStateChangedListener)` | Sets an observer for state changes.
`getOnStateChangedListener()` | Gets the observer watching state changes.

## Making Custom State Displays
Writing custom state displays are simple and easy!

`EmptyStateRecyclerView` has an associated internal interface called, `StateDisplay`, that it uses to draw states to the screen. Create a specialization of `StateDisplay` and implement its `onDrawState(EmptyStateRecyclerView, Canvas)` method; where you can use any kind of drawing logic on the Android `Canvas` API.

There is a base semi-concrete `StateDisplay` implementation in the repository that you can use if you want called, `AbstractStateDisplay`. This just stores padding that can be used while drawing in any subclasses of it.

Here is a simple example:
```java
public class ExampleEmptyStateDisplay extends AbstractStateDisplay {
    // ...any properties needed to perform drawing logic
        
    @Override
    public void onDrawState(EmptyStateRecyclerView rv, Canvas canvas) {
            // ... any cool drawing logic on the Canvas
    }
}
```

### Using your custom state display
You can easily use your custom state display by setting the display for the state that you want the `EmptyStateRecyclerView` to show it for when invoked for that state. Here's a simple example using the custom state display above:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    EmptyStateRecyclerView rv = new EmptyStateRecyclerView(this);
    
    // Set your custom state display for the desired state
    rv.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY, new ExampleEmptyStateDisplay());
    rv.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
    
    setContentView(rv);
}
```
