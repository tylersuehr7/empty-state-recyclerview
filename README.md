# Empty State RecyclerView
*Updates to docs and repository are still in progress*

A highly customizable subclass of RecyclerView that draws state displays for loading and empty content states.

Easily allows you to handle displaying stateful information (like content is loading, content is empty, content loading had failed, or content loaded ok) to the user in a fun and awesome way!

<img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_anim.gif" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_single_list.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_list.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_card.png" width="200">

<img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_empty.png" width="200"> <img src="https://github.com/tylersuehr7/empty-state-recyclerview/blob/master/docs/screen_error.png" width="200">

Here's some of the core features of this library:
* Can show something interesting and cool while data is loading in the background
* Can show something if data fails to load or is empty
* Very lightweight, memory, and processing efficient as opposed to other alternatives
* Very customizable

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
